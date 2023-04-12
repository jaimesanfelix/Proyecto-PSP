import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ClienteSocket {

    // private static final String DNSAWS =
    // "ec2-3-237-199-99.compute-1.amazonaws.com";
    private static final String DNSAWS = "localhost";

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Socket socket;
        ObjectOutputStream salida;
        String frase;
        
        socket = new Socket(DNSAWS, 11000);
        salida = new ObjectOutputStream(socket.getOutputStream());

        Scanner sc = new Scanner(System.in);
        System.out.print("Introduce tu usuario: ");
        String usuario = sc.nextLine();
        String u1 = usuario.substring(0, 1).toUpperCase();
        String nombreUsuario = u1 + usuario.substring(1);
        salida.writeObject(nombreUsuario);
        
        WorkerCliente wc = new WorkerCliente(socket, nombreUsuario);
        wc.start();
        System.out.print(nombreUsuario + "> ");

        do {
            
            frase = sc.nextLine();
            //System.out.println(nombreUsuario + ">" + frase);
            salida.writeObject(frase);
        } while (!frase.contains("exit"));

    }
}