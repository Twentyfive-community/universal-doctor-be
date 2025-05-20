package exception;

public class DoctorAlreadyAcceptedException extends RuntimeException {
    public DoctorAlreadyAcceptedException(String message) {
        super(message);
    }
}
