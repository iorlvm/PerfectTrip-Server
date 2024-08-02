package idv.tia201.g1.chat.service.impl;

import idv.tia201.g1.chat.dao.ChatMessageDao;
import idv.tia201.g1.chat.dao.ChatParticipantDao;
import idv.tia201.g1.chat.dao.ChatRoomDao;
import idv.tia201.g1.chat.dao.ChatUserMappingDao;
import idv.tia201.g1.chat.service.ChatService;
import idv.tia201.g1.dto.ChatRoomDTO;
import idv.tia201.g1.dto.MessageDTO;
import idv.tia201.g1.dto.ParticipantDTO;
import idv.tia201.g1.entity.ChatMessage;
import idv.tia201.g1.entity.ChatParticipant;
import idv.tia201.g1.entity.ChatRoom;
import idv.tia201.g1.entity.ChatUserMapping;
import idv.tia201.g1.utils.DtoConverter;
import idv.tia201.g1.utils.UserHolder;
import idv.tia201.g1.utils.redis.RedisIdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class ChatServiceImpl implements ChatService {
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
        if (!isChatRoomValid(chatId)) {
            throw new IllegalArgumentException("參數異常: 聊天室不存在");
        }

        // 利用登入使用者id以及type, 獲取senderId
        Integer id = UserHolder.getId();
        String type = UserHolder.getRole();

        Long senderId = findMappingUserId(type, id);
        if (senderId == null || !isParticipantValid(senderId, chatId)) {
            // 映射關係不存在, 表示之前完全沒使用過聊天室
            // 或經過檢查以後發現不是合法的參與者
            throw new IllegalStateException("非法的請求: 該用戶不是此聊天室的參與者");
        }

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

        // 將資料寫入messageDTO(DTO物件)
        messageDTO.setMessageId(messageId);
        messageDTO.setSenderId(senderId);
        messageDTO.setTimestamp(now.toString());

        return messageDTO;
    }

    @Override
    public Page<MessageDTO> getMessages(long chatId, int page, int size) {
        if (!isChatRoomValid(chatId)) {
            throw new IllegalArgumentException("參數異常: 聊天室不存在");
        }
        // 分頁設定
        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Order.desc("createdDate")));

        // TODO: 未來調整成訊息異步寫入的話, 這裡也要增加相應的調整
        Page<ChatMessage> result = chatMessageDao.findByChatId(chatId, pageable);

        List<ChatMessage> messages = result.getContent();
        List<MessageDTO> messageDTOS = new ArrayList<>(messages.size());

        return new PageImpl<>(messageDTOS, pageable, result.getTotalElements());
    }

    @Override
    public List<ChatParticipant> getChatParticipantsByChatId(Long chatId) {
        // TODO: 優化為緩存的形式
        return chatParticipantDao.findByChatId(chatId);
    }

    private boolean isParticipantValid(Long senderId, Long chatId) {
        // TODO: 優化為緩存的形式
        return chatParticipantDao.findByMappingUserIdAndChatId(senderId, chatId) != null;
    }

    private boolean isChatRoomValid(Long chatId) {
        // TODO: 優化為緩存的形式
        return chatRoomDao.findById(chatId).isPresent();
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
