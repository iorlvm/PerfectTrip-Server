package idv.tia201.g1.utils;

import idv.tia201.g1.dto.ChatRoomDTO;
import idv.tia201.g1.dto.ParticipantDTO;
import idv.tia201.g1.entity.ChatParticipant;
import idv.tia201.g1.entity.ChatRoom;

public class DtoConverter {
    public static ChatRoomDTO toChatRoomDTO (ChatRoom chatRoom, ChatParticipant chatUser) {
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

        return chatRoomDTO;
    }


    public static ParticipantDTO toParticipantDTO (ChatParticipant participant) {
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
}
