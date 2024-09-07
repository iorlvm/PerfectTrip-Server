package idv.tia201.g1.chat.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import idv.tia201.g1.chat.dto.*;
import idv.tia201.g1.core.entity.UserAuth;
import idv.tia201.g1.chat.dao.ChatMessageDao;
import idv.tia201.g1.chat.dao.ChatParticipantDao;
import idv.tia201.g1.chat.dao.ChatRoomDao;
import idv.tia201.g1.chat.event.UserUpdateEvent;
import idv.tia201.g1.chat.service.CacheService;
import idv.tia201.g1.chat.service.ChatService;
import idv.tia201.g1.chat.entity.ChatMessage;
import idv.tia201.g1.chat.entity.ChatParticipant;
import idv.tia201.g1.chat.entity.ChatRoom;
import idv.tia201.g1.chat.utils.DtoConverter;
import idv.tia201.g1.core.utils.UserHolder;
import idv.tia201.g1.core.utils.redis.RedisIdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

import static idv.tia201.g1.chat.utils.Utils.isImageEmpty;
import static idv.tia201.g1.core.utils.Constants.*;

@Service
public class ChatServiceImpl implements ChatService {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ChatParticipantDao chatParticipantDao;
    @Autowired
    private ChatRoomDao chatRoomDao;
    @Autowired
    private ChatMessageDao chatMessageDao;
    @Autowired
    RedisIdWorker idWorker;
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Autowired
    private CacheService cacheService;

