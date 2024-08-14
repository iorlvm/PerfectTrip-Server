package idv.tia201.g1.chat.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import idv.tia201.g1.chat.dao.ChatMessageDao;
import idv.tia201.g1.chat.dao.ChatParticipantDao;
import idv.tia201.g1.chat.dao.ChatRoomDao;
import idv.tia201.g1.chat.dao.ChatUserMappingDao;
import idv.tia201.g1.chat.service.CacheService;
import idv.tia201.g1.dto.MessageDTO;
import idv.tia201.g1.entity.ChatMessage;
import idv.tia201.g1.entity.ChatParticipant;
import idv.tia201.g1.entity.ChatRoom;
import idv.tia201.g1.entity.ChatUserMapping;
import idv.tia201.g1.utils.redis.CacheClient;
import idv.tia201.g1.utils.redis.RedisIdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static idv.tia201.g1.chat.utils.Utils.isImageEmpty;
import static idv.tia201.g1.utils.Constants.*;

@Service
public class CacheServiceImpl implements CacheService {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ChatUserMappingDao chatUserMappingDao;
    @Autowired
    private ChatParticipantDao chatParticipantDao;
    @Autowired
    private ChatRoomDao chatRoomDao;
    @Autowired
    private ChatMessageDao chatMessageDao;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    private CacheClient cacheClient;
    @Autowired
    private RedisIdWorker idWorker;

    @Override
    public Long findMappingUserId(String userType, Integer refId) {
        // 幾乎不會被更新的值 (也可以設定為永不過期)
        return cacheClient.queryWithMutex(
                CACHE_CHAT_USER_MAPPING,
                LOCK_CHAT_USER_MAPPING,
                userType + ":" + refId,
                Long.class,
                CACHE_CHAT_TTL,
                TimeUnit.SECONDS,
                (id) -> {
                    String[] parts = id.split(":", 2);
                    return chatUserMappingDao.findMappingUserIdByUserTypeAndRefId(parts[0], Integer.valueOf(parts[1]));
                }
        );
    }

    @Override
    public ChatUserMapping createUserMapping(String type, Integer id) {
        ChatUserMapping userMapping = new ChatUserMapping();
        userMapping.setUserType(type);
        userMapping.setRefId(id);
        // 存入資料庫
        chatUserMappingDao.save(userMapping);
        // 將緩存中的資料更新
        String key = CACHE_CHAT_USER_MAPPING + type + ":" + id;
        Long userId = userMapping.getMappingUserId();
        stringRedisTemplate.opsForValue().set(key, userId.toString());
        return userMapping;
    }

    @Override
    public boolean isChatRoomInvalid(Long chatId) {
        return findChatRoomByChatId(chatId) == null;
    }

    @Override
    public ChatRoom findChatRoomByChatId(Long chatId) {
        // 查詢結果放入redis緩存 (鍵:"cache:chatroom:chatId)
        // 先查詢redis, 不存在緩存才重建 (過期消失時間60分鐘)
        // 後續所有的操作都要同步更新資料, 加快送出訊息的讀寫效率 (避免異步寫入造成資料不同步)
        return cacheClient.queryWithMutex(
                CACHE_CHAT,
                LOCK_CHAT,
                chatId,
                ChatRoom.class,
                CACHE_CHAT_TTL,
                TimeUnit.SECONDS,
                chatRoomDao::findByChatId
        );
    }

    @Override
    public List<ChatParticipant> getChatParticipantsByChatId(Long chatId) {
        // 查詢結果放入redis緩存 (鍵:"cache:ptp:chatId)
        // 先查詢redis, 不存在緩存才重建 (過期消失時間60分鐘)
        // 後續所有的操作都要同步更新資料, 加快送出訊息的讀寫效率 (避免異步寫入造成資料不同步)
        return cacheClient.queryWithMutex(
                CACHE_CHAT_PARTICIPANT,
                LOCK_CHAT_PARTICIPANT,
                chatId,
                CACHE_CHAT_TTL,
                TimeUnit.SECONDS,
                chatParticipantDao::findByChatIdToDTO);
    }

    @Override
    public boolean isParticipantNotFound(Long senderId, Long chatId) {
        // TODO: 優化為緩存的形式
        return chatParticipantDao.findByMappingUserIdAndChatId(senderId, chatId) == null;
    }

    @Override
    public Set<Long> getChatRoomsIdByRoleAndId(String role, Integer id) {
        // TODO: 優化為緩存的形式
        return chatParticipantDao.findChatIdByTypeAndRefId(role, id);
    }

    @Override
    public void updateLastReadingAt(Long chatId, Long mappingUserId, Timestamp now) {
        // TODO: 更新緩存資料, 並加入隊列進行資料庫寫入
        chatParticipantDao.updateLastReadingAtByChatIdAndMappingUserId(chatId, mappingUserId, now);
    }

    @Override
    public void updateChatInfo(Long chatId, String chatName, String chatPhoto) {
        // TODO: 更新緩存資料, 並加入隊列進行資料庫寫入
        chatRoomDao.updateChatInfoByChatId(chatId, chatName, chatPhoto);
    }

    @Override
    public MessageDTO saveMessage(Long senderId, Long chatId, MessageDTO messageDTO) {
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
        //       目前的實現速度有點太慢了, 一個聊天訊息要通訊200~300ms
        //       看看有沒有辦法用redis優化速度
        //       如果用redis太過困難的話, 可以考慮直接開執行緒異步寫入
        //       然後直接回應前端, 讓前端頁面先顯示
        //       但缺點是可能會在當機後造成訊息丟失, 顯示畫面與資料庫內容不同步
        chatMessageDao.save(chatMessage);

        List<ChatParticipant> chatParticipants = chatParticipantDao.findByChatId(chatId);
        for (ChatParticipant chatParticipant : chatParticipants) {
            if (!Objects.equals(chatParticipant.getMappingUserId(), senderId)) {
                // 對所有的其他參與者增加未讀數量
                chatParticipant.setUnreadMessages(chatParticipant.getUnreadMessages() + 1);
            } else {
                // 對自己更新最後讀取時間, 並設定未讀數量為0
                chatParticipant.setUnreadMessages(0);
                chatParticipant.setLastReadingAt(now);
            }
            chatParticipantDao.save(chatParticipant);
        }
        // 更新聊天室的最後訊息以及, 最後訊息時間
        ChatRoom chatRoom = findChatRoomByChatId(chatId);

        if (chatRoom == null) {
            throw new IllegalStateException("狀態異常: 沒有對應的聊天室");
        }

        if (messageDTO.getContent() != null) {
            chatRoom.setLastMessage(messageDTO.getContent());
        } else {
            // 沒有文字內容的情況只會是圖片 (如果有其他擴充在對這邊做修正)
            chatRoom.setLastMessage("圖片");
        }

        chatRoom.setLastMessageAt(now);
        chatRoomDao.save(chatRoom);

        // 將資料寫入messageDTO(DTO物件)
        messageDTO.setMessageId(messageId);
        messageDTO.setSenderId(senderId);
        messageDTO.setTimestamp(now.toString());

        return messageDTO;
    }
}