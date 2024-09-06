package idv.tia201.g1.product.service;


import idv.tia201.g1.product.entity.Product;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface RoomService {


    Product addNewRoom(Long companyId, MultipartFile photo,
                       String roomType,
                       int roomPrice, Long changeId) throws SQLException, IOException;

    List<String> getAllRoomTypes();

    List<Product> getAllRooms();

    byte[] getRoomPhotoByRoomId(Long roomId) throws SQLException;

    void deletRoom(Long roomId);

    Product updateRoom(Long roomId, String roomType, int roomPrice, byte[] photoByte);

    Optional<Product> getRoomById(Long roomId);
}
