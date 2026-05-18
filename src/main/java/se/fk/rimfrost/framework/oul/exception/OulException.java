package se.fk.rimfrost.framework.oul.exception;

public class OulException extends Exception
{

   public enum ErrorType
   {
      NOT_FOUND, BAD_REQUEST, SERVICE_UNAVAILABLE, UNEXPECTED_ERROR
   }

   private final ErrorType errorType;

   public OulException(ErrorType errorType, String message)
   {
      super(message);
      this.errorType = errorType;
   }

   public OulException(ErrorType errorType, String message, Throwable cause)
   {
      super(message, cause);
      this.errorType = errorType;
   }

   public ErrorType getErrorType()
   {
      return errorType;
   }
}
