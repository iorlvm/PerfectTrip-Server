package idv.tia201.g1.chat.service.impl;

import com.google.gson.Gson;
import idv.tia201.g1.authentication.service.UserAuth;
import idv.tia201.g1.chat.dao.ChatMessageDao;
import idv.tia201.g1.chat.dao.ChatParticipantDao;
import idv.tia201.g1.chat.dao.ChatRoomDao;
import idv.tia201.g1.chat.dao.ChatUserMappingDao;
import idv.tia201.g1.chat.event.UserUpdateEvent;
import idv.tia201.g1.chat.service.ChatService;
import idv.tia201.g1.dto.*;
import idv.tia201.g1.entity.ChatMessage;
import idv.tia201.g1.entity.ChatParticipant;
import idv.tia201.g1.entity.ChatRoom;
import idv.tia201.g1.entity.ChatUserMapping;
import idv.tia201.g1.utils.DtoConverter;
import idv.tia201.g1.utils.UserHolder;
import idv.tia201.g1.utils.redis.RedisIdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static idv.tia201.g1.utils.Constants.*;

@Service
public class ChatServiceImpl implements ChatService {
    private final static Gson gson = new Gson();

    @Autowired
    private ChatUserMappingDao chatUserMappingDao;
    @Autowired
    private ChatParticipantDao chatParticipantDao;
    @Autowired
    private ChatRoomDao chatRoomDao;
    @Autowired
    private ChatMessageDao chatMessageDao;
    @Autowired
    RedisIdWorker idWorker;
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Override
    public void updateUserInfo(UserAuth userAuth) {
        if (userAuth == null) throw new IllegalArgumentException("參數錯誤：沒有傳入任何使用者");

        String role = userAuth.getRole();
        Integer id = userAuth.getId();

        Long loginUser = findMappingUserId(role, id);
        if (loginUser == null) {
            // 沒使用過聊天室, 不需要做任何事情
            return;
        }

        PayloadDTO payloadDTO = new PayloadDTO();
        payloadDTO.setAuthorId(loginUser);
        payloadDTO.setTimestamp(Timestamp.from(Instant.now()).toString());
        payloadDTO.setAction(CHAT_ACTION_UPDATE_USER_INFO);

        // 轉寫userAuth為dto物件, 並轉成json格式放入內容中
        PayloadDTO.UserInfo userInfoDTO = DtoConverter.toUserInfoDTO(userAuth);
        payloadDTO.setContent(gson.toJson(userInfoDTO));

        eventPublisher.publishEvent(new UserUpdateEvent(this, payloadDTO));
    }


    @Override
    public ChatRoomDTO initChatRoom(Set<UserIdentifier> users) {
        // 檢查參數
        if (users == null || users.isEmpty()) {
            throw new IllegalArgumentException("參數錯誤：沒有傳入任何使用者");
        }

        // 取得登入用戶資料
        String type = UserHolder.getRole();
        Integer id = UserHolder.getId();

        if (type == null || id == null)
            throw new IllegalStateException("不合法的訪問: 請檢查您的登入狀態");

        // 如果只有一個使用者，檢查是否為當前登入用戶
        if (users.size() == 1) {
            UserIdentifier singleUser = users.iterator().next();
            if (type.equals(singleUser.getType()) && id.equals(singleUser.getId())) {
                throw new IllegalArgumentException("不能創建與自己單獨的聊天室");
            }
        }

        Long loginUserId = getOrCreateMappingUserId(type, id);

        // 創造聊天室
        ChatRoom chatRoom = createChatRoom();
        Long chatId = chatRoom.getChatId();

        // 創造參與者列表
        List<ParticipantDTO> participantDTOS = new ArrayList<>(users.size() + 1);
        // 將登入者寫入資料庫
        ChatParticipant loginUser = addParticipantToChatRoom(chatId, loginUserId);
        // 登入者同時也是參與者, 也要一起加入列表之中
        participantDTOS.add(DtoConverter.toParticipantDTO(loginUser));

        // 遍歷傳入的使用者
        for (UserIdentifier user : users) {
            // 取得或建立映射id
            Long mappingUserId = getOrCreateMappingUserId(user.getType(), user.getId());

            // 將參與者寫入資料庫
            ChatParticipant chatParticipant = addParticipantToChatRoom(chatId, mappingUserId);
            // 將參與者轉寫成DTO格式, 並存入列表
            participantDTOS.add(DtoConverter.toParticipantDTO(chatParticipant));
        }

        // 將聊天室轉寫成DTO格式, 並將參與者列表存入其中
        ChatRoomDTO chatRoomDTO = DtoConverter.toChatRoomDTO(chatRoom, loginUser);
        chatRoomDTO.setParticipants(participantDTOS);

        return chatRoomDTO;
    }

