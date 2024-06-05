package BeatBoxChat;

import java.io.EOFException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

public class BeatBoxServer {
    private ArrayList<ObjectOutputStream> clientOutputStreams;
    private ArrayList<Thread> clientHandlerThreads;
    private final int PORT = 5244;

    public static void main(String[] args){
        new BeatBoxServer().go();
    }

    public void go() {
        this.clientOutputStreams = new ArrayList<>();
        this.clientHandlerThreads = new ArrayList<>();

        try (ServerSocket serverSock = new ServerSocket(this.PORT)){
            String currentIP = InetAddress.getLocalHost().getHostAddress();
            System.out.println("Server Running on " + currentIP + ":" + serverSock.getLocalPort());
            while (true) {
                System.out.println("Listening for connection...");
                Socket clientSocket = serverSock.accept();
                System.out.println("Connection Received: " + clientSocket);

                ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                this.clientOutputStreams.add(out);

                Thread t = new Thread(new ClientHandler(clientSocket));
                this.clientHandlerThreads.add(t);
                t.start();
            }
        }
        catch (Exception ex) {ex.printStackTrace();}
    }

    public  void tellEveryone(Object one, Object two) {
        Iterator<ObjectOutputStream> it = this.clientOutputStreams.iterator();
        while(it.hasNext()) {
            try {
                ObjectOutputStream out = it.next();
                out.writeObject(one);
                out.flush();
                out.writeObject(two);
            }
            catch (Exception ex) {ex.printStackTrace();}
        }
    }

    public class ClientHandler implements Runnable {
        ObjectInputStream inputStream;
        Socket clientSocket;

        public ClientHandler(Socket socket){
            try{
                this.clientSocket = socket;
                this.inputStream = new ObjectInputStream(socket.getInputStream());
            }
            catch(Exception ex) {
                System.out.println("Problem adding Client!!");
            }
        } // end ClientHandler Constructor

        public void run() {
            Object o1, o2;
            try {
                while ((o1 = this.inputStream.readObject()) != null) {
                    o2 = this.inputStream.readObject();
                    System.out.println("Message Received From: " + this.clientSocket);
                    tellEveryone(o1, o2);
                }
            }
            catch (EOFException eof){
                System.out.println("Connection closed for client: " + this.clientSocket);
            }
            catch(Exception ex) {
                ex.printStackTrace();
            }
        } // end run()

    } // end ClientHandler class
}
