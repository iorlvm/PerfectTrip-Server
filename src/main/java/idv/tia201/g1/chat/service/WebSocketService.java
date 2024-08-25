package idv.tia201.g1.chat.service;

import idv.tia201.g1.chat.dto.PayloadDTO;

public interface WebSocketService {
    /**
     * 處理聊天室發送的訊息
     *
     * @param role 聊天室使用者身分
     * @param id 聊天室使用者id
     * @param payloadDTO 聊天室發送的訊息
     * @return 處理後的訊息
     */
    PayloadDTO handlePayload(String role, Integer id, PayloadDTO payloadDTO);
}
