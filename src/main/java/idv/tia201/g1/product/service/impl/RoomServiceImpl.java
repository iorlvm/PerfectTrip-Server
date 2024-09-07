//package idv.tia201.g1.product.service.impl;
//
//import idv.tia201.g1.product.dao.RoomDao;
//import idv.tia201.g1.product.exception.InternalServerException;
//import idv.tia201.g1.product.exception.ResourceNotFoundException;
//import idv.tia201.g1.product.service.RoomService;
//import idv.tia201.g1.product.entity.Product;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//import javax.sql.rowset.serial.SerialBlob;
//import java.io.IOException;
//import java.sql.Blob;
//import java.sql.SQLException;
//import java.util.Date;
//import java.util.List;
//import java.util.Optional;
//
//@Service
//@RequiredArgsConstructor
//public class RoomServiceImpl implements RoomService {
//
//    private final RoomDao roomDao;
//
//    @Override
//    public Product addNewRoom(Long companyId,
//                              MultipartFile photo,
//                              String roomType,
//                              int roomPrice,
//                              Long changeId) throws SQLException, IOException {
//        Product room = new Product();
//        room.setCompanyId(companyId);
//
//        room.setRoomPrice(roomPrice);
//        room.setProductName("Default Product Name");
//        room.setChangeId(changeId);
//        room.setCreatedDate(new Date());
//        Date currentDate = new Date();
//        room.setLastModifiedDate(currentDate);
//        room.setCreatedDate(currentDate);
//        return roomDao.save(room);
//    }
//
//    @Override
//    public List<String> getAllRoomTypes() {
//        return roomDao.findDistinctRoomTypes();  // 獲取 category 的唯一值
//    }
//
//    @Override
//    public List<Product> getAllRooms() {
//        return roomDao.findAll();  // 從 product_master 表中獲取所有記錄
//    }
//
//    @Override
//    public byte[] getRoomPhotoByRoomId(Long roomId) throws SQLException {
//        Optional<Product> theRoom = roomDao.findById(Math.toIntExact(roomId));
//        if (theRoom.isEmpty()) {
//            throw new ResourceNotFoundException("Sorry, Room not found");
//        }
//        Blob photoBlob = theRoom.get().getPhoto();
//
//        if (photoBlob != null) {
//            return photoBlob.getBytes(1, (int) photoBlob.length());
//        }
//        return null;
//    }
//
//    @Override
//    public void deletRoom(Long roomId) {
//        Optional<Product> theRoom = roomDao.findById(Math.toIntExact(roomId));
//        if (theRoom.isPresent()) {
//            roomDao.deleteById(Math.toIntExact(roomId));  // 刪除 product_master 表中的記錄
//        }
//    }
//
//    @Override
//    public Product updateRoom(Long roomId, String roomType, int roomPrice, byte[] photoByte) {
//        Product room = roomDao.findById(Math.toIntExact(roomId))
//                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));
//        if (roomType != null) {
//            room.setRoomType(roomType);
//        }
//        if (roomPrice > 0) {
//            room.setRoomPrice(roomPrice);
//        }
//        if (photoByte != null && photoByte.length > 0) {
//            try {
//                room.setPhoto(new SerialBlob(photoByte));
//            } catch (SQLException e) {
//                throw new InternalServerException("Error updating photo");
//            }
//
//
//        }
//        return roomDao.save(room);
//    }
//
//    @Override
//    public Optional<Product> getRoomById(Long roomId) {
//        return Optional.of(roomDao.findById(Math.toIntExact(roomId)).get());
//    }
//
//
//}
