import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class Worker{
    
        Socket socketCliente;
        ArrayList<Socket> listaClientes;
        ObjectInputStream entrada;
        ObjectOutputStream salida;

    
        public Worker(){
            
        }
        
        
        public Worker(Socket socketCliente, ArrayList<Socket> listaClientes) throws ClassNotFoundException, IOException{
                this.socketCliente = socketCliente;
                this.listaClientes = listaClientes;

                chatBot();
    
                
    
                 
        }


        private void contestar(String FraseCliente) throws IOException{

                String FraseMayusculas;

                try {
                        salida = new ObjectOutputStream(socketCliente.getOutputStream());
                } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }
                FraseMayusculas = FraseCliente.toUpperCase();
                System.out.println("El server devuelve la frase: " + FraseMayusculas);
                salida.writeObject(FraseMayusculas);

        }
        
        private void contestarTodos(String FraseCliente) throws IOException{

                
                String FraseMayusculas;
                
                for (int i = 0; i < listaClientes.size(); i++) {
                        try {
                                salida = new ObjectOutputStream(listaClientes.get(i).getOutputStream());
                        } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                        }
                        FraseMayusculas = FraseCliente.toUpperCase();
                        System.out.println("El server devuelve la frase: " + FraseMayusculas);
                        salida.writeObject(FraseMayusculas);
                }
                
                
        }
        

        private void chatBot() throws ClassNotFoundException, IOException{
                String FraseCliente = "";

                do {
                        try {
                                entrada = new ObjectInputStream(socketCliente.getInputStream());
                                FraseCliente = (String) entrada.readObject();
                                System.out.println("La frase recibida es: " + FraseCliente);
                                contestarTodos(FraseCliente);
                        } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                        }
                       
            
                        
                          
                } while (!FraseCliente.equalsIgnoreCase("exit"));
                

                socketCliente.close(); 

        }
        
    
}