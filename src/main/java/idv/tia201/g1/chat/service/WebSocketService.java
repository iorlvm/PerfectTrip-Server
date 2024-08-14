package idv.tia201.g1.chat.service;

import idv.tia201.g1.dto.PayloadDTO;

public interface WebSocketService {
    PayloadDTO handlePayload(String role, Integer id, PayloadDTO payloadDTO);
}
