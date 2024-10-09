package idv.tia201.g1.order.service.impl;

import idv.tia201.g1.chat.dao.ChatParticipantDao;
import idv.tia201.g1.chat.dto.MessageDTO;
import idv.tia201.g1.chat.service.CacheService;
import idv.tia201.g1.chat.service.ChatService;
import idv.tia201.g1.core.entity.UserAuth;
import idv.tia201.g1.core.utils.UserHolder;
import idv.tia201.g1.order.dao.OrderDao;
import idv.tia201.g1.order.dao.OrderDetailDao;
import idv.tia201.g1.order.dto.OrderDTO;
import idv.tia201.g1.order.dto.OrderProductDTO;
import idv.tia201.g1.order.dto.PaymentRequest;
import idv.tia201.g1.order.dto.PaymentResponse;
import idv.tia201.g1.order.entity.Order;
import idv.tia201.g1.order.service.OrderService;
import idv.tia201.g1.order.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;

import static idv.tia201.g1.core.utils.Constants.ROLE_USER;
import static idv.tia201.g1.core.utils.Constants.WEB_ORDER_PREFIX;

@Service
public class PaymentServiceImpl implements PaymentService {
    private static final String PAY_URL = "https://sandbox.tappaysdk.com/tpc/payment/pay-by-prime";
    private static final int PAY_STATE_SUCCESS = 0;

    @Value("${payment.partner-key}")
    private String PARTNER_KEY;
    @Value("${payment.merchant-id}")
    private String MERCHANT_ID;
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private OrderDetailDao orderDetailDao;
    @Autowired
    private ChatService chatService;
    @Autowired
    private ChatParticipantDao chatParticipantDao;
    @Autowired
    private CacheService cacheService;
    @Autowired
    private OrderService orderService;

    @Override
    public PaymentResponse processPayment(Integer orderId, PaymentRequest paymentRequest) {
        // 驗證登入人身份
        UserAuth loginUser = UserHolder.getUser();
        if (loginUser == null || !ROLE_USER.equals(loginUser.getRole())) {
            throw new IllegalStateException("登入狀態異常：未登入或是不符合的使用者身份");
        }

        // 驗證前端傳遞的格式是否符合
        validatePaymentRequest(paymentRequest);

        // 取得訂單物件
        Order order = orderDao.findByOrderId(orderId);

        // 驗證訂單是否存在 與 訂單所有人與登入者是否相符
        if (order == null || !Objects.equals(order.getUserId(), loginUser.getId())) {
            throw new IllegalStateException("訂單狀態異常：訂單不存在或不屬於該客戶");
        }

        // 將訂單細節與編號補充填入請求物件
        populatePaymentRequest(paymentRequest, order);

        // 將請求物件傳遞到支付網站
        PaymentResponse response = callPaymentService(paymentRequest);

        // 根據支付網站回傳的結果進行訂單操作 (改寫訂單狀態... etc)
        processPaymentResponse(order, response);

        return response;
    }

    /**
     * 傳遞支付請求到支付網站並等待回應
     *
     * @param paymentRequest 經過後端處理的請求物件
     * @return 支付網站的回應物件
     */
    private PaymentResponse callPaymentService(PaymentRequest paymentRequest) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-api-key", PARTNER_KEY);
        HttpEntity<PaymentRequest> requestEntity = new HttpEntity<>(paymentRequest, headers);

