package idv.tia201.g1.product.exception;

/**
 * ClassName:InvalidBookingRequestException
 * Package: idv.tia201.g1.product.exception
 * Description:
 *
 * @Author: Jacob
 * @Create: 2024/8/28 - 下午3:38
 * @Version: v1.0
 */
public class InvalidBookingRequestException extends RuntimeException {
    public InvalidBookingRequestException(String message) {
        super(message);
    }
}
