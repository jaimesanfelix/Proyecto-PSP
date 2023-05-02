import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.security.Key;

import utils.KeysManager;
import utils.RSAReceiver;

public class WorkerCliente extends Thread {

    Socket socket;
    ObjectInputStream entrada;
    String usuario;
    Key clavePublica;

    public WorkerCliente(){

    }

    public WorkerCliente(Socket socket, String usuario) throws Exception {
        this.socket = socket;
        this.usuario = usuario;
        this.clavePublica = KeysManager.getClavePublica();
    }

    @Override
    public void run() {
        
        String fraseRecibida = "";
        do {
            try {
                entrada = new ObjectInputStream(socket.getInputStream());
                fraseRecibida = new String(RSAReceiver.decipher((byte[])entrada.readObject(), clavePublica));
                System.out.println(fraseRecibida);
                System.out.print(usuario + "> ");
            } catch (ClassNotFoundException | IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } while (!fraseRecibida.contains("EXIT"));
        try {
            socket.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }

}
