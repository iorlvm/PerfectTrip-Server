package idv.tia201.g1.chat.service;

import idv.tia201.g1.core.entity.UserAuth;
import idv.tia201.g1.chat.dto.ChatRoomDTO;
import idv.tia201.g1.chat.dto.MessageDTO;
import idv.tia201.g1.chat.dto.UserIdentifier;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

public interface ChatService {
    void updateUserInfo(UserAuth userAuth);

    @Transactional
    Long initChatRoom(Set<UserIdentifier> users);

    @Transactional
    void initChatRoomWithAdmin(UserAuth userAuth);

    @Transactional(readOnly = true)
    ChatRoomDTO getChatRoomById(Long chatId);

    @Transactional(readOnly = true)
    List<ChatRoomDTO> getChatRooms(int size, Timestamp earliestTimestamp);

    @Transactional(readOnly = true)
    List<MessageDTO> getMessages(long chatId, Long messageId, int size);

    @Transactional
    MessageDTO sendMessage(Long chatId, MessageDTO messageDTO);

    Long getOrCreateMappingUserId(String type, Integer id);

    Long getTotalUnreadMessagesNumber(Long mappingUserId);

    @Transactional
    void updateChatRoomPinned(Long chatId, Boolean pinned);

    @Transactional
    void updateChatRoomNotify(Long chatId, String state);
}
