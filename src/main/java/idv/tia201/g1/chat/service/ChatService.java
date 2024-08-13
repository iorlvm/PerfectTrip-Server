package idv.tia201.g1.chat.service;

import idv.tia201.g1.authentication.service.UserAuth;
import idv.tia201.g1.dto.ChatRoomDTO;
import idv.tia201.g1.dto.MessageDTO;
import idv.tia201.g1.dto.PayloadDTO;
import idv.tia201.g1.dto.UserIdentifier;
import idv.tia201.g1.entity.ChatParticipant;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

public interface ChatService {

    void updateUserInfo(UserAuth userAuth);

    void updateChatRoomNotify(Long chatId, String state);

    ChatRoomDTO initChatRoom(Set<UserIdentifier> users);

    ChatRoomDTO getChatRoomById(Long chatId);

    List<ChatRoomDTO> getChatRooms(int size, Timestamp earliestTimestamp);

    List<MessageDTO> getMessages(long chatId, Long messageId, int size);

    MessageDTO sendMessage(Long chatId, MessageDTO messageDTO);

    List<ChatParticipant> getChatParticipantsByChatId(Long chatId);

    Set<Long> getChatRoomsIdByRoleAndId(String role, Integer id);

    Long getOrCreateMappingUserId(String type, Integer id);

    PayloadDTO handlePayload(String role, Integer id, PayloadDTO payloadDTO);

    void updateChatRoomPinned(Long chatId, Boolean pinned);
}
