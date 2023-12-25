import java.io.*;
import java.net.*;

public class Consumidor {
  public static void main(String[] args) {
    MulticastSocket socket = null;
    DatagramPacket packetIn = null;
    byte[] bufIn = new byte[256];
    try {
      socket = new MulticastSocket(8888);
      InetAddress address = InetAddress.getByName("224.1.1.1");

      socket.joinGroup(address);
      
      while(true) {
        packetIn = new DatagramPacket(bufIn, bufIn.length);
        socket.receive(packetIn);
        String msg = new String(bufIn, 0, packetIn.getLength()).toUpperCase();
        System.out.println("From " + packetIn.getAddress() + " Msg : " + msg);
      }

    } catch (IOException ioe) {
      System.out.println(ioe);
    }
  }
}