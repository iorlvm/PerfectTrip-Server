package idv.tia201.g1.product.exception;


import java.sql.SQLException;

public class InternalServerException extends RuntimeException {
    public InternalServerException(String message, SQLException e) {
        super(message);
    }
}