    @Override
    public Long getOrCreateMappingUserId(String type, Integer id) {
        if (type == null || id == null) {
            throw new IllegalStateException("不合法的訪問: 請檢查您的登入狀態");
        }

        // TODO: 優化為緩存模式
        Long userId = findMappingUserId(type, id);
        if (userId == null) {
            userId = createUserMapping(type, id).getMappingUserId();
        }
        return userId;
    }

    private ChatRoom createChatRoom() {
        Long chatId = idWorker.nextId("chat");
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setChatId(chatId);
        return chatRoomDao.save(chatRoom);
    }

    private ChatParticipant addParticipantToChatRoom(Long chatId, Long mappingUserId) {
        ChatParticipant chatParticipant = new ChatParticipant();
        chatParticipant.setChatId(chatId);
        chatParticipant.setMappingUserId(mappingUserId);
        return chatParticipantDao.save(chatParticipant);
    }

    @Override
    public ChatRoomDTO getChatRoomById(Long chatId) {
        // 取得登入用戶資料
        String type = UserHolder.getRole();
        Integer id = UserHolder.getId();

        if (type == null || id == null)
            throw new IllegalStateException("不合法的訪問: 請檢查您的登入狀態");

        // 根據登入用戶取得映射id
        Long userMappingId = findMappingUserId(type, id);
        if (userMappingId == null) {
            // 映射關係不存在, 表示之前完全沒使用過聊天室 (同時也不可能是這個聊天室的參與者)
            throw new IllegalStateException("非法的請求: 該用戶不是此聊天室的參與者");
        }

        return createChatRoomDTO(chatId, userMappingId);
    }

