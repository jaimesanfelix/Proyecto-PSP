import java.io.*;
import java.net.*;
import java.security.Key;
import java.util.Scanner;

import utils.KeysManager;
import utils.RSASender;

public class ClienteSocket {

    //private static final String DNSAWS = "ec2-3-237-240-193.compute-1.amazonaws.com";
    private static final String DNSAWS = "localhost";

    public static void main(String[] args) throws Exception {
        Socket socket;
        ObjectOutputStream salida;
        String frase;
        Key clavePublica;
        
        socket = new Socket(DNSAWS, 11000);
        salida = new ObjectOutputStream(socket.getOutputStream());
        clavePublica = KeysManager.getClavePublica();

        Scanner sc = new Scanner(System.in);
        System.out.print("Introduce tu usuario: ");
        String usuario = sc.nextLine();
        String u1 = usuario.substring(0, 1).toUpperCase();
        String nombreUsuario = u1 + usuario.substring(1);
        salida.writeObject(RSASender.cipher(nombreUsuario, clavePublica));
        
        WorkerCliente wc = new WorkerCliente(socket, nombreUsuario);
        wc.start();
        System.out.print(nombreUsuario + "> ");

        do {
            
            frase = sc.nextLine();
            //System.out.println(nombreUsuario + ">" + frase);
            salida.writeObject(RSASender.cipher(frase, clavePublica));
        } while (!frase.contains("exit"));

    }
}