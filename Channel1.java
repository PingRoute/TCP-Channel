import java.util.ArrayList;
import java.util.HashMap;
public abstract class Channel1 {
  private MessageListener messageListener = null;
  private int seqGenerator = 0;
  protected HashMap<Integer, ArrayList<Message1>> pendingMessages = new HashMap<Integer, ArrayList<Message1>>();
  protected int channelID = -1;
  public Channel1() {
  }
  public int getID() {
    return channelID;
  }
  protected abstract void sendMessage(Message1 message) throws ChannelException;
  public void sendMessage1(String message) throws ChannelException {
    assert (message != null);
    sendMessage(new Message1(Message1.Type.NewMessage, message, this.channelID));
  }
  public void setMessageListener(MessageListener messageListener) {
    assert (messageListener != null);
    this.messageListener = messageListener;
  }
  public void sendMessage2(String message, int clientID) throws ChannelException {
    assert(message != null);
    assert(clientID >=0);
    sendMessage(new Message1(Message1.Type.NewMessage, message, clientID));
  }
  private Message1 checkMessageReceived(Message1 message) {
    assert (message != null);
    ArrayList<Message1> messageList = pendingMessages.get(message.getChannelID());
    assert(messageList != null);
    if(messageList.size() >0)
      return messageList.remove(0);
    return new Message1(Message1.Type.NoMessage, "", this.channelID);
  }
  protected Message1 messageReceived(Message1 message) throws ChannelException {
    assert(message != null);
    switch (message.getType()) {
      case Added:
        this.channelID = Integer.parseInt(message.getValue());
        break;
      case CheckMessage:
        return checkMessageReceived(message);
      case MessageReceived:
        this.messageListener.messageReceived(message.getValue(), message.getChannelID());
        break;
      case NewMessage:
        this.messageListener.messageReceived(message.getValue(), message.getChannelID());
        return new Message1(Message1.Type.Acknowledge, "", -1);
      case NoMessage:
        break;
      case Register:
        int newChannelID= seqGenerator++;
        pendingMessages.put(newChannelID, new ArrayList<Message1>());
        return new Message1(Message1.Type.Added, "" + newChannelID, -1);
      case Acknowledge:
      break;
      default:
        throw new ChannelException("Invalid message type received: " + message.getType());
    }
    return null;
  }
  public abstract void close() throws ChannelException;
}
  
      