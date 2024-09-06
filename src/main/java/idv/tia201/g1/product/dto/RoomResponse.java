package idv.tia201.g1.product.dto;

import idv.tia201.g1.product.entity.Product;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;

import java.util.List;

@Data
@NoArgsConstructor
public class RoomResponse {

    private Long id;

    private String roomType;

    private int roomPrice;

    private boolean isBooked;

    private String photo;

    private List<BookingResponse> bookings;

    public RoomResponse(Long id, String roomType, int roomPrice) {
        this.id = id;
        this.roomType = roomType;
        this.roomPrice = roomPrice;
    }

    public RoomResponse(Long id, String roomType, int roomPrice, boolean isBooked,
                        byte[] photoBytes, List<BookingResponse> bookings) {
        this.id = id;
        this.roomType = roomType;
        this.roomPrice = roomPrice;
        this.isBooked = isBooked;
        this.photo = photoBytes != null ? Base64.encodeBase64String(photoBytes) : null;
        this.bookings = bookings;
    }

    public RoomResponse(Product theRoom) {

    }
}
