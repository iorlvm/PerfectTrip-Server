package idv.tia201.g1.Room.service.impl;


import idv.tia201.g1.Room.dao.RoomDao;
import idv.tia201.g1.Room.exception.ResourceNotFoundException;
import idv.tia201.g1.Room.service.RoomService;
import idv.tia201.g1.entity.Room;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomDao roomDao;

    @Override
    public Room addNewRoom(MultipartFile photo, String roomType, int roomPrice) throws SQLException, IOException {
        Room room = new Room();
        room.setRoomType(roomType);
        room.setRoomPrice(roomPrice);
        if(!photo.isEmpty()) {
            byte[] photoBytes = photo.getBytes();
            Blob photoBlob = new SerialBlob(photoBytes);
            room.setPhoto(photoBlob);

        }
        return roomDao.save(room);
    }

    @Override
    public List<String> getAllRoomTypes() {


        return roomDao.findDistinctRoomTypes();
    }

    @Override
    public List<Room> getAllRooms() {

        return roomDao.findAll();
    }

    @Override
    public byte[] getRoomPhotoByRoomId(Long roomId) throws SQLException {
        Optional<Room> theRoom = roomDao.findById(Math.toIntExact(roomId));
        if(theRoom.isEmpty()){
            throw new ResourceNotFoundException("Sorry, Room not found");
        }
        Blob photoBlob = theRoom.get().getPhoto();

        if(photoBlob != null){
            return photoBlob.getBytes(1, (int) photoBlob.length());
        }
        return null;
    }
}
