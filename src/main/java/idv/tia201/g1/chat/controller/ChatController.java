package idv.tia201.g1.chat.controller;

import idv.tia201.g1.chat.service.ChatService;
import idv.tia201.g1.chat.dto.ChatRoomDTO;
import idv.tia201.g1.core.dto.Result;
import idv.tia201.g1.chat.dto.MessageDTO;
import idv.tia201.g1.chat.dto.UserIdentifier;
import idv.tia201.g1.core.utils.UserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static idv.tia201.g1.core.utils.Constants.TIMESTAMP_MAX_STRING;

@RestController
@RequestMapping("/api/chat")
public class ChatController {
    @Autowired
    private ChatService chatService;

    @GetMapping("/uid")
    public Result getChatUserIdAndUnread() {
        try {
            Long uid = chatService.getOrCreateMappingUserId(UserHolder.getRole(), UserHolder.getId());
            Long unread = chatService.getTotalUnreadMessagesNumber(uid);
            return Result.ok(new String[]{String.valueOf(uid), String.valueOf(unread)});
        } catch (Exception e) {
            // TODO: 也許需要log紀錄
            return Result.fail(e.getMessage());
        }
    }

    @GetMapping("/rooms")
    public Result getChatRoomList(
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = TIMESTAMP_MAX_STRING) String earliest
    ) {
        try {
            Timestamp timestamp = Timestamp.valueOf(earliest);
            List<ChatRoomDTO> chatRooms = chatService.getChatRooms(size, timestamp);
            return Result.ok(chatRooms, (long) chatRooms.size());
        } catch (Exception e) {
            // TODO: 也許需要log紀錄
            return Result.fail(e.getMessage());
        }
    }

    @GetMapping("/rooms/{chatId}")
    public Result getChatRoomById(@PathVariable Long chatId) {
        try {
            ChatRoomDTO chatRoom = chatService.getChatRoomById(chatId);
            return Result.ok(chatRoom);
        } catch (Exception e) {
            // TODO: 也許需要log紀錄
            return Result.fail(e.getMessage());
        }
    }

    @PostMapping("/rooms")
    public Result initChatRoom(@RequestBody List<UserIdentifier> users) {
        Set<UserIdentifier> userSet = new HashSet<>(users);
        try {
            Long chatId = chatService.initChatRoom(userSet);
            return Result.ok(String.valueOf(chatId));
        } catch (Exception e) {
            // TODO: 也許需要log紀錄
            return Result.fail(e.getMessage());
        }
    }

    @PutMapping("/rooms/{chatId}/notify")
    public Result updateChatRoomNotify(@PathVariable Long chatId, @RequestBody ChatRoomDTO chatRoomDTO) {
        try {
            chatService.updateChatRoomNotify(chatId, chatRoomDTO.getNotifySettings());
            return Result.ok(chatRoomDTO.getNotifySettings());
        } catch (Exception e) {
            // TODO: 也許需要log紀錄
            return Result.fail(e.getMessage());
        }
    }

    @PutMapping("/rooms/{chatId}/pinned")
    public Result updateChatRoomPinned(@PathVariable Long chatId, @RequestBody ChatRoomDTO chatRoomDTO) {
        try {
            chatService.updateChatRoomPinned(chatId, chatRoomDTO.getPinned());
            return Result.ok(chatRoomDTO.getPinned());
        } catch (Exception e) {
            // TODO: 也許需要log紀錄
            return Result.fail(e.getMessage());
        }
    }

    @GetMapping("/rooms/{chatId}/messages")
    public Result getMessages(@PathVariable Long chatId, @RequestParam(required = false) Long messageId, @RequestParam(defaultValue = "20") int size) {
        try {
            // 利用messageId只會往上增加以及唯一性, 可以準確地往前抓取一定數量的資料
            if (messageId == null) messageId = Long.MAX_VALUE;              // 沒傳入值時, 給予最大值作為預設值 (從頭抓取)

            if (messageId <= 0) return Result.fail("沒有更多訊息");   // 傳入的值已經是0的時候, 不可能有資料

            List<MessageDTO> messages = chatService.getMessages(chatId, messageId, size);

            if (messages.isEmpty() && messageId < Long.MAX_VALUE) {
                return Result.fail("沒有更多訊息");   // 回傳值是空list, 沒有資料
            } else {
                return Result.ok(messages, (long) messages.size());
            }
        } catch (Exception e) {
            // TODO: 也許需要log紀錄
            return Result.fail(e.getMessage());
        }
    }

    @PostMapping("/rooms/{chatId}/messages")
    public Result sendMessage(@PathVariable Long chatId, @RequestBody MessageDTO messageDTO) {
        // 這個控制器在用途上好像有點問題
        try {
            messageDTO = chatService.sendMessage(chatId, messageDTO);
            return Result.ok(messageDTO);
        } catch (Exception e) {
            // TODO: 也許需要log紀錄
            return Result.fail(e.getMessage());
        }
    }
}
