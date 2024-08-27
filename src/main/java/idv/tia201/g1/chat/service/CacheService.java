package idv.tia201.g1.chat.service;

import idv.tia201.g1.chat.dto.MessageDTO;
import idv.tia201.g1.chat.entity.ChatMessage;
import idv.tia201.g1.chat.entity.ChatParticipant;
import idv.tia201.g1.chat.entity.ChatRoom;
import idv.tia201.g1.chat.entity.ChatUserMapping;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

public interface CacheService {
    Long findMappingUserId(String role, Integer id);

    /**
     * 對使用者創建一個映射關係 (並更新緩存中的值)
     *
     * @param role 使用者身分
     * @param id 使用者id
     * @return 使用者的mapping關係
     */
    ChatUserMapping createUserMapping(String role, Integer id);

    /**
     * 判斷是否為有效的聊天室
     *
     * @param chatId 聊天室id
     * @return 該聊天室是否存在
     */
    boolean isChatRoomInvalid(Long chatId);

    ChatRoom findChatRoomByChatId(Long chatId);

    /**
     * 獲取聊天室中所有的參與者列表
     *
     * @param chatId 聊天室id
     * @return 參與者列表
     */
    List<ChatParticipant> getChatParticipantsByChatId(Long chatId);

    /**
     * 判斷是否為該聊天室的參與者
     *
     * @param mappingUserId 使用者的mappingId
     * @param chatId 聊天室id
     * @return 是否為參與者
     */
    boolean isParticipantNotFound(Long mappingUserId, Long chatId);

    /**
     * 獲取使用者的參與中的聊天室id列表 (for參與連線使用, 所以沒有順序需求)
     *
     * @param role 使用者身分
     * @param id 使用者id
     * @return 參與中的聊天室id列表
     */
    Set<Long> getChatRoomIdsByRoleAndId(String role, Integer id);

    /**
     * 更新聊天室參與者的最後閱讀時間
     *
     * @param chatId 聊天室id
     * @param mappingUserId 使用者的id
     * @param time 現在時間
     */
    void updateLastReadingAt(Long chatId, Long mappingUserId, Timestamp time);

    /**
     * 更新聊天室參與者的聊天室設定
     *
     * @param chatId 聊天室id
     * @param mappingUserId 使用者的id
     * @param pinned 釘選設定
     * @param notify 通知設定
     */
    void updateChatSettings(Long chatId, Long mappingUserId, Boolean pinned, String notify);

    /**
     * 對聊天室的名稱或是照片進行修改
     *
     * @param chatId 聊天室的id
     * @param chatName 更新的聊天室名稱 (null時忽略)
     * @param chatPhoto 更新的聊天室照片 (null時忽略)
     */
    void updateChatInfo(Long chatId, String chatName, String chatPhoto);

    /**
     * 將訊息存到緩存中, 並放到消息隊列進行資料庫的異步寫入
     *
     * @param senderId 發送訊息者的id
     * @param chatId 聊天室的id
     * @param messageDTO 發送的訊息
     * @return 處理後的訊息
     */
    MessageDTO saveMessage(Long senderId, Long chatId, MessageDTO messageDTO);

    /**
     * 從緩存中取得暫存中的聊天訊息
     *
     * @param chatId 聊天室id
     * @return 暫存中的訊息
     */
    List<ChatMessage> getMessages(long chatId);
}
