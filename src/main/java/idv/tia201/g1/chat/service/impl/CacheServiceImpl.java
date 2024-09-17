package idv.tia201.g1.chat.service.impl;

import idv.tia201.g1.chat.utils.ChatCacheClient;
import idv.tia201.g1.chat.dao.ChatMessageDao;
import idv.tia201.g1.chat.dao.ChatParticipantDao;
import idv.tia201.g1.chat.dao.ChatRoomDao;
import idv.tia201.g1.chat.dao.ChatUserMappingDao;
import idv.tia201.g1.chat.service.CacheService;
import idv.tia201.g1.chat.dto.MessageDTO;
import idv.tia201.g1.chat.entity.ChatMessage;
import idv.tia201.g1.chat.entity.ChatParticipant;
import idv.tia201.g1.chat.entity.ChatRoom;
import idv.tia201.g1.chat.entity.ChatUserMapping;
import idv.tia201.g1.core.utils.basic.JSONUtil;
import idv.tia201.g1.core.utils.redis.CacheClient;
import idv.tia201.g1.core.utils.redis.RedisIdWorker;
import jakarta.annotation.PostConstruct;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static idv.tia201.g1.chat.utils.Utils.isImageEmpty;
import static idv.tia201.g1.core.utils.Constants.*;

@Service
public class CacheServiceImpl implements CacheService {
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
    private ChatCacheClient chatCacheClient;
    @Autowired
    private RedisIdWorker idWorker;
    @Autowired
    TransactionTemplate transactionTemplate;

    private final static String QUEUE_NAME = "stream.chat";
    private static final DefaultRedisScript<Long> MANAGE_UNREAD_MESSAGES_SCRIPT;