    @Override
    public void updateUserInfo(UserAuth userAuth) {
        if (userAuth == null) throw new IllegalArgumentException("參數錯誤：沒有傳入任何使用者");

        String role = userAuth.getRole();
        Integer id = userAuth.getId();

        Long loginUserId = cacheService.findMappingUserId(role, id);
        if (loginUserId == null) {
            // 沒使用過聊天室, 不需要做任何事情
            return;
        }

        PayloadDTO payloadDTO = new PayloadDTO();
        payloadDTO.setAuthorId(loginUserId);
        payloadDTO.setTimestamp(Timestamp.from(Instant.now()).toString());
        payloadDTO.setAction(CHAT_ACTION_UPDATE_USER_INFO);

        // 轉寫userAuth為dto物件, 並轉成json格式放入內容中
        PayloadDTO.UserInfo userInfoDTO = DtoConverter.toUserInfoDTO(userAuth);
        try {
            payloadDTO.setContent(objectMapper.writeValueAsString(userInfoDTO));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        eventPublisher.publishEvent(new UserUpdateEvent(this, payloadDTO));
    }

    @Override
    public ChatRoomDTO initChatRoom(Set<UserIdentifier> users) {
        // 檢查參數
        if (users == null || users.isEmpty()) {
            throw new IllegalArgumentException("參數錯誤：沒有傳入任何使用者");
        }

        // 取得登入用戶資料
        String type = UserHolder.getRole();
        Integer id = UserHolder.getId();

        if (type == null || id == null)
            throw new IllegalStateException("不合法的訪問: 請檢查您的登入狀態");

        // 如果只有一個使用者，檢查是否為當前登入用戶
        if (users.size() == 1) {
            UserIdentifier singleUser = users.iterator().next();
            if (type.equals(singleUser.getType()) && id.equals(singleUser.getId())) {
                throw new IllegalArgumentException("不能創建與自己單獨的聊天室");
            }
        }

        Long loginUserId = getOrCreateMappingUserId(type, id);

        // 創造聊天室
        ChatRoom chatRoom = createChatRoom();
        Long chatId = chatRoom.getChatId();
        // 將登入者寫入資料庫
        addParticipantToChatRoom(chatId, loginUserId);
        // 遍歷傳入的使用者
        for (UserIdentifier user : users) {
            // 取得或建立映射id
            Long mappingUserId = getOrCreateMappingUserId(user.getType(), user.getId());
            // 將參與者寫入資料庫
            addParticipantToChatRoom(chatId, mappingUserId);
        }

        // 初始化完畢以後, 重新查詢並回傳 (這樣寫不太好, 但因為我DTO物件查詢有額外欄位所以不能直接使用回傳值)
        return getChatRoomById(chatId);
    }

    @Override
    public Long getOrCreateMappingUserId(String type, Integer id) {
        if (type == null || id == null) {
            throw new IllegalStateException("不合法的訪問: 請檢查您的登入狀態");
        }

        Long userId = cacheService.findMappingUserId(type, id);
        if (userId == null) {
            userId = cacheService.createUserMapping(type, id).getMappingUserId();
        }

        return userId;
    }

    @Override
    public Long getTotalUnreadMessagesNumber(Long mappingUserId) {
        return chatParticipantDao.getTotalUnreadMessagesNumberByMappingUserId(mappingUserId);
    }

    private ChatRoom createChatRoom() {
        Long chatId = idWorker.nextId("chat");
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setChatId(chatId);
        return chatRoomDao.save(chatRoom);
    }

    private void addParticipantToChatRoom(Long chatId, Long mappingUserId) {
        ChatParticipant chatParticipant = new ChatParticipant();
        chatParticipant.setChatId(chatId);
        chatParticipant.setMappingUserId(mappingUserId);
        chatParticipantDao.save(chatParticipant);
    }

    @Override
    public ChatRoomDTO getChatRoomById(Long chatId) {
        // 取得登入用戶資料
        Long loginUserId = findMappingUserId();
        if (loginUserId == null) {
            // 映射關係不存在, 表示之前完全沒使用過聊天室 (同時也不可能是這個聊天室的參與者)
            throw new IllegalStateException("非法的請求: 該用戶不是此聊天室的參與者");
        }

        return createChatRoomDTO(chatId, loginUserId);
    }

    @Override
    public List<ChatRoomDTO> getChatRooms(int size, Timestamp earliestTimestamp) {
        // 取得登入用戶資料
        String type = UserHolder.getRole();
        Integer id = UserHolder.getId();

        // 根據登入用戶取得映射id
        Long loginUserId = findMappingUserId();
        if (loginUserId == null) {
            // 映射關係不存在, 表示之前完全沒使用過聊天室(不可能存在聊天列表), 回傳一個長度為0的page
            return Collections.emptyList();
        }

        // 取得聊天室列表
        List<Long> chatIdsForUser = chatParticipantDao.findChatIdByTypeAndRefId(type, id, size, earliestTimestamp);
        List<ChatRoomDTO> chatRoomDTOS = new ArrayList<>(chatIdsForUser.size());

        // 根據列表中的聊天室id取得各個聊天室的參與者
        for (Long chatId : chatIdsForUser) {
            // 利用chatId取得聊天室的詳細資料, 並寫入DTO物件
            ChatRoomDTO chatRoomDTO = createChatRoomDTO(chatId, loginUserId);
            chatRoomDTOS.add(chatRoomDTO);
        }
        // 回傳
        return chatRoomDTOS;
    }

    @Override
    public MessageDTO sendMessage(Long chatId, MessageDTO messageDTO) {
        // 檢查是否有發送任何訊息
        if (messageDTO.getContent() == null && isImageEmpty(messageDTO.getImg())) {
            throw new IllegalArgumentException("請求異常：沒有發送任何內容");
        }

        // 檢查聊天室是否存在 (不存在 訪回異常狀態)
        if (cacheService.isChatRoomInvalid(chatId)) {
            throw new IllegalArgumentException("參數異常: 聊天室不存在");
        }

        // 利用登入使用者, 獲取senderId
        Long senderId = findMappingUserId();
        if (senderId == null || cacheService.isParticipantNotFound(senderId, chatId)) {
            // 映射關係不存在, 表示之前完全沒使用過聊天室
            // 或經過檢查以後發現不是合法的參與者
            throw new IllegalStateException("非法的請求: 該用戶不是此聊天室的參與者");
        }

        return cacheService.saveMessage(senderId, chatId, messageDTO);
    }

    @Override
    public List<MessageDTO> getMessages(long chatId, Long messageId, int size) {
        // 利用messageId的自增以及唯一性, 可以準確地往上抓取一定數量的資料
        if (cacheService.isChatRoomInvalid(chatId)) {
            throw new IllegalArgumentException("參數異常: 聊天室不存在");
        }

        List<ChatMessage> messages;
        if (messageId == Long.MAX_VALUE) {
            // 第一次訪問先從緩存中取資料
            List<ChatMessage> cacheMessages = cacheService.getMessages(chatId);
            if (cacheMessages.size() >= size) {
                messages = cacheMessages;
            } else {
                messages = new ArrayList<>(size);
                messages.addAll(cacheMessages);
            }
        } else {
            messages = new ArrayList<>(size);
        }

        // 取到的資料數量沒超過size
        if (messages.size() < size) {
            // 雖然沒超過size, 但還是有取到值
            if (!messages.isEmpty()) {
                messageId = messages.get(messages.size() - 1).getMessageId();
            }
            Pageable pageable = PageRequest.of(0, size - messages.size(), Sort.by(Sort.Direction.DESC, "messageId"));

            // 從資料庫中撈取補充訊息數量
            List<ChatMessage> dbMessages = chatMessageDao.findByChatIdAndMessageIdLessThan(chatId, messageId, pageable);
            if (dbMessages != null) messages.addAll(dbMessages);
        }

        List<MessageDTO> messageDTOS = new ArrayList<>(messages.size());

        for (ChatMessage message : messages) {
            MessageDTO messageDTO = DtoConverter.toMessageDTO(message);
            messageDTOS.add(messageDTO);
        }

        return messageDTOS;
    }

    @Override
    public void updateChatRoomPinned(Long chatId, Boolean pinned) {
        if (pinned == null) {
            throw new IllegalArgumentException("參數異常：pinned不可為空");
        }

        Long loginUserId = findMappingUserId();
        if (loginUserId == null || cacheService.isParticipantNotFound(loginUserId, chatId)) {
            // 映射關係不存在, 表示之前完全沒使用過聊天室 (同時也不可能是這個聊天室的參與者)
            throw new IllegalStateException("非法的請求: 該用戶不是此聊天室的參與者");
        }

        cacheService.updateChatSettings(chatId, loginUserId, pinned, null);
    }

    @Override
    public void updateChatRoomNotify(Long chatId, String state) {
        switch (state) {
            case "on":
            case "off":
                break;
            default:
                throw new IllegalArgumentException("參數異常：只能傳入'on', 'off'");
        }

        // 取得登入用戶資料
        Long loginUserId = findMappingUserId();
        if (loginUserId == null || cacheService.isParticipantNotFound(loginUserId, chatId)) {
            // 映射關係不存在, 表示之前完全沒使用過聊天室 (同時也不可能是這個聊天室的參與者)
            throw new IllegalStateException("非法的請求: 該用戶不是此聊天室的參與者");
        }

        cacheService.updateChatSettings(chatId, loginUserId, null, state);
    }

    private Long findMappingUserId() {
        String type = UserHolder.getRole();
        Integer id = UserHolder.getId();

        if (type == null || id == null) {
            throw new IllegalStateException("不合法的訪問: 請檢查您的登入狀態");
        }

        return cacheService.findMappingUserId(type, id);
    }

    private ChatRoomDTO createChatRoomDTO(Long chatId, Long userMappingId) {
        // 利用chatId取得聊天室的詳細資料, 並寫入DTO物件
        ChatRoom chatRoom = cacheService.findChatRoomByChatId(chatId);
        if (chatRoom == null) {
            throw new IllegalStateException("狀態異常: 沒有對應的聊天室");
        }

        // 取得聊天室中所有的參與者
        List<ChatParticipant> chatParticipants = cacheService.getChatParticipantsByChatId(chatId);
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
}