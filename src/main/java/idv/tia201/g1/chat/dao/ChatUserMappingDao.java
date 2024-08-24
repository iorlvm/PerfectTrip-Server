package idv.tia201.g1.chat.dao;

import idv.tia201.g1.entity.ChatUserMapping;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatUserMappingDao extends JpaRepository<ChatUserMapping, Long> {

    @Query("SELECT c.mappingUserId " +
            "FROM ChatUserMapping c " +
            "WHERE c.userType = :userType AND c.refId = :refId")
    Long findMappingUserIdByUserTypeAndRefId(@Param("userType") String userType, @Param("refId") Integer refId);
}
