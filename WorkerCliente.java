import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class WorkerCliente extends Thread {

    Socket socket;
    ObjectInputStream entrada;
    String usuario;

    public WorkerCliente(){

    }

    public WorkerCliente(Socket socket, String usuario) throws IOException {
        this.socket = socket;
        this.usuario = usuario;
    }

    @Override
    public void run() {
        
        String fraseRecibida = "";
        do {
            try {
                entrada = new ObjectInputStream(socket.getInputStream());
                fraseRecibida = (String) entrada.readObject();
                System.out.println(fraseRecibida);
                System.out.print(usuario + "> ");
            } catch (ClassNotFoundException | IOException e) {
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
