package energy.leap.meterhub.service.exception;

public class IllegalMeterBatchReadingXmlException extends RuntimeException {
    public IllegalMeterBatchReadingXmlException(String message) {
        super(message);
    }

    public IllegalMeterBatchReadingXmlException(String message, Throwable cause) {
        super(message, cause);
    }
}
