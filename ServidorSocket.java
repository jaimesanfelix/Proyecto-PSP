import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;

public class ServidorSocket {
    
    private static final int PORT=11000;

    public static void main(String[] args) throws Exception {

        ServerSocket serverSocket;
        Socket clientSocket;
        HashMap<Socket, String> listaClientes = new HashMap<>();
        
        serverSocket = new ServerSocket(PORT);
        System.out.println("Server iniciado y escuchando en el puerto "+ PORT);
        System.out.println("Server esperando clientes...");
        while (true) {
            clientSocket = serverSocket.accept();
            listaClientes.put(clientSocket, null);
            Worker w = new Worker(clientSocket, listaClientes);
            w.start();
            
           
        }
        
    }
}