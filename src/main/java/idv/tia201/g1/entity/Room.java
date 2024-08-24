package idv.tia201.g1.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.awt.print.Book;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String roomType;
    private int roomPrice;
    private boolean isBooked = false;

    @Lob
    private Blob photo;

    @OneToMany(mappedBy ="room", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<BookedRoom> bookings;

    public Room() {
        this.bookings = new ArrayList<>();
    }

    public void addBooking(BookedRoom booking){
        if(bookings == null){
            bookings = new ArrayList<>();
        }
        bookings.add(booking);
        booking.setRoom(this);
        isBooked = true;
        long bookingCode = (long)(Math.random() * 1e10);
        String bookingCode1 = Long.toString(bookingCode);
        booking.setBookingConfirmCode(bookingCode1);
    }
}
