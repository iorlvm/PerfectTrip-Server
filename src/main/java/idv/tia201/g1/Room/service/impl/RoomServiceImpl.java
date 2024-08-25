package idv.tia201.g1.Room.service.impl;

import idv.tia201.g1.Room.dao.RoomDao;
import idv.tia201.g1.Room.exception.ResourceNotFoundException;
import idv.tia201.g1.Room.service.RoomService;
import idv.tia201.g1.Room.entity.Room;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomDao roomDao;

    @Override
    public Room addNewRoom(Long companyId, MultipartFile photo, String roomType, int roomPrice, Long changeId) throws SQLException, IOException {
        Room room = new Room();
        room.setCompanyId(companyId);
        room.setRoomType(roomType);  // 這裡仍然可以使用 roomType，因為在實體類中已經映射到 category
        room.setRoomPrice(roomPrice); // 同理，這裡可以保持不變
        room.setProductName("Default Product Name");
        room.setChangeId(changeId);
        room.setCreateDate(new Date());

        Date currentDate = new Date();
        room.setLastModifiedDate(currentDate);
        room.setCreatedDate(currentDate);

        if (!photo.isEmpty()) {
            byte[] photoBytes = photo.getBytes();
            Blob photoBlob = new SerialBlob(photoBytes);
            room.setPhoto(photoBlob);  // 將照片設置到對應欄位
        }
        return roomDao.save(room);  // 儲存到 product_master 表
    }

    @Override
    public List<String> getAllRoomTypes() {
        return roomDao.findDistinctRoomTypes();  // 獲取 category 的唯一值
    }

    @Override
    public List<Room> getAllRooms() {
        return roomDao.findAll();  // 從 product_master 表中獲取所有記錄
    }

    @Override
    public byte[] getRoomPhotoByRoomId(Long roomId) throws SQLException {
        Optional<Room> theRoom = roomDao.findById(Math.toIntExact(roomId));
        if (theRoom.isEmpty()) {
            throw new ResourceNotFoundException("Sorry, Room not found");
        }
        Blob photoBlob = theRoom.get().getPhoto();

        if (photoBlob != null) {
            return photoBlob.getBytes(1, (int) photoBlob.length());
        }
        return null;
    }

    @Override
    public void deletRoom(Long roomId) {
        Optional<Room> theRoom = roomDao.findById(Math.toIntExact(roomId));
        if (theRoom.isPresent()) {
            roomDao.deleteById(Math.toIntExact(roomId));  // 刪除 product_master 表中的記錄
        }
    }
}
