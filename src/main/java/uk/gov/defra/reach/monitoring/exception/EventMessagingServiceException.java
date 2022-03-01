package uk.gov.defra.reach.monitoring.exception;

public class EventMessagingServiceException extends Exception {

  private static final long serialVersionUID = 6533604079796191012L;

  public EventMessagingServiceException(String message, Exception cause) {
    super(message, cause);
  }
}
