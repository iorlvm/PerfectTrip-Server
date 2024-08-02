package idv.tia201.g1.chat.service;

import idv.tia201.g1.dto.ChatRoomDTO;
import idv.tia201.g1.dto.MessageDTO;
import idv.tia201.g1.entity.ChatParticipant;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ChatService {
    ChatRoomDTO getChatRoomById (Long chatId);

    Page<ChatRoomDTO> getChatRooms(int page, int size);

    Page<MessageDTO> getMessages(long chatId, int page, int size);

    MessageDTO sendMessage(Long chatId, MessageDTO messageDTO);

    List<ChatParticipant> getChatParticipantsByChatId(Long chatId);
}
