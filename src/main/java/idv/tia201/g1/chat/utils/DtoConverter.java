package idv.tia201.g1.chat.utils;

import idv.tia201.g1.chat.entity.ChatMessage;
import idv.tia201.g1.chat.entity.ChatParticipant;
import idv.tia201.g1.chat.entity.ChatRoom;
import idv.tia201.g1.core.entity.UserAuth;
import idv.tia201.g1.chat.dto.ChatRoomDTO;
import idv.tia201.g1.chat.dto.MessageDTO;
import idv.tia201.g1.chat.dto.ParticipantDTO;
import idv.tia201.g1.chat.dto.PayloadDTO;
import idv.tia201.g1.core.utils.Constants;
import idv.tia201.g1.member.entity.Admin;
import idv.tia201.g1.member.entity.Company;
import idv.tia201.g1.member.entity.User;

import static idv.tia201.g1.core.utils.Constants.BASE_URL;

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
        chatRoomDTO.setNotifySettings(chatUser.getNotify());
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
        if (participant.getAvatar() != null) {
            participantDTO.setAvatar(BASE_URL + participant.getAvatar());
        }
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
            imageDTO.setSrc(BASE_URL + message.getImg());
            messageDTO.setImg(imageDTO);
        }

        return messageDTO;
    }

    public static PayloadDTO.UserInfo toUserInfoDTO(UserAuth userAuth) {
        PayloadDTO.UserInfo userInfo = new PayloadDTO.UserInfo();
        switch (userAuth.getRole()) {
            case Constants.ROLE_USER:
                if (userAuth instanceof User user) {
                    userInfo.setAvatar(BASE_URL + user.getAvatar());
                    userInfo.setName(user.getNickname());
                }
                break;
            case Constants.ROLE_COMPANY:
                if (userAuth instanceof Company company) {
                    // TODO: 靜態寫死 之後要改
                    userInfo.setAvatar(BASE_URL +"image/74502663084965891");
                    userInfo.setName(company.getCompanyName());
                }
                break;
            case Constants.ROLE_ADMIN:
                // TODO: 先暫時給預設值 未來修正
                if (userAuth instanceof Admin admin) {
                    userInfo.setAvatar(BASE_URL +"image/74502663084965891");
                    userInfo.setName(admin.getAdminGroup());
                }
                break;
        }

        return userInfo;
    }
}
