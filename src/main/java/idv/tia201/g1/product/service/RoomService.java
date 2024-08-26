package idv.tia201.g1.product.service;


import idv.tia201.g1.product.entity.Room;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public interface RoomService {


    Room addNewRoom(Long companyId, MultipartFile photo,
                    String roomType,
                    int roomPrice, Long changeId) throws SQLException, IOException;

    List<String> getAllRoomTypes();

    List<Room> getAllRooms();

    byte[] getRoomPhotoByRoomId(Long roomId) throws SQLException;

    void deletRoom(Long roomId);
}
