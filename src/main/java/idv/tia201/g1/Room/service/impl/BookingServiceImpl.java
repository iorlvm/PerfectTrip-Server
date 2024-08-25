package idv.tia201.g1.Room.service.impl;

import idv.tia201.g1.Room.service.BookingService;
import idv.tia201.g1.Room.entity.BookedRoom;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {

    @Override
    public List<BookedRoom> getAllBookingsByRoomId(Long roomId) {
        return null;
    }
}