        try {
            ResponseEntity<PaymentResponse> responseEntity = restTemplate.exchange(
                    PAY_URL,
                    HttpMethod.POST,
                    requestEntity,
                    PaymentResponse.class
            );
            return responseEntity.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new IllegalStateException("支付異常: 網路異常，如已扣款請聯繫客服或支付平台處理。");
        } catch (Exception e) {
            throw new IllegalStateException("支付異常: 發生未知錯誤", e);
        }
    }

    /**
     * 根據回應處理訂單
     *
     * @param order           訂單物件
     * @param paymentResponse 支付網站的回應物件
     */
    private void processPaymentResponse(Order order, PaymentResponse paymentResponse) {
        if (paymentResponse == null) {
            throw new IllegalStateException("支付異常: 支付網站服務異常，如已扣款請聯繫客服或支付平台處理。");
        }

        if (paymentResponse.getStatus() != PAY_STATE_SUCCESS) {
            // 付款不成功, 不進行後續操作
            throw new IllegalStateException("支付失敗: 請稍後重試。");
        }

        Date endDate = order.getEndDate();
        Timestamp endTimestamp = new Timestamp(endDate.getTime());
        order.setPayStatus(paymentResponse.getRec_trade_id());
        orderDetailDao.updateExpiredTimeByOrderId(order.getOrderId(),endTimestamp);
        Order saved = orderDao.save(order);

        // 付款完成: 發送系統提示訊息
        sendOrderSysMessage(saved.getOrderId());
    }

    private void sendOrderSysMessage(Integer orderId) {
        OrderDTO orderDTO = orderService.getOrder(orderId);

        Date startDate = orderDTO.getStartDate();
        Date endDate = orderDTO.getEndDate();
        String hotelName = orderDTO.getHotelName();

        List<OrderProductDTO> products = orderDTO.getProducts();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

        // 創建訊息
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("您已成功預定")
                .append(sdf.format(startDate))
                .append("至")
                .append(sdf.format(endDate))
                .append(hotelName)
                .append("：\n");

        // 拼接房間名稱和數量，使用「、」分隔
        messageBuilder.append("{");
        for (int i = 0; i < products.size(); i++) {
            OrderProductDTO product = products.get(i);
            String productName = product.getProductName();
            Integer quantity = product.getQuantity();

            messageBuilder.append(productName).append(" ").append(quantity).append(" 間");

            // 在非最後一個產品後添加 "、"
            if (i < products.size() - 1) {
                messageBuilder.append("、");
            }
        }
        messageBuilder.append("}\n");

        messageBuilder.append("期待讓您有場愉快放鬆的美妙旅程，")
                .append("若有問題歡迎回覆此訊息，我們將在2小時內提供回覆。\n")
                .append("期待您的入住！");

        // 封裝為聊天室的系統訊息
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setSenderId(1L);
        messageDTO.setContent( messageBuilder.toString());
        messageDTO.setTimestamp(new Timestamp(System.currentTimeMillis()).toString());

        // 取得登入者編號
        Long loginUserId = chatService.getOrCreateMappingUserId(UserHolder.getRole(), UserHolder.getId());
        // 取得登入者與系統訊息的聊天室編號
        Long chatId = chatParticipantDao.findChatIdByTwoUserIds(loginUserId, 1L);

        // 假設你要將這個訊息保存
        cacheService.saveMessage(1L, chatId, messageDTO);
    }

    /**
     * 檢查支付請求物件是否符合格式
     *
     * @param paymentRequest 前端發送過來的請求物件
     */
    private void validatePaymentRequest(PaymentRequest paymentRequest) {
        if (paymentRequest == null) {
            throw new IllegalArgumentException("支付異常: 請確保網路通信正常以及欄位填寫完整");
        }

        // 驗證prime
        if (!StringUtils.hasText(paymentRequest.getPrime())) {
            throw new IllegalArgumentException("支付異常: 請確保網路通信正常以及欄位填寫完整");
        }

        // 驗證信用卡必填欄位
        PaymentRequest.Cardholder cardholder = paymentRequest.getCardholder();
        if (cardholder == null) {
            throw new IllegalArgumentException("支付異常: 信用卡欄位填寫不完整");
        }
        if (!StringUtils.hasText(cardholder.getPhone_number()) ||
                !StringUtils.hasText(cardholder.getName()) ||
                !StringUtils.hasText(cardholder.getEmail())) {
            throw new IllegalArgumentException("支付異常: 信用卡欄位填寫不完整");
        }
    }


    /**
     * 將資訊補充填入支付請求物件中
     *
     * @param paymentRequest 支付請求物件
     * @param order          訂單物件
     */
    private void populatePaymentRequest(PaymentRequest paymentRequest, Order order) {
        paymentRequest.setPartner_key(PARTNER_KEY);  // 使用從配置中讀取的值
        paymentRequest.setMerchant_id(MERCHANT_ID);  // 使用從配置中讀取的值
        paymentRequest.setOrder_number(WEB_ORDER_PREFIX + order.getOrderId());
        paymentRequest.setAmount(order.getActualPrice());
        paymentRequest.getCardholder().setMemberId(String.valueOf(order.getUserId()));
    }
}
