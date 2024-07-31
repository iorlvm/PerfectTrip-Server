package idv.tia201.g1.chat.service.impl;

import idv.tia201.g1.chat.dao.ChatParticipantDao;
import idv.tia201.g1.chat.dao.ChatRoomDao;
import idv.tia201.g1.chat.dao.ChatUserMappingDao;
import idv.tia201.g1.chat.service.ChatService;
import idv.tia201.g1.dto.ChatRoomDTO;
import idv.tia201.g1.dto.ParticipantDTO;
import idv.tia201.g1.entity.ChatParticipant;
import idv.tia201.g1.entity.ChatRoom;
import idv.tia201.g1.entity.ChatUserMapping;
import idv.tia201.g1.utils.UserHolder;
import idv.tia201.g1.utils.redis.RedisIdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

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
    private RedisIdWorker idWorker;

    @Override
    public Page<ChatRoomDTO> getChatRooms(int page, int size) {
        // 取得登入用戶資料
        String type = UserHolder.getRole();
        Integer id = UserHolder.getId();

        if (type == null || id == null)
            throw new IllegalStateException("不合法的訪問: 請檢查您的登入狀態");

        // 分頁以及排序設定
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(
                        Sort.Order.desc("pinned"),
                        Sort.Order.desc("lastModifiedDate")
                )
        );

        // 根據登入用戶取得映射id
        ChatUserMapping userMapping = chatUserMappingDao.findByUserTypeAndRefId(type, id);
        if (userMapping == null) {
            // 用戶映射關係不存在, 創造一個映射關係
            createUserMapping(type, id); // TODO: 應該可以優化成開新執行緒去執行 (但意義好像不大)
            // 映射關係不存在, 表示之前完全沒使用過聊天室(不可能存在聊天列表), 回傳一個長度為0的page
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }
        // 根據映射id取得聊天室列表
        Long mappingId = userMapping.getMappingUserId();
        Page<ChatParticipant> result = chatParticipantDao.findByMappingUserId(mappingId, pageable);
        Long[] chatIdsForUser = result.getContent().stream()
                .map(ChatParticipant::getChatId)
                .toArray(Long[]::new);

        List<ChatRoomDTO> chatRoomDTOS = new ArrayList<>(size);
        // 根據列表中的聊天室id取得各個聊天室的參與者
        // 根據參與者映射id, 取得各個映射id對應的ref_id type
        for (Long chatId : chatIdsForUser) {
            // 利用chatId取得聊天室的詳細資料, 並寫入DTO物件
            ChatRoom chatRoom = chatRoomDao.findById(chatId).orElse(null);
            if (chatRoom == null) {
                // 理論上不可能進到這個條件中, chatId是FK
                throw new IllegalStateException("狀態異常: 沒有對應的聊天室");
            }

            // 取得聊天室中所有的參與者
            List<ChatParticipant> chatParticipants = chatParticipantDao.findByChatId(chatId);
            // TODO: 查詢結果放入redis緩存 (鍵:"cache:participant:chatId")
            //       先查詢redis, 不存在緩存才重建 (使用互斥鎖方案, 過期消失時間20秒?)

            List<ParticipantDTO> participantDTOS = new ArrayList<>(chatParticipants.size());

            for (ChatParticipant chatParticipant : chatParticipants) {
                // 利用參與者的映射id獲得ref_id type
                Long mappingUserId = chatParticipant.getMappingUserId();
                ChatUserMapping chatUserMapping = chatUserMappingDao.findById(mappingUserId).orElse(null);
                if (chatUserMapping == null) {
                    throw new IllegalArgumentException("狀態異常: 沒有對應的使用者映射資料");
                }

                Integer refId = chatUserMapping.getRefId();
                String userType = chatUserMapping.getUserType();
                // 根據type去搜尋對應的表格中與ref_id相同的id, 取得詳細資料
                // 將詳細資料補充寫入chatParticipant (暱稱以及頭像)

                // TODO: 查詢結果放入redis緩存 (鍵:"mappping:userType:refId", 過期時間增加少許亂數避免緩存雪崩)
                //       有可能被大量重複讀取, 使用邏輯過期以及自然淘汰的綜合策略 (副作用 有可能讀到更新前的資料, 但最多20秒後會更新)
                //       這個操作只是為了取得暱稱以及頭像, 資料的一致性要求沒有這麼高  這個副作用是可以被接受的
                //       未來提高一致性的做法, 當user資料變更時 將redis上的緩存資料刪除 (需其他組員配合)



            }

            // 將詳細資料寫入DTO物件
            ChatRoomDTO chatRoomDTO = new ChatRoomDTO();
            chatRoomDTO.setParticipants(participantDTOS);
            chatRoomDTOS.add(chatRoomDTO);
        }
        // 回傳
        return new PageImpl<>(chatRoomDTOS, pageable, result.getTotalElements());
    }

    private ChatUserMapping createUserMapping(String type, Integer id) {
        ChatUserMapping userMapping = new ChatUserMapping();
        userMapping.setUserType(type);
        userMapping.setRefId(id);
        return chatUserMappingDao.save(userMapping);
    }
}
