package response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

@Data
public class ResponseWrapper<T> {
    private boolean success;
    private String message;
    private int statusCode;
    private LocalDateTime timestamp;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    public ResponseWrapper(boolean success, String message, int statusCode, T data) {
        this.success = success;
        this.message = message;
        this.statusCode = statusCode;
        this.timestamp = LocalDateTime.now();
        this.data = data;
    }

    public static <T> ResponseWrapper<T> success(T data, String message) {
        return new ResponseWrapper<>(true, message, 200, data);
    }

    public static <T> ResponseWrapper<T> failure(String message, int statusCode) {
        return new ResponseWrapper<>(false, message, statusCode, null);
    }

    public static <T> ResponseWrapper<T> noContent(String message, int statusCode) {
        return new ResponseWrapper<>(false, message, statusCode, null);
    }

    protected ResponseEntity<ResponseWrapper<Void>> unauthorized(String message) {
        return ResponseEntity.status(401).body(ResponseWrapper.failure(message, 401));
    }

    protected ResponseEntity<ResponseWrapper<Void>> forbidden(String message) {
        return ResponseEntity.status(403).body(ResponseWrapper.failure(message, 403));
    }


}
