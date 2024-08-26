package idv.tia201.g1.product.service;

import idv.tia201.g1.product.entity.BookedRoom;

import java.util.List;


public interface BookingService {
    List<BookedRoom> getAllBookingsByRoomId(Long roomId);
}
