import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;
import java.net.InetAddress;

public class JavaUdpClient {

    public static void main(String args[]) throws Exception
    {
        System.out.println("JAVA UDP CLIENT");
        DatagramSocket socket = null;
        int portNumber = 9008;

        Scanner scanner = new Scanner(System.in);

        while (true) {
            String sendMsg = scanner.nextLine();
            try {
                socket = new DatagramSocket();
                InetAddress address = InetAddress.getByName("localhost");
                byte[] sendBuffer = sendMsg.getBytes();
                byte[] receiveBuffer = new byte[20];

                DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, address, portNumber);
                socket.send(sendPacket);

                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                socket.receive(receivePacket);

                String reply = new String(receivePacket.getData());
                InetAddress replyAddress = receivePacket.getAddress();

                System.out.println("received \"" + reply + "\" from: " + replyAddress);
            }
            catch(Exception e){
                e.printStackTrace();
            }
            finally {
                if (socket != null) {
                    socket.close();
                }
            }
        }
    }
}
