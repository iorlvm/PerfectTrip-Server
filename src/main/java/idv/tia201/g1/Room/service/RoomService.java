package idv.tia201.g1.Room.service;


import idv.tia201.g1.dto.RoomResponse;
import idv.tia201.g1.entity.Room;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public interface RoomService {


    Room addNewRoom(MultipartFile photo,
                    String roomType,
                    int roomPrice) throws SQLException, IOException;

    List<String> getAllRoomTypes();
}