    @Override
    public Page<ChatRoomDTO> getChatRooms(int page, int size) {
        // 取得登入用戶資料
        String type = UserHolder.getRole();
        Integer id = UserHolder.getId();

        if (type == null || id == null)
            throw new IllegalStateException("不合法的訪問: 請檢查您的登入狀態");

        // 分頁設定
        Pageable pageable = PageRequest.of(page, size);

        // 根據登入用戶取得映射id
        Long userMappingId = findMappingUserId(type, id);
        if (userMappingId == null) {
            // 映射關係不存在, 表示之前完全沒使用過聊天室(不可能存在聊天列表), 回傳一個長度為0的page
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        // 取得聊天室列表
        Page<Long> result = chatParticipantDao.findChatIdByTypeAndRefId(type, id, pageable);
        List<Long> chatIdsForUser = result.getContent();
        List<ChatRoomDTO> chatRoomDTOS = new ArrayList<>(chatIdsForUser.size());

        // 根據列表中的聊天室id取得各個聊天室的參與者
        for (Long chatId : chatIdsForUser) {
            // 利用chatId取得聊天室的詳細資料, 並寫入DTO物件
            ChatRoomDTO chatRoomDTO = createChatRoomDTO(chatId, userMappingId);
            chatRoomDTOS.add(chatRoomDTO);
        }
        // 回傳
        return new PageImpl<>(chatRoomDTOS, pageable, result.getTotalElements());
    }

    private ChatRoomDTO createChatRoomDTO(Long chatId, Long userMappingId) {
        // 利用chatId取得聊天室的詳細資料, 並寫入DTO物件
        ChatRoom chatRoom = chatRoomDao.findById(chatId).orElse(null);
        // TODO: 查詢結果(DTO)放入redis緩存 (鍵:"cache:chatroom:chatId")
        //       先查詢redis, 不存在緩存才重建 (使用互斥鎖方案, 過期消失時間20秒?)
        if (chatRoom == null) {
            throw new IllegalStateException("狀態異常: 沒有對應的聊天室");
        }

        // 取得聊天室中所有的參與者
        List<ChatParticipant> chatParticipants = getChatParticipantsByChatId(chatId);
        List<ParticipantDTO> participantDTOS = new ArrayList<>(chatParticipants.size());

        ChatParticipant loginChatUser = null;
        for (ChatParticipant chatParticipant : chatParticipants) {
            // 將chatParticipant 轉換成 participantDTO物件
            if (loginChatUser == null && userMappingId.equals(chatParticipant.getMappingUserId())) {
                // 搜尋當下登入的使用者
                loginChatUser = chatParticipant;
            }
            ParticipantDTO participantDTO = DtoConverter.toParticipantDTO(chatParticipant);
            participantDTOS.add(participantDTO);
        }
        if (loginChatUser == null) {
            throw new IllegalStateException("非法的請求: 該用戶不是此聊天室的參與者");
        }

        // 將詳細資料寫入DTO物件
        ChatRoomDTO chatRoomDTO = DtoConverter.toChatRoomDTO(chatRoom, loginChatUser);
        chatRoomDTO.setParticipants(participantDTOS);
        return chatRoomDTO;
    }

    @Override
    public MessageDTO sendMessage(Long chatId, MessageDTO messageDTO) {
        // 檢查是否有發送任何訊息
        if (messageDTO.getContent() == null && isImageEmpty(messageDTO.getImg())) {
            throw new IllegalArgumentException("請求異常：沒有發送任何內容");
        }

        // 檢查聊天室是否存在 (不存在 訪回異常狀態)
        if (isChatRoomValid(chatId)) {
            throw new IllegalArgumentException("參數異常: 聊天室不存在");
        }

        // 利用登入使用者id以及type, 獲取senderId
        Integer id = UserHolder.getId();
        String type = UserHolder.getRole();

        Long senderId = findMappingUserId(type, id);
        if (senderId == null || isParticipantNotFound(senderId, chatId)) {
            // 映射關係不存在, 表示之前完全沒使用過聊天室
            // 或經過檢查以後發現不是合法的參與者
            throw new IllegalStateException("非法的請求: 該用戶不是此聊天室的參與者");
        }

        return saveMessage(senderId, chatId, messageDTO);
    }

    private MessageDTO saveMessage(Long senderId, Long chatId, MessageDTO messageDTO) {
        Long messageId = idWorker.nextId("message");
        Timestamp now = Timestamp.from(Instant.now());

        // 將資料寫入chatMessage(Entity物件)
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setMessageId(messageId);
        chatMessage.setMappingUserId(senderId);
        chatMessage.setChatId(chatId);
        chatMessage.setContent(messageDTO.getContent());

        MessageDTO.ImageDTO img = messageDTO.getImg();
        if (!isImageEmpty(img)) {
            chatMessage.setImg(img.getSrc());
        }

        chatMessage.setCreatedDate(now);
        chatMessage.setLastModifiedDate(now);

        // 將chatMessage存入資料庫
        // TODO: 先將chatMessage存入Redis, 後續實現消息對列異步存入資料庫
        chatMessageDao.save(chatMessage);
        // TODO: 對所有的其他參與者增加未讀數量 (未實現)
        // TODO: 更新聊天室的最後訊息以及, 最後訊息時間

        // 將資料寫入messageDTO(DTO物件)
        messageDTO.setMessageId(messageId);
        messageDTO.setSenderId(senderId);
        messageDTO.setTimestamp(now.toString());

        return messageDTO;
    }

    @Override
    public List<MessageDTO> getMessages(long chatId, Long messageId, int size) {
        // 利用messageId的自增以及唯一性, 可以準確地往上抓取一定數量的資料
        if (isChatRoomValid(chatId)) {
            throw new IllegalArgumentException("參數異常: 聊天室不存在");
        }
        // 分頁設定
        Pageable pageable = PageRequest.of(0, size);

        // TODO: 未來調整成訊息異步寫入的話, 這裡也要增加相應的調整
        List<ChatMessage> messages = chatMessageDao.findByChatIdAndMessageIdLessThan(chatId, messageId, pageable);
        List<MessageDTO> messageDTOS = new ArrayList<>(messages.size());

        for (ChatMessage message : messages) {
            MessageDTO messageDTO = DtoConverter.toMessageDTO(message);
            messageDTOS.add(messageDTO);
        }

        return messageDTOS;
    }

    @Override
    public List<ChatParticipant> getChatParticipantsByChatId(Long chatId) {
        // TODO: 優化為緩存的形式
        return chatParticipantDao.findByChatId(chatId);
    }

    @Override
    public Set<Long> getChatRoomsIdByRoleAndId(String role, Integer id) {
        return chatParticipantDao.findChatIdByTypeAndRefId(role, id);
    }

    @Override
    public PayloadDTO handlePayload(String role, Integer id, PayloadDTO payloadDTO) {
        String action = payloadDTO.getAction();
        // 驗證action值是否合法
        switch (action) {
            case CHAT_ACTION_SEND_MESSAGE:
            case CHAT_ACTION_READ_MESSAGE:
            case CHAT_ACTION_UPDATE_ROOM_INFO:
            case CHAT_ACTION_UPDATE_NOTIFY:
            case CHAT_ACTION_UPDATE_PINNED:
                break;
            default:
                throw new IllegalArgumentException("參數異常: 未定義的操作");
        }

        Long chatId = payloadDTO.getChatId();
        // 驗證chatId是否存在
        if (isChatRoomValid(chatId)) {
            throw new IllegalArgumentException("參數異常: 聊天室不存在");
        }

        Long authorId = getOrCreateMappingUserId(role, id);
        // 驗證authorId是否是這個chatId的參與者
        if (isParticipantNotFound(authorId, chatId)) {
            throw new IllegalStateException("非法的請求: 該用戶不是此聊天室的參與者");
        }

        // 將兇手(?)以及目的地寫入dto中
        payloadDTO.setAuthorId(authorId);
        payloadDTO.setChatId(chatId);

        // 根據action執行對應的操作
        switch (action) {
            case CHAT_ACTION_SEND_MESSAGE:
                processPayloadToSendMessage(payloadDTO);
                break;
            case CHAT_ACTION_READ_MESSAGE:
                processPayloadToReadMessage(payloadDTO);
                break;
            case CHAT_ACTION_UPDATE_ROOM_INFO:
                processPayloadToUpdateRoomInfo(payloadDTO);
                break;
            case CHAT_ACTION_UPDATE_NOTIFY:
                processPayloadToUpdateNotify(payloadDTO);
                break;
            case CHAT_ACTION_UPDATE_PINNED:
                processPayloadToUpdatePinned(payloadDTO);
                break;
        }

        return payloadDTO;
    }

    private void processPayloadToSendMessage(PayloadDTO payloadDTO) {
        String content = payloadDTO.getContent();
        MessageDTO messageDTO = gson.fromJson(content, MessageDTO.class);

        // 檢查是否有發送任何訊息
        if (messageDTO.getContent() == null && isImageEmpty(messageDTO.getImg())) {
            throw new IllegalArgumentException("請求異常：沒有發送任何內容");
        }

        saveMessage(payloadDTO.getAuthorId(), payloadDTO.getChatId(), messageDTO);
        payloadDTO.setContent(gson.toJson(messageDTO));
        payloadDTO.setTimestamp(messageDTO.getTimestamp());
    }

    private void processPayloadToReadMessage(PayloadDTO payloadDTO) {
        Timestamp now = Timestamp.from(Instant.now());
        payloadDTO.setTimestamp(now.toString());
        Long chatId = payloadDTO.getChatId();
        Long authorId = payloadDTO.getAuthorId();
        chatParticipantDao.updateLastReadingAtByChatIdAndMappingUserId(chatId, authorId, now);
    }

    private void processPayloadToUpdateNotify(PayloadDTO payloadDTO) {
        Timestamp now = Timestamp.from(Instant.now());
        payloadDTO.setTimestamp(now.toString());
        Long chatId = payloadDTO.getChatId();
        Long authorId = payloadDTO.getAuthorId();
        String content = payloadDTO.getContent();
        switch (content) {
            case "on":
            case "off":
                break;
            default:
                content = "off";
        }
        chatParticipantDao.updateNotifyByChatIdAndMappingUserId(chatId, authorId, content);
    }

    private void processPayloadToUpdatePinned(PayloadDTO payloadDTO) {
        Timestamp now = Timestamp.from(Instant.now());
        payloadDTO.setTimestamp(now.toString());
        Long chatId = payloadDTO.getChatId();
        Long authorId = payloadDTO.getAuthorId();
        String content = payloadDTO.getContent();
        boolean pinned = "true".equals(content);

        chatParticipantDao.updatePinnedByChatIdAndMappingUserId(chatId, authorId, pinned);
    }

    private void processPayloadToUpdateRoomInfo(PayloadDTO payloadDTO) {
        Timestamp now = Timestamp.from(Instant.now());
        Long chatId = payloadDTO.getChatId();
        String content = payloadDTO.getContent();

        PayloadDTO.RoomInfo roomInfo = gson.fromJson(content, PayloadDTO.RoomInfo.class);
        chatRoomDao.updateChatInfoByChatId(chatId, roomInfo.getChatName(), roomInfo.getPhoto());
        payloadDTO.setTimestamp(now.toString());
    }

    private boolean isParticipantNotFound(Long senderId, Long chatId) {
        // TODO: 優化為緩存的形式
        return chatParticipantDao.findByMappingUserIdAndChatId(senderId, chatId) == null;
    }

    private boolean isChatRoomValid(Long chatId) {
        // TODO: 優化為緩存的形式
        return chatRoomDao.findById(chatId).isEmpty();
    }

    private Long findMappingUserId(String userType, Integer refId) {
        // TODO: 優化為緩存的形式
        return chatUserMappingDao.findMappingUserIdByUserTypeAndRefId(userType, refId);
    }

    private ChatUserMapping createUserMapping(String type, Integer id) {
        ChatUserMapping userMapping = new ChatUserMapping();
        userMapping.setUserType(type);
        userMapping.setRefId(id);
        return chatUserMappingDao.save(userMapping);
    }

    private static boolean isImageEmpty(MessageDTO.ImageDTO img) {
        return img == null || img.getSrc() == null || img.getSrc().trim().isEmpty();
    }
}