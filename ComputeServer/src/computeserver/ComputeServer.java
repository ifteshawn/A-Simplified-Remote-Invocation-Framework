/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package computeserver;

import Contract.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;


/**
 *
 * @author Ifte
 */
public class ComputeServer {

    /**
     * @param args the command line arguments
     */
    private static int clientCount;
    
    public static void main(String[] args) {
        //Thread for establishing a listening socket on port 6789 for Object transfer
        new Thread(() -> {
            try {
                int serverPort = 6789;
                ServerSocket listenSocket = new ServerSocket(serverPort);
                int i = 0;
                while (true) {
                    Socket clientSocket = listenSocket.accept();
                    clientCount++;
                    Connection1 c = new Connection1(clientSocket, i++, clientCount);
                }
            } catch (IOException e) {
                System.out.println("Listen socket:" + e.getMessage());
            }
        }).start();
        System.out.println("The server is listening on port 6789 for object transfer...");
        System.out.println("------------------------------");

        //Thread for establishing a listening socket on port 6789 for File transfer
        new Thread(() -> {
            try {
                int serverPort = 6790;
                ServerSocket listenSocket = new ServerSocket(serverPort);
                int i = 0;
                while (true) {
                    Socket clientSocket = listenSocket.accept();
                    clientCount++;
                    Connection c = new Connection(clientSocket, i++, clientCount);
                }
            } catch (IOException e) {
                System.out.println("Listen socket:" + e.getMessage());
            }
        }).start();
        System.out.println("The server is listening on port 6790 for file transfer...");
        System.out.println("------------------------------");
    }
}

//Connection class for File transfer
class Connection extends Thread {

    InputStream in;
    OutputStream out;
    Socket clientSocket;
    int thrdn;
    int clientCount;

    public Connection(Socket aClientSocket, int tn, int client) {

        try {
            thrdn = tn;
            clientSocket = aClientSocket;
            in = clientSocket.getInputStream();
            //Start the thread
            this.start();
        } catch (IOException e) {
            System.out.println("Connection:" + e.getMessage());
        }
    }

    public void run() {
        String ClassName = null;
        while (true) {
            try {
                //Construct data input stream to receive class files
                DataInputStream clientData = new DataInputStream(in);
                //Receive the class file name
                ClassName = clientData.readUTF();
                //Receive the class file length
                int size = clientData.readInt();
                //Construct a byte array to receive the class file
                byte[] buffer = new byte[size];
                int bytesRead = clientData.read(buffer, 0, buffer.length);
                //Construct a file output stream to save the class file
                FileOutputStream fo = new FileOutputStream(ClassName);
                try (BufferedOutputStream bos = new BufferedOutputStream(fo)) {
                    bos.write(buffer, 0, bytesRead);
                }
                System.out.println("The class file of " + ClassName + " has been downloaded.");
            } catch (EOFException e) {
                System.out.println("EOF" + e.getMessage());
                break;
            } catch (FileNotFoundException ex) {
                System.out.println("File " + ClassName + " cannot find.");
                break;
            } catch (SocketException e) {
                System.out.println("Client closed.");
                break;
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
    
    
    
    
 //Connection class for establishing Object transfer   
 class Connection1 extends Thread {

    ObjectInputStream in;
    ObjectOutputStream out;
    Socket clientSocket;
    int thrdn;
    int clientCount;

    public Connection1(Socket aClientSocket, int tn, int client) {

        try {
            thrdn = tn;
            clientSocket = aClientSocket;
            in = new ObjectInputStream(clientSocket.getInputStream());
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            this.start();
        } catch (IOException e) {
            System.out.println("Connection:" + e.getMessage());
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                //Read and cast the compute object into The Task interface
                Task ts = (Task) in.readObject();//in is the object input stream

                System.out.println("Performing a client task of " + ts.getClass().getName());

                System.out.println("------------------------------");

                //Execute the task
                ts.executeTask();

                //Send the compute object back
                out.writeObject(ts);

            } catch (EOFException e) {
                break;

            } catch (IOException e) {
                System.out.println("Writing object failed: " + e.getMessage());
                break;

                //The class file has not been uploaded
            } catch (ClassNotFoundException ex) {

                try {

                    System.out.println("The compute-task " + ex.getMessage() + " cannot be found!");

                    //Construct an error message
                    String emg = "Please upload the compute-task " + ex.getMessage() + " class before calling the server";

                    CSMessage misc = new CSMessage();

                    misc.setMessage(emg);

                    //Send the error message
                    out.writeObject(misc);

                } catch (IOException e) {
                    System.out.println("Writing object failed: " + e.getMessage());
                }
            }
        }
    }
}
