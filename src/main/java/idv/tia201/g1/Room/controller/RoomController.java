package idv.tia201.g1.Room.controller;

import idv.tia201.g1.Room.exception.PhotoRetrieverException;
import idv.tia201.g1.Room.service.BookingService;
import idv.tia201.g1.Room.service.RoomService;
import idv.tia201.g1.Room.dto.BookingResponse;
import idv.tia201.g1.Room.dto.RoomResponse;
import idv.tia201.g1.Room.entity.BookedRoom;
import idv.tia201.g1.Room.entity.Room;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/rooms")
@RestController
public class RoomController {

    private final RoomService roomService;
    private final BookingService bookingService;

    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping("/add/new-room")
    public ResponseEntity<RoomResponse> addNewRoom(
            @RequestParam("companyId") Long companyId,
            @RequestParam("changeId") Long changeId,
            @RequestParam("photo") MultipartFile photo,
            @RequestParam("roomType") String roomType,
            @RequestParam("roomPrice") int roomPrice) throws SQLException, IOException {

        Room savedRoom = roomService.addNewRoom(companyId, photo, roomType, roomPrice,changeId);
        RoomResponse roomResponse = new RoomResponse(
                savedRoom.getId(),
                savedRoom.getRoomType(),
                savedRoom.getRoomPrice()
        );
        return ResponseEntity.ok(roomResponse);
    }

    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping("/room/types")
    public List<String> getRoomTypes() throws SQLException {
        return roomService.getAllRoomTypes();  // 獲取 category
    }

    @GetMapping("/all")
    public ResponseEntity<List<RoomResponse>> getAllRooms() throws SQLException {
        List<Room> rooms = roomService.getAllRooms();
        List<RoomResponse> roomResponses = new ArrayList<>();

        for (Room ele : rooms) {
            byte[] photoByte = roomService.getRoomPhotoByRoomId(ele.getId());
            RoomResponse roomResponse = getRoomResponse(ele);

            if (photoByte != null && photoByte.length > 0) {
                String base64Photo = Base64.encodeBase64String(photoByte);
                roomResponse.setPhoto(base64Photo);
                roomResponses.add(roomResponse);
            }
        }
        return ResponseEntity.ok(roomResponses);
    }

    private RoomResponse getRoomResponse(Room room) {

        List<BookedRoom> bookings = getAllBookingsByRoomId(room.getId());
        List<BookingResponse> bookingInfo = bookings
                .stream()
                .map(booking ->
                        new BookingResponse(
                                booking.getBookingId(),
                                booking.getCheckInDate(),
                                booking.getCheckOutDate(),
                                booking.getBookingConfirmCode()))
                .toList();
        byte[] photoBytes = null;
        Blob photoBlob = room.getPhoto();
        if (photoBlob != null) {
            try {
                photoBytes = photoBlob.getBytes(1, (int) photoBlob.length());
            } catch (SQLException e) {
                throw new PhotoRetrieverException("Error retrieving photo");
            }
        }
        return new RoomResponse(
                room.getId(),
                room.getRoomType(),  // roomType 對應 category
                room.getRoomPrice(),  // roomPrice 對應 price
                room.isBooked(), photoBytes, bookingInfo
        );
    }

    private List<BookedRoom> getAllBookingsByRoomId(Long roomId) {
        return bookingService.getAllBookingsByRoomId(roomId);
    }

    @DeleteMapping("delete/room/{roomId}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long roomId) {
        roomService.deletRoom(roomId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
