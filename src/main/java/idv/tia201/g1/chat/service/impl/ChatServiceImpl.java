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
import idv.tia201.g1.utils.DtoConverter;
import idv.tia201.g1.utils.UserHolder;
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
        ChatUserMapping userMapping = chatUserMappingDao.findByUserTypeAndRefId(type, id);
        if (userMapping == null) {
            // 用戶映射關係不存在, 創造一個映射關係
            createUserMapping(type, id); // TODO: 應該可以優化成開新執行緒去執行 (但意義好像不大)
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
            ChatRoom chatRoom = chatRoomDao.findById(chatId).orElse(null);
            // TODO: 查詢結果(DTO)放入redis緩存 (鍵:"cache:chatroom:chatId")
            //       先查詢redis, 不存在緩存才重建 (使用互斥鎖方案, 過期消失時間20秒?)
            if (chatRoom == null) {
                // chatId是FK, 理論上不可能進到這個條件中
                throw new IllegalStateException("狀態異常: 沒有對應的聊天室");
            }

            // 取得聊天室中所有的參與者
            List<ChatParticipant> chatParticipants = chatParticipantDao.findByChatId(chatId);
            List<ParticipantDTO> participantDTOS = new ArrayList<>(chatParticipants.size());

            ChatParticipant loginChatUser = null;
            for (ChatParticipant chatParticipant : chatParticipants) {
                // 將chatParticipant 轉換成 participantDTO物件
                if (loginChatUser == null && type.equals(chatParticipant.getType()) && id.equals(chatParticipant.getUserId())) {
                    // 搜尋當下登入的使用者
                    loginChatUser = chatParticipant;
                }
                ParticipantDTO participantDTO = DtoConverter.toParticipantDTO(chatParticipant);
                participantDTOS.add(participantDTO);
            }

            // 將詳細資料寫入DTO物件
            ChatRoomDTO chatRoomDTO = DtoConverter.toChatRoomDTO(chatRoom, loginChatUser);
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
