package idv.tia201.g1.Room.service;

import idv.tia201.g1.Room.entity.BookedRoom;

import java.util.List;


public interface BookingService {
    List<BookedRoom> getAllBookingsByRoomId(Long roomId);
}
