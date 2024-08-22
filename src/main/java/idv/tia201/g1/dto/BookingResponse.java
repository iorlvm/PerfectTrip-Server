package idv.tia201.g1.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingResponse {

    private Long bookingId;

    private Date checkInDate;

    private Date checkOutDate;

    private String guestFullName;

    private String guestEmail;

    private int totalNumOfGuest;

    private int NumofAdults;

    private int NumOfChildren;

    private String bookingConfirmCode;

    private RoomResponse room;


    public BookingResponse(Long bookingId, Date checkInDate,
                           Date checkOutDate, String bookingConfirmCode) {
        this.bookingId = bookingId;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.bookingConfirmCode = bookingConfirmCode;
    }
}
