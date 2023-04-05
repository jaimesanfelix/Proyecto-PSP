import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ClienteSocket {

    //private static final String DNSAWS = "ec2-3-237-199-99.compute-1.amazonaws.com";
    private static final String DNSAWS = "localhost";
    
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Socket socket;
        ObjectInputStream entrada;
        ObjectOutputStream eixida;
        String frase;
        String FraseRecibida;

        socket = new Socket(DNSAWS, 11000);
        
        do {
            eixida = new ObjectOutputStream(socket.getOutputStream());
            System.out.println("Introduce la frase a enviar en minúsculas");
        Scanner in = new Scanner(System.in);
        frase = in.nextLine();
        System.out.println("Se envia la frase " + frase);
        eixida.writeObject(frase);

        entrada = new ObjectInputStream(socket.getInputStream());
        FraseRecibida = (String) entrada.readObject();
        System.out.println(
                "La frase recibida es: " + FraseRecibida);
        } while (!frase.equalsIgnoreCase("exit"));
        
        socket.close();
    }
}