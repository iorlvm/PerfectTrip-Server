package idv.tia201.g1.product.service.impl;

import idv.tia201.g1.product.service.BookingService;
import idv.tia201.g1.product.entity.BookedRoom;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {

    @Override
    public List<BookedRoom> getAllBookingsByRoomId(Long roomId) {
        return null;
    }
}
