import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class ServidorSocket {
    
    private static final int PORT=11000;

    public static void main(String[] args) throws Exception {

        ServerSocket serverSocket;
        Socket clientSocket;
        ArrayList<Socket> listaClientes = new ArrayList<>();
        
        serverSocket = new ServerSocket(PORT);
        System.out.println("Server iniciado y escuchando en el puerto "+ PORT);
        System.out.println("Server esperando clientes...");
        while (true) {
            clientSocket = serverSocket.accept();
            listaClientes.add(clientSocket);
            Worker w = new Worker(clientSocket, listaClientes);
            w.start();
            
           
        }
        
    }
}