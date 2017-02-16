class Message1 {
  public enum Type {
    Register, Added, NewMessage, CheckMessage, MessageReceived, NoMessage, Acknowledge; }
  private Type type;
  private String value;
  private int channelID = -1;
  //Constructor, used for received messages.
  public Message1(String messageString) {
    String[] messageParts = messageString.split(":", 3);
    type = Type.valueOf(messageParts[0]);
    channelID = Integer.parseInt(messageParts[1]);
    value = messageParts[2];
  }
  //Constructor, used for message to be send.
  public Message1(Type type, String value, int channelID) {
    this.type = type;
    this.value = value;
    this.channelID = channelID;
  }
  public Type getType() {
    return type;
  }
  public String getValue() {
    return value;
  }
  public String toString() {
    return this.type.toString() + ":" + this.channelID + ":" + this.value;
  }
  public int getChannelID() {
    return this.channelID;
  }
}