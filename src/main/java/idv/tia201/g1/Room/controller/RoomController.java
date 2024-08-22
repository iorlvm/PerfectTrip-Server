package idv.tia201.g1.Room.controller;

import idv.tia201.g1.Room.service.RoomService;
import idv.tia201.g1.dto.RoomResponse;
import idv.tia201.g1.entity.Room;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/rooms")
@RestController
public class RoomController {

    private final RoomService roomService;

    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping("/add/new-room")
    public ResponseEntity<RoomResponse> addNewRoom(
            @RequestParam("photo") MultipartFile photo,
            @RequestParam("roomType") String roomType,
            @RequestParam("roomPrice") int roomPrice) throws SQLException, IOException {

        Room savedRoom = roomService.addNewRoom(photo, roomType,roomPrice);
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
        return roomService.getAllRoomTypes();
    }


}
