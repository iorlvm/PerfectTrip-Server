package idv.tia201.g1.order.uitls;

import idv.tia201.g1.order.dao.OrderDao;

import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class OrderUtil {

    public static List<Double> getDiscountByCompanyIdBetweenStartDateAnEndDate(OrderDao orderDao, Integer companyId, Date startDate, Date endDate) {
        List<Date> datesBetween = getDatesBetween(startDate, endDate);
        List<Double> res = new ArrayList<>(datesBetween.size());
        for (Date date : datesBetween) {
            Double discount = orderDao.getDiscountByCompanyIdAndDate(companyId, date);
            if (discount != null) res.add(discount);
            else res.add(1.0);
        }
        return res;
    }

    public static long getDaysBetween(Date startDate, Date endDate) {
        LocalDate startLocalDate = startDate.toLocalDate();
        LocalDate endLocalDate = endDate.toLocalDate();
        return ChronoUnit.DAYS.between(startLocalDate, endLocalDate);
    }

    public static int calculateTotalDiscountedPrice(int dailyPrice, List<Double> discounts) {
        double totalDiscountedPrice = 0.0;

        for (Double discount : discounts) {
            totalDiscountedPrice += dailyPrice * discount;
        }

        return (int) Math.round(totalDiscountedPrice);
    }

    public static List<Date> getDatesBetween(Date startDate, Date endDate) {
        List<Date> dates = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);

        while (calendar.getTime().before(endDate)) {
            dates.add(new Date(calendar.getTimeInMillis()));
            calendar.add(Calendar.DATE, 1);
        }

        return dates;
    }
}
