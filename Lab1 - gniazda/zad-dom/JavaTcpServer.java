import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class JavaTcpServer {

    public static void main(String[] args) throws IOException {

        System.out.println("JAVA TCP SERVER");
        int portNumber = 12345;
        ServerSocket serverSocket = null;
        ArrayList<Socket> clients = new ArrayList<Socket>();

        try {
            // create socket
            serverSocket = new ServerSocket(portNumber);

            for(int i=0; i<3; i++)
            {
                // accept client
                Socket clientSocket = serverSocket.accept();
                clients.add(clientSocket);
            }
            for(int i=0; i<clients.size(); i++) {
                // new thread
                ClientRunnableGet clientRunnableGet = new ClientRunnableGet(clients.get(i));
                ClientRunnableSend clientRunnableSend = new ClientRunnableSend(clients.get(i), clients);
                new Thread(clientRunnableGet).start();
                new Thread(clientRunnableSend).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally{
            if (serverSocket != null){
                serverSocket.close();
            }
        }
    }

    private static class ClientRunnableGet implements Runnable {
        private final Socket clientSocket;

        public ClientRunnableGet(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        public void run() {
            // in & out streams
            BufferedReader in = null;
            try {
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));

                // read msg, send response
                String msg;
                while((msg = in.readLine()) != null) {
                    System.out.println("lol");
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                try {
                    if(in != null) {
                        in.close();
                        clientSocket.close();
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static class ClientRunnableSend implements Runnable {
        private final Socket clientSocket;
        private final ArrayList<Socket> clients;

        public ClientRunnableSend(Socket clientSocket, ArrayList<Socket> clients) {
            this.clientSocket = clientSocket;
            this.clients = new ArrayList<Socket>();
            for(int i = 0; i<clients.size(); i++) {
                if(clients.get(i) != clientSocket) {
                    this.clients.add(clients.get(i));
                    System.out.println(clients.get(i).getPort());
                }
            }
        }

        public void run() {
            // in & out streams
            ArrayList<PrintWriter> out = new ArrayList<PrintWriter>();
            BufferedReader in = null;
            try {
                for(int i=0; i<this.clients.size(); i++) {
                    out.add(new PrintWriter(this.clients.get(i).getOutputStream(), true));
                }

                // read msg, send response
                char[] buffer = new char[30];
                int charsRead = 0;
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));
                while((charsRead = in.read(buffer)) != -1) {
                    String msg = new String(buffer).substring(0, charsRead);
                    for(int i=0; i<out.size(); i++) {
                        out.get(i).println(clientSocket.getPort() + ": " + msg);
                    }
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                try {
                    if(out != null) {
                        for(int i=0; i<out.size(); i++) {
                            out.get(i).close();
                        }
                    }
                    if(in != null) {
                        in.close();
                        clientSocket.close();
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
