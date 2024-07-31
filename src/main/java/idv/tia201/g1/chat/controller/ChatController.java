package idv.tia201.g1.chat.controller;

import idv.tia201.g1.chat.service.ChatService;
import idv.tia201.g1.dto.ChatRoomDTO;
import idv.tia201.g1.dto.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat")
public class ChatController {
    @Autowired
    private ChatService chatService;

    @GetMapping("/rooms")
    public Result getRooms() {

        Page<ChatRoomDTO> chatRooms = chatService.getChatRooms(0, 20);

        return Result.ok(chatRooms.getContent(),chatRooms.getTotalElements());
    }
}
