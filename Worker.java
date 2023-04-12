import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class Worker extends Thread {

        Socket socketCliente;
        ArrayList<Socket> listaClientes;
        ObjectInputStream entrada;
        ObjectOutputStream salida;
        String usuario;

        public Worker() {

        }

        public Worker(Socket socketCliente, ArrayList<Socket> listaClientes) throws ClassNotFoundException, IOException {
                this.socketCliente = socketCliente;
                this.listaClientes = listaClientes;
        }

        private void contestar(String fraseCliente) throws IOException {

                String fraseMayusculas;

                try {
                        salida = new ObjectOutputStream(socketCliente.getOutputStream());
                } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }
                fraseMayusculas = "\t" + fraseCliente.toUpperCase() + "<" + usuario;
                System.out.println(fraseMayusculas);
                salida.writeObject(fraseMayusculas);

        }

        private void contestarTodos(String fraseCliente) throws IOException {

                String fraseMayusculas = "";
                fraseMayusculas = usuario + "> " + fraseCliente;
                System.out.println(fraseMayusculas);

                for (int i = 0; i < listaClientes.size(); i++) {
                        try {
                                salida = new ObjectOutputStream(listaClientes.get(i).getOutputStream());
                        } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                        }
                        if (listaClientes.get(i) != socketCliente) {
                                fraseMayusculas = "\n\t" + fraseCliente.toUpperCase() + " <" + usuario;
                        }else{
                                fraseMayusculas = "\t" + fraseCliente.toUpperCase() + " <" + usuario;
                        }
                        
                        salida.writeObject(fraseMayusculas);
                }
                

        }

        @Override
        public void run() {
                String fraseCliente = "";

                try {
                        entrada = new ObjectInputStream(socketCliente.getInputStream());
                        usuario = (String) entrada.readObject();
                } catch (IOException | ClassNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }
                

                do {
                        try {
                                fraseCliente = (String) entrada.readObject();
                                if (fraseCliente.contains("exit")) {
                                        contestar(fraseCliente);
                                } else {
                                        contestarTodos(fraseCliente);
                                }

                        } catch (IOException | ClassNotFoundException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                        }

                } while (!fraseCliente.contains("exit"));

                try {
                        socketCliente.close();
                        listaClientes.remove(socketCliente);
                } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }

        }

}