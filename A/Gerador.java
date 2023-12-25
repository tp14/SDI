import java.io.*;
import java.net.*;

public class Gerador {
  public static void main(String[] args) {
    DatagramSocket socket = null;
    DatagramPacket packetOut = null;
    byte[] bufOut;
    final int PORT = 8888;
 
    try {
      socket = new DatagramSocket();
      String msg;
 
      msg = args[1];
      bufOut = msg.getBytes();

      InetAddress address = InetAddress.getByName("224.1.1.1");
      packetOut = new DatagramPacket(bufOut, bufOut.length, address, PORT);

      socket.send(packetOut);
      System.out.println("Server sends name: " + msg);

    } catch (IOException ioe) {
      System.out.println(ioe);
    }
  }
}