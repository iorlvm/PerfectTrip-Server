package idv.tia201.g1.chat.controller;

import idv.tia201.g1.chat.service.ChatService;
import idv.tia201.g1.dto.ChatRoomDTO;
import idv.tia201.g1.dto.Result;
import idv.tia201.g1.dto.MessageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chat")
public class ChatController {
    @Autowired
    private ChatService chatService;

    @GetMapping("/rooms")
    public Result getChatRoomList(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {

        Page<ChatRoomDTO> chatRooms = chatService.getChatRooms(page, size);

        return Result.ok(chatRooms.getContent(),chatRooms.getTotalElements());
    }

    @PostMapping("/rooms/{chatId}/messages")
    public Result sendMessage(@PathVariable Long chatId, @RequestBody MessageDTO messageDTO) {
        try {
            messageDTO = chatService.sendMessage(chatId, messageDTO);
        } catch (Exception e) {
            // TODO: 也許需要log紀錄
            return Result.fail(e.getMessage());
        }

        return Result.ok(messageDTO);
    }
}
