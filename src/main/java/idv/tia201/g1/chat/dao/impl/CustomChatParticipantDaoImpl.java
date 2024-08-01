package idv.tia201.g1.chat.dao.impl;

import idv.tia201.g1.chat.dao.CustomChatParticipantDao;
import idv.tia201.g1.entity.ChatParticipant;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static idv.tia201.g1.utils.Constants.*;

@Repository
public class CustomChatParticipantDaoImpl implements CustomChatParticipantDao {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<ChatParticipant> findByChatId(Long chatId) {
        String queryStr = "SELECT " +
                "    p.mapping_user_id AS user_id, " +
                "    CASE " +
                "        WHEN m.user_type = '" + ROLE_USER + "' THEN u.nickname " +
                "        WHEN m.user_type = '" + ROLE_COMPANY + "' THEN c.company_name " +
                "        WHEN m.user_type = '" + ROLE_ADMIN + "' THEN '平台管理員' " +
                "    END AS name, " +
                "    CASE " +
                "        WHEN m.user_type = '" + ROLE_USER + "' THEN u.avatar " +
                "        WHEN m.user_type = '" + ROLE_COMPANY + "' THEN '' " +
                "        WHEN m.user_type = '" + ROLE_ADMIN + "' THEN '' " +
                "    END AS avatar, " +
                "    m.user_type AS type, " +
                "    p.pinned, " +
                "    p.notify, " +
                "    p.unread_messages, " +
                "    p.last_reading_at " +
                "FROM chat_user_mappings m " +
                "LEFT JOIN user_master u ON " +
                "    m.ref_id = u.user_id AND m.user_type = '" + ROLE_USER + "' " +
                "LEFT JOIN company_master c ON " +
                "    m.ref_id = c.company_id AND m.user_type = '" + ROLE_COMPANY + "' " +
                "LEFT JOIN admin_master a ON " +
                "    m.ref_id = a.admin_id AND m.user_type = '" + ROLE_ADMIN + "' " +
                "JOIN chat_participants p ON " +
                "    p.mapping_user_id = m.mapping_user_id " +
                "JOIN chat_rooms r ON " +
                "    p.chat_id = r.chat_id " +
                "WHERE r.chat_id = :chatId";

        Query query = entityManager.createNativeQuery(queryStr);
        query.setParameter("chatId", chatId);

        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();
        List<ChatParticipant> list = new ArrayList<>();

        for (Object[] result : results) {
            ChatParticipant participant = getParticipant(result);
            list.add(participant);
        }

        return list;
    }

    @Override
    public Page<Long> findChatIdByTypeAndRefId(String type, Integer refId, Pageable pageable) {
        String queryStr = "SELECT " +
                "    result.chat_id, " +
                "    result.total_count " +
                "FROM ( " +
                "    SELECT " +
                "        p.chat_id, " +
                "        p.last_modified_date, " +
                "        p.pinned, " +
                "        COUNT(*) OVER() AS total_count " +
                "    FROM chat_participants p " +
                "    JOIN chat_user_mappings m ON " +
                "        p.mapping_user_id = m.mapping_user_id " +
                "    WHERE m.ref_id = :refId AND m.user_type = :type " +
                ") AS result " +
                "ORDER BY " +
                "    result.pinned DESC, " +
                "    result.last_modified_date DESC " +
                "LIMIT :pageSize OFFSET :offset;";

        Query query = entityManager.createNativeQuery(queryStr);
        query.setParameter("refId", refId);
        query.setParameter("type", type);
        query.setParameter("pageSize", pageable.getPageSize());
        query.setParameter("offset", pageable.getOffset());

        @SuppressWarnings("unchecked")
        List<Object[]> resultList = query.getResultList();

        List<Long> chatIds = resultList.stream()
                .map(row -> ((Number) row[0]).longValue())
                .toList();

        long total = resultList.isEmpty() ? 0L : ((Number) resultList.get(0)[1]).longValue();

        return new PageImpl<>(chatIds, pageable, total);
    }

    private static ChatParticipant getParticipant(Object[] result) {
        ChatParticipant participant = new ChatParticipant();
        participant.setMappingUserId((Long) result[0]);
        participant.setName((String) result[1]);
        participant.setAvatar((String) result[2]);
        participant.setType((String) result[3]);
        participant.setPinned((Boolean) result[4]);
        participant.setNotify((String) result[5]);
        participant.setUnreadMessages((Integer) result[6]);
        participant.setLastReadingAt((Timestamp) result[7]);
        return participant;
    }
}
