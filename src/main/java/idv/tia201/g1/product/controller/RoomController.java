//package idv.tia201.g1.product.controller;
//import idv.tia201.g1.product.exception.PhotoRetrieverException;
//import idv.tia201.g1.product.exception.ResourceNotFoundException;
//import idv.tia201.g1.product.service.RoomService;
//import idv.tia201.g1.product.dto.BookingResponse;
//import idv.tia201.g1.product.dto.RoomResponse;
//import idv.tia201.g1.product.entity.Product;
//import lombok.RequiredArgsConstructor;
//import org.apache.tomcat.util.codec.binary.Base64;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import javax.sql.rowset.serial.SerialBlob;
//import java.io.IOException;
//import java.sql.Blob;
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
///* 使用Lombok可以減少樣板編碼，包含 Getter,Setter,構造函數,toString(),equals(),hashCode()等方法
//*  此註釋自動生成包含 final 字段或是標註為 @NonNull 的構造函數，對於一個類有多個依賴項可以減少手動編寫構造函數
//* Spring 的 @Autowired */
//@RequiredArgsConstructor
//
///* Spring MVC 的註解，定義 HTTP 請求的 URL映射，表示這個類的所有方法將對應 /rooms 路徑
//* 的 HTTP 請求，包含 GET, POST, PUT, DELETE */
//@RequestMapping("/rooms")
//
///*
//* Spring MVC 的註解，為 @Controller 與 @ResponseBody 的結合體，
//* 標記一個類為 RESTful 網絡服務的控制器，並且它的所有方法返回的結果，
//* 都會直接寫入 HTTP 響應體中，而不是解析為一個視圖 View
//* */
//@RestController
//
//// 負責處理與房間相關的 HTTP 請求
//public class RoomController {
//
//    // 房間相關業務與預定相關業務邏輯
//    private final RoomService roomService;
//
//    /*
//     允許來自特定來源的跨域請求，跨域資源共享 CORS 允許不同域名之間的請求方法
//     來自前端的 HTTP 能訪問此控制器
//     */
//
//    @CrossOrigin(origins = "http://localhost:5173")
//
//    /*
//     * 處理 HTTP POST 請求的方法，調用來向服務器發送新房間添加請求的操作
//     */
//    @PostMapping("/add/new-room")
//
//    // 返回帶有 RoomResponse資料的 HTTP 回應實體
//    public ResponseEntity<RoomResponse> addNewRoom(
//
//        // @RequsetParam 表示這些參數的值會從 HTTP 請求中的請求參數中提取
//            @RequestParam("companyId") Long companyId,
//            @RequestParam("changeId") Long changeId,
//
//        // MultipartFile 用於處理資料上傳
//            @RequestParam("photo") MultipartFile photo,
//            @RequestParam("roomType") String roomType,
//            @RequestParam("roomPrice") int roomPrice) throws SQLException, IOException {
//
//        /* 調用了 roomService 的 addNewRoom 方法，將公司ID、房間照片、房間類型、
//         房間價格和變更ID 作為參數傳入。addNewRoom 方法返回一個保存好的房間 savedRoom 對象。
//        * */
//        Product savedRoom = roomService.addNewRoom(companyId, photo, roomType,
//                roomPrice,changeId);
//
//        /*
//         * 創建新的 RoomResponse 對象，並將剛剛保存的房間的ID、房間類型和房間價格設置為
//         * 這個回應對象的屬性。這個 RoomResponse 將被用來封裝響應給前端的數據。
//         */
//        RoomResponse roomResponse = new RoomResponse(
//                savedRoom.getId(),
//                savedRoom.getRoomType(),
//                savedRoom.getRoomPrice()
//        );
//        // 返回一個 HTTP 200 OK 的回應，其中包含了 roomResponse 對象。
//        return ResponseEntity.ok(roomResponse);
//    }
//
//
//    @CrossOrigin(origins = "http://localhost:5173")
//
//    // 標示了一個處理 HTTP GET 請求的方法，並且這個請求的路徑必須匹配 /room/types
//    @GetMapping("/room/types")
//
//    // List<String> 方法返回的是一個包含 String 類型的列表。這個列表會包含所有房間類型的名稱。
//    // getRoomTypes 則是獲取房間類型
//    public List<String> getRoomTypes() throws SQLException {
//        return roomService.getAllRoomTypes();
//    }
//
//    // 標示了一個處理 HTTP GET 請求的方法，並且這個請求的路徑必須匹配 /rooms/all
//    @GetMapping("/all")
//
//    /* 返回一個包含 RoomResponse 列表的 ResponseEntity 對象。ResponseEntity 是一個
//    用於表示 HTTP 回應的實體類型，包含狀態碼、表頭和回應本體。方法的功能是獲取所有房間的訊息
//    * */
//    public ResponseEntity<List<RoomResponse>> getAllRooms() throws SQLException {
//
//    // 從數據庫中獲取所有房間的列表
//        List<Product> rooms = roomService.getAllRooms();
//
//    // 創建了一個空的 ArrayList，這個列表用來存儲轉換後的 RoomResponse 對象。
//    // RoomResponse 是一個用於傳遞房間訊息的數據傳輸對象（DTO）。
//        List<RoomResponse> roomResponses = new ArrayList<>();
//
//
//        for (Product ele : rooms) {
//
//            /* 調用 roomService 的 getRoomPhotoByRoomId() 方法，根據當前房間的 ID
//            來獲取該房間的照片數據，並將其存儲在 photoByte 變量中。這個變量是一個字節數組，
//            表示房間照片的二進制數據。*/
//            byte[] photoByte = roomService.getRoomPhotoByRoomId(ele.getId());
//
//            /* 將當前的 Room 對象 ele 傳入，並生成一個 RoomResponse 對象。這個 RoomResponse
//             對象包含了該房間的基本信息，準備作為回應的一部分返回給客戶端。*/
//            RoomResponse roomResponse = getRoomResponse(ele);
//
//            /* 將房間的照片數據 photoByte 編碼為 Base64 字符串，並將結果存儲在 base64Photo 變量中。
//            Base64 編碼常用於在 JSON 或 XML 中傳輸二進制數據（如圖片）。
//            * */
//            if (photoByte != null && photoByte.length > 0) {
//                String base64Photo = Base64.encodeBase64String(photoByte);
//                roomResponse.setPhoto(base64Photo);
//
//            // 剛剛構建的 roomResponse 對象添加到 roomResponses 列表中，
//            // 這個列表最終會包含所有房間的信息。
//                roomResponses.add(roomResponse);
//            }
//        }
//        return ResponseEntity.ok(roomResponses);
//    }
//
//    private RoomResponse getRoomResponse(Product room) {
//
//        List<BookedRoom> bookings = getAllBookingsByRoomId(room.getId());
//        List<BookingResponse> bookingInfo = bookings
//                .stream()
//                .map(booking ->
//                        new BookingResponse(
//                                booking.getBookingId(),
//                                booking.getCheckInDate(),
//                                booking.getCheckOutDate(),
//                                booking.getBookingConfirmCode()))
//                .toList();
//        byte[] photoBytes = null;
//        Blob photoBlob = room.getPhoto();
//        if (photoBlob != null) {
//            try {
//                photoBytes = photoBlob.getBytes(1, (int) photoBlob.length());
//            } catch (SQLException e) {
//                throw new PhotoRetrieverException("Error retrieving photo");
//            }
//        }
//        return new RoomResponse(
//                room.getId(),
//                room.getRoomType(),  // roomType 對應 category
//                room.getRoomPrice(),  // roomPrice 對應 price
//                room.isBooked(), photoBytes, bookingInfo
//        );
//    }
//
//    private List<BookedRoom> getAllBookingsByRoomId(Long roomId) {
//        return bookingService.getAllBookingsByRoomId(roomId);
//    }
//
//    @DeleteMapping("delete/room/{roomId}")
//    public ResponseEntity<Void> deleteRoom(@PathVariable Long roomId) {
//        roomService.deletRoom(roomId);
//        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//    }
//
//    @PutMapping("/update/{roomId}")
//    public ResponseEntity<RoomResponse> updateRoom(@PathVariable Long roomId,
//                                                  @RequestParam(required = false) String roomType,
//                                                   @RequestParam(required = false) int roomPrice,
//                                                   @RequestParam(required = false) MultipartFile photo)
//            throws IOException, SQLException {
//                byte[] photoByte  = photo != null && !photo.isEmpty()?
//                        photo.getBytes() : roomService.getRoomPhotoByRoomId(roomId);
//                Blob photoBlob = photoByte != null && photoByte.length > 0 ?
//                        new SerialBlob(photoByte) : null;
//                Product theRoom = roomService.updateRoom(roomId,roomType,roomPrice,photoByte);
//                theRoom.setPhoto(photoBlob);
//
//                RoomResponse roomResponse = new RoomResponse(theRoom);
//                return ResponseEntity.ok(roomResponse);
//    }
//
//    @GetMapping("/room/{roomId}")
//    public ResponseEntity<Optional<RoomResponse>> getRoomById(@PathVariable Long roomId) {
//        Optional<Product> theRoom = roomService.getRoomById(roomId);
//        return theRoom.map(room -> {
//            RoomResponse roomResponse = getRoomResponse(room);
//            return ResponseEntity.ok(Optional.of(roomResponse));
//        }).orElseThrow(() -> new ResourceNotFoundException("room not found"));
//    }
//
//
//
//}
