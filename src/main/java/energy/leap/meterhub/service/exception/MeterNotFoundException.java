package energy.leap.meterhub.service.exception;

public class MeterNotFoundException extends RuntimeException {
    public MeterNotFoundException(String message) {
        super(message);
    }
}
