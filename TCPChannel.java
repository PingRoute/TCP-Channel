import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Timer;
import java.util.TimerTask;
public abstract class TCPChannel extends Channel1 implements Runnable {
  private int port = 0;
  private String server = null;
  private ServerSocket serverSocket = null;
  private Timer clientTimer = null;
  //Constructor, to be used by the server.
  public TCPChannel(int port) {
    super();
    this.port = port;
    new Thread(this).start();
  }
  //Constructor, to be used by the clients.
  public TCPChannel(String server, int port) throws ChannelException {
    super();
    assert (server!= null);
    this.port = port;
    this.server =server;
    this.sendMessage(new Message1(Message1.Type.Register, "", -1));
    while(this.channelID == -1)
      try {
      Thread.sleep(100);
    }catch(InterruptedException e) {
      throw new ChannelException("Error while waiting for the new channelID", e);
    }
    clientTimer = new Timer();
    clientTimer.scheduleAtFixedRate(new TimerTask() {
      public void run() {
        try{
          sendMessage(new Message1(Message1.Type.CheckMessage, "", channelID));
        }catch(ChannelException e) {
          e.printStackTrace();
        }
      }
    }, 1000, 1000);
  }
  protected void sendMessage(Message1 message) throws ChannelException {
    if(this.server != null) {
      try {
        Socket socket = new Socket(this.server, port);
        PrintWriter out= new PrintWriter(socket.getOutputStream());
        BufferedReader in= new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out.println(message.toString());
        out.flush();
        String input =in.readLine();
        socket.close();
        this.messageReceived(new Message1(input));
      }catch(IOException e) {
        throw new ChannelException("Error sending message", e);
      }
    }else
      this .pendingMessages.get(message.getChannelID()).add(message);
  }
  public void run() {
    assert(this.server == null);
    try {
      serverSocket = new ServerSocket(this.port);
    } catch(IOException e) {
      System.err.println("Unable to open server port: " + e.getMessage());
      return;
    }
    while(true) {
      Socket socket;
      try {
        socket = serverSocket.accept();
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream());
        String input = in.readLine();
        Message1 reply = this.messageReceived(new Message1(input));
        out.println(reply.toString());
        out.flush();
        out.close();
        in.close();
      }catch(SocketException e) {
        if (e.getMessage().equalsIgnoreCase("Socket closed")) {
          break;
        }
        System.err.println("Unable to accept connection 1: " + e.getMessage());
        break;
      }catch(IOException e) {
        System.err.println("Unable to accept connection 2: " + e.getMessage());
        break;
      } catch(ChannelException e) {
        e.printStackTrace();
      }
    }
  }
  public void close() throws ChannelException {
    if (this.server == null) {
      assert(serverSocket != null);
      try { 
        if(!serverSocket.isClosed())
          serverSocket.close();
      } catch(SocketException e) {
      } catch(IOException e) {
        throw new ChannelException("Unable to close the channel", e);
      }
    } else {
      assert(clientTimer != null);
      clientTimer.cancel();
    }
  }
}
        
             
                                    
      