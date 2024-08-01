package idv.tia201.g1.chat.service;

import idv.tia201.g1.dto.ChatRoomDTO;
import idv.tia201.g1.dto.MessageDTO;
import org.springframework.data.domain.Page;

public interface ChatService {
    Page<ChatRoomDTO> getChatRooms(int page, int size);

    MessageDTO sendMessage(Long chatId, MessageDTO messageDTO);

}
