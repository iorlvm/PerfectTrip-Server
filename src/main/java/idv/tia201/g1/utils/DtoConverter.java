package idv.tia201.g1.utils;

import idv.tia201.g1.authentication.service.UserAuth;
import idv.tia201.g1.dto.ChatRoomDTO;
import idv.tia201.g1.dto.MessageDTO;
import idv.tia201.g1.dto.ParticipantDTO;
import idv.tia201.g1.dto.PayloadDTO;
import idv.tia201.g1.entity.*;

import static idv.tia201.g1.utils.Constants.*;

public class DtoConverter {
    public static ChatRoomDTO toChatRoomDTO(ChatRoom chatRoom, ChatParticipant chatUser) {
        if (chatRoom == null || chatUser == null)
            throw new IllegalArgumentException("參數異常: 聊天室與使者用不得為空");

        ChatRoomDTO chatRoomDTO = new ChatRoomDTO();
        chatRoomDTO.setChatId(chatRoom.getChatId());
        chatRoomDTO.setChatName(chatRoom.getChatName());
        chatRoomDTO.setUnreadMessages(chatUser.getUnreadMessages());
        chatRoomDTO.setLastMessage(chatRoom.getLastMessage());
        chatRoomDTO.setLastMessageAt(chatRoom.getLastMessageAt());
        chatRoomDTO.setPhoto(chatRoom.getPhoto());
        chatRoomDTO.setNotifSettings(chatUser.getNotify());
        chatRoomDTO.setPinned(chatUser.getPinned());
        chatRoomDTO.setLastModifiedAt(chatRoom.getLastModifiedDate());

        return chatRoomDTO;
    }


    public static ParticipantDTO toParticipantDTO(ChatParticipant participant) {
        if (participant == null)
            throw new IllegalArgumentException("參數異常: participant不得為空");

        ParticipantDTO participantDTO = new ParticipantDTO();
        participantDTO.setUserId(participant.getMappingUserId());
        participantDTO.setName(participant.getName());
        participantDTO.setType(participant.getType());
        participantDTO.setAvatar(participant.getAvatar());
        participantDTO.setLastReadingAt(participant.getLastReadingAt());

        return participantDTO;
    }

    public static MessageDTO toMessageDTO(ChatMessage message) {
        if (message == null)
            throw new IllegalArgumentException("參數異常: message不得為空");

        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setMessageId(message.getMessageId());
        messageDTO.setSenderId(message.getMappingUserId());
        messageDTO.setContent(message.getContent());
        messageDTO.setTimestamp(message.getCreatedDate().toString());
        if (message.getImg() != null) {
            MessageDTO.ImageDTO imageDTO = new MessageDTO.ImageDTO();
            imageDTO.setSrc(message.getImg());
            messageDTO.setImg(imageDTO);
        }

        return messageDTO;
    }

    public static PayloadDTO.UserInfo toUserInfoDTO(UserAuth userAuth) {
        PayloadDTO.UserInfo userInfo = new PayloadDTO.UserInfo();
        switch (userAuth.getRole()) {
            case ROLE_USER:
                if (userAuth instanceof User user) {
                    userInfo.setAvatar("image/74502663084965891");
                    userInfo.setName(user.getNickname());
                }
                break;
            case ROLE_COMPANY:
                if (userAuth instanceof Company company) {
                    userInfo.setAvatar("image/74502663084965891");
                    userInfo.setName(company.getCompanyName());
                }
                break;
            case ROLE_ADMIN:
                // TODO: 先暫時給預設值 未來修正
                userInfo.setAvatar("image/74502663084965891");
                userInfo.setName("管理員");
                break;
        }

        return userInfo;
    }
}