    static {
        MANAGE_UNREAD_MESSAGES_SCRIPT = new DefaultRedisScript<>();
        MANAGE_UNREAD_MESSAGES_SCRIPT.setLocation(new ClassPathResource("lua/manageUnreadMessages.lua"));
        MANAGE_UNREAD_MESSAGES_SCRIPT.setResultType(Long.class);
    }

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
        cacheClient.set(key, userId, CACHE_CHAT_TTL, TimeUnit.SECONDS);
        return userMapping;
    }

    @Override
    public boolean isChatRoomInvalid(Long chatId) {
        return findChatRoomByChatId(chatId) == null;
    }

    @Override
    public ChatRoom findChatRoomByChatId(Long chatId) {
        // 只要被查詢過都會延長時間 (不存在的情形, 不會有任何反應)
        String key = CACHE_CHAT + chatId;
        stringRedisTemplate.expire(key, CACHE_CHAT_TTL, TimeUnit.SECONDS);

        // 查詢結果放入redis緩存 (鍵:"cache:chatroom:chatId)
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
        // 只要被查詢過都會延長時間 (不存在的情形, 不會有任何反應)
        String key = CACHE_CHAT_PARTICIPANT + chatId;
        stringRedisTemplate.expire(key, CACHE_CHAT_TTL, TimeUnit.SECONDS);

        // 查詢結果放入redis緩存 (鍵:"cache:ptp:chatId)
        return chatCacheClient.getAllParticipantsList(
                chatId,
                CACHE_CHAT_TTL,
                TimeUnit.SECONDS,
                chatParticipantDao::findByChatId);
    }

    @Override
    public boolean isParticipantNotFound(Long mappingUserId, Long chatId) {
        return findParticipantByChatIdAndMappingUserId(chatId, mappingUserId) == null;
    }

    private ChatParticipant findParticipantByChatIdAndMappingUserId(Long chatId, Long mappingUserId) {
        String key = CACHE_CHAT_PARTICIPANT + chatId;
        stringRedisTemplate.expire(key, CACHE_CHAT_TTL, TimeUnit.SECONDS);

        return chatCacheClient.queryParticipantWithMutex(
                chatId,
                mappingUserId,
                CACHE_CHAT_TTL,
                TimeUnit.SECONDS,
                (ids) -> chatParticipantDao.findByMappingUserIdAndChatId(ids[1], ids[0])
        );
    }

    @Override
    public Set<Long> getChatRoomIdsByRoleAndId(String role, Integer id) {
        return chatParticipantDao.findChatIdByTypeAndRefId(role, id);
    }

    @Override
    public void updateLastReadingAt(Long chatId, Long mappingUserId, Timestamp now) {
        ChatParticipant participant = findParticipantByChatIdAndMappingUserId(chatId, mappingUserId);
        participant.setUnreadMessages(0);
        participant.setLastReadingAt(now);

        // 修改緩存中的資料
        chatCacheClient.mapPut(
                CACHE_CHAT_PARTICIPANT + chatId,
                mappingUserId.toString(),
                participant,
                CACHE_CHAT_TTL,
                TimeUnit.SECONDS);
        // 將修改丟入隊列中
        stringRedisTemplate.opsForStream().add(
                QUEUE_NAME,
                Collections.singletonMap("participant", JSONUtil.toJsonStr(participant)));
    }

    @Override
    public void updateChatSettings(Long chatId, Long mappingUserId, Boolean pinned, String notify) {
        if (pinned == null && notify == null) return; // 沒有修改任何東西

        ChatParticipant participant = findParticipantByChatIdAndMappingUserId(chatId, mappingUserId);
        if (pinned != null) participant.setPinned(pinned);
        if (notify != null) participant.setNotify(notify);

        // 修改緩存中的資料
        chatCacheClient.mapPut(
                CACHE_CHAT_PARTICIPANT + chatId,
                mappingUserId.toString(),
                participant,
                CACHE_CHAT_TTL,
                TimeUnit.SECONDS);
        // 將修改丟入隊列中
        stringRedisTemplate.opsForStream().add(
                QUEUE_NAME,
                Collections.singletonMap("participant", JSONUtil.toJsonStr(participant)));
    }

    @Override
    public void updateChatInfo(Long chatId, String chatName, String chatPhoto) {
        ChatRoom chatRoom = findChatRoomByChatId(chatId);
        if (chatName != null) chatRoom.setChatName(chatName);
        if (chatPhoto != null) chatRoom.setPhoto(chatPhoto);
        // 修改緩存中的資料
        cacheClient.set(CACHE_CHAT + chatId, chatRoom, CACHE_CHAT_TTL, TimeUnit.SECONDS);
        // 將修改丟入隊列中
        stringRedisTemplate.opsForStream().add(
                QUEUE_NAME,
                Collections.singletonMap("chatRoom", JSONUtil.toJsonStr(chatRoom)));
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

        // 更新聊天室的最後訊息以及, 最後訊息時間
        ChatRoom chatRoom = findChatRoomByChatId(chatId);

        if (chatRoom == null) {
            throw new IllegalStateException("狀態異常: 沒有對應的聊天室");
        }

        String lastMessage = null;

        if (messageDTO.getContent() != null && !messageDTO.getContent().trim().isEmpty()) {
            lastMessage = messageDTO.getContent();
        } else {
            // 沒有文字內容的情況只會是圖片 (如果有其他擴充在對這邊做修正)
            lastMessage = "圖片";
        }

        chatRoom.setLastMessage(lastMessage);
        chatRoom.setLastMessageAt(now);

        // 處理緩存以及消息隊列
        manageSendMessage(chatMessage, chatRoom);

        // 將資料寫入messageDTO(DTO物件)
        messageDTO.setMessageId(messageId);
        messageDTO.setSenderId(senderId);
        messageDTO.setTimestamp(now.toString());

        return messageDTO;
    }

    @Override
    public List<ChatMessage> getMessages(long chatId) {
        String key = CACHE_CHAT_MESSAGES + chatId;
        List<String> messageJsonList = stringRedisTemplate.opsForList().range(key, 0, -1);
        if (messageJsonList == null || messageJsonList.isEmpty()) return Collections.emptyList();

        return messageJsonList.stream()
                .map(json -> JSONUtil.toBean(json, ChatMessage.class))
                .sorted(Comparator.comparing(ChatMessage::getMessageId).reversed())
                .toList();
    }

    private void manageSendMessage(ChatMessage chatMessage, ChatRoom chatRoom) {
        // chatMessage暫存到redis中, 過期時間 60 分鐘
        // list格式 key: cache:messages:chatId, 讀取時要優先取出所有的暫存訊息
        String messagesKey = CACHE_CHAT_MESSAGES + chatMessage.getChatId();
        String messageJson = JSONUtil.toJsonStr(chatMessage);
        stringRedisTemplate.opsForList().leftPush(messagesKey, messageJson);
        stringRedisTemplate.expire(messagesKey, CACHE_CHAT_TTL, TimeUnit.SECONDS);

        // 修改緩存中的聊天室資料
        String chatKey = CACHE_CHAT + chatRoom.getChatId();
        cacheClient.set(chatKey, chatRoom, CACHE_CHAT_TTL, TimeUnit.SECONDS);

        // 對所有的其他參與者增加未讀數量
        List<ChatParticipant> participants = manageUnreadMessages(
                chatMessage.getChatId(),
                chatMessage.getMappingUserId(),
                chatMessage.getCreatedDate());

        Map<String, String> queue = new HashMap<>();
        queue.put("chatMessage", messageJson);
        queue.put("chatRoom", JSONUtil.toJsonStr(chatRoom));
        queue.put("participants", JSONUtil.toJsonStr(participants));

        // 將更新資料庫的任務放進消息隊列中
        stringRedisTemplate.opsForStream().add(QUEUE_NAME, queue);
    }

    private List<ChatParticipant> manageUnreadMessages(Long chatId, Long senderId, Timestamp now) {
        // 確保 Rides 緩存中有資料, 不存在的情形下會被重建 (已經存在的情況下回應速度很快, 個人評估是覺得可以接受的額外開銷)
        getChatParticipantsByChatId(chatId);

        // 使用 lua 腳本對緩存資料進行操作
        stringRedisTemplate.execute(
                MANAGE_UNREAD_MESSAGES_SCRIPT,
                Collections.singletonList(CACHE_CHAT_PARTICIPANT + chatId),
                senderId.toString(),
                now.toString());

        return getChatParticipantsByChatId(chatId);
    }

    private static final ExecutorService CHAT_EXECUTOR = Executors.newSingleThreadExecutor();

    @PostConstruct
    private void init() {
        // 啟動以後創建一個執行緒處理消息隊列中的資料
        CHAT_EXECUTOR.submit(new ChatQueueHandler());
    }

    private class ChatQueueHandler implements Runnable {
        private final static String CHAT_GROUP = "chat_group";
        private final static int RETRY_TIMES = 100;
        private final static String CLIENT = "c1";

        @Override
        public void run() {
            while (true) {
                try {
                    // 從隊列中取出一筆資料
                    List<MapRecord<String, Object, Object>> read = stringRedisTemplate.opsForStream().read(
                            Consumer.from(CHAT_GROUP, CLIENT),
                            StreamReadOptions.empty().count(1).block(Duration.ofSeconds(2)),
                            StreamOffset.create(QUEUE_NAME, ReadOffset.lastConsumed())
                    );

                    // 檢查隊列中是否有資料
                    if (read == null || read.isEmpty()) {
                        continue;
                    }

                    // 有資料解析資料, 並寫入資料庫中
                    MapRecord<String, Object, Object> record = read.get(0);
                    processChatQueue(record);

                    // 處理完畢 (標記為已處理)
                    stringRedisTemplate.opsForStream().acknowledge(QUEUE_NAME, CHAT_GROUP, record.getId());
                } catch (Exception e) {
//                    e.printStackTrace();
                    // 異常重試
                    handlePendingList();
                }
            }
        }

        private void handlePendingList() {
            MapRecord<String, Object, Object> record = null;
            for (int i = 0; i < RETRY_TIMES; i++) {
                try {
                    // 從隊列中取出一筆資料
                    List<MapRecord<String, Object, Object>> read = stringRedisTemplate.opsForStream().read(
                            Consumer.from(CHAT_GROUP, CLIENT),
                            StreamReadOptions.empty().count(1),
                            StreamOffset.create(QUEUE_NAME, ReadOffset.from("0"))
                    );

                    // 檢查隊列中是否有資料
                    if (read == null || read.isEmpty()) {
                        // 沒有異常訊息, 結束異常處理程序
                        return;
                    }

                    // 有資料解析資料, 並寫入資料庫中
                    record = read.get(0);
                    processChatQueue(record);

                    // 處理完畢 (標記為已處理)
                    stringRedisTemplate.opsForStream().acknowledge(QUEUE_NAME, CHAT_GROUP, record.getId());
                } catch (Exception e) {
//                    e.printStackTrace();
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }

            if (record != null) {
                // 超出重試次數
                LoggerFactory.getLogger(CacheServiceImpl.class).error(record.getValue().toString());
                // 超出預期的錯誤 (避免卡死, 先跳過這筆訊息)
                stringRedisTemplate.opsForStream().acknowledge(QUEUE_NAME, CHAT_GROUP, record.getId());
            }
        }

        private void processChatQueue(MapRecord<String, Object, Object> record) {
            Map<Object, Object> value = record.getValue();

            String chatMessageStr = (String) value.get("chatMessage");
            String chatRoomStr = (String) value.get("chatRoom");
            String participantStr = (String) value.get("participant");
            String participantListStr = (String) value.get("participants");


            transactionTemplate.execute(status -> {
                if (chatMessageStr != null && !chatMessageStr.trim().isEmpty()) {
                    ChatMessage chatMessage = JSONUtil.toBean(chatMessageStr, ChatMessage.class);
                    chatMessageDao.save(chatMessage);
                }

                if (chatRoomStr != null && !chatRoomStr.trim().isEmpty()) {
                    ChatRoom chatRoom = JSONUtil.toBean(chatRoomStr, ChatRoom.class);
                    chatRoomDao.save(chatRoom);
                }

                if (participantStr != null && !participantStr.trim().isEmpty()) {
                    ChatParticipant participant = JSONUtil.toBean(participantStr, ChatParticipant.class);
                    chatParticipantDao.save(participant);
                }

                if (participantListStr != null && !participantListStr.trim().isEmpty()) {
                    List<ChatParticipant> participants = JSONUtil.toList(participantListStr, ChatParticipant.class);
                    chatParticipantDao.saveAll(participants);
                }
                return null;
            });
        }
    }
}