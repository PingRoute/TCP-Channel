public class ChannelException extends Exception {
  public ChannelException(String message) {
    super(message);
  }
  public ChannelException(String message, Exception ex) {
    super(message + "(" + ex.getMessage() + ")", ex.getCause());
  }
}