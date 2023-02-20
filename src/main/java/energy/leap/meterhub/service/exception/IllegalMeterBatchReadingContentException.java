package energy.leap.meterhub.service.exception;

public class IllegalMeterBatchReadingContentException extends RuntimeException {
    public IllegalMeterBatchReadingContentException(String message) {
        super(message);
    }
}
