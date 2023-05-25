import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.Key;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

import utils.KeysManager;
import utils.RSAReceiver;
import utils.RSASender;

public class Worker extends Thread {

        Socket socketCliente;
        HashMap<Socket, String> listaClientes;
        ObjectInputStream entrada;
        ObjectOutputStream salida;
        String usuario;
        Key clavePrivada;
        Timestamp tiempoUsuario;
        String[] listaComandos = {"!ping", "@user", "!userList", "!deleteUser", "!userTime", "!serverTime", "!listaComandos"};

        public Worker() {

        }

        public Worker(Socket socketCliente, HashMap<Socket, String> listaClientes) throws Exception {
                this.socketCliente = socketCliente;
                this.listaClientes = listaClientes;
                this.clavePrivada = KeysManager.getClavePrivada();
                this.tiempoUsuario = new Timestamp(System.currentTimeMillis());
        }

        private void contestar(String fraseCliente) throws Exception {

                String fraseAEnviar;

                try {
                        salida = new ObjectOutputStream(socketCliente.getOutputStream());
                } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }
                fraseAEnviar = "\t" + fraseCliente + "<" + usuario;
                System.out.println(fraseAEnviar);
                salida.writeObject(RSASender.cipher(fraseAEnviar, clavePrivada));

        }

        private void contestarTodos(String fraseCliente) throws Exception {

                String fraseAEnviar = "";
                fraseAEnviar = usuario + "> " + fraseCliente;
                System.out.println(fraseAEnviar);

                for(Socket cliente:listaClientes.keySet()) {
                        try {
                                salida = new ObjectOutputStream(cliente.getOutputStream());
                        } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                        }
                        if (cliente != socketCliente) {
                                fraseAEnviar = "\n\t" + fraseCliente + " <" + usuario;
                        }else{
                                fraseAEnviar = "\t" + fraseCliente + " <" + usuario;
                        }
                        
                        salida.writeObject(RSASender.cipher(fraseAEnviar, clavePrivada));
                }                

        }


        private void contestarUsuario(String usuario, String fraseCliente) throws Exception {

                String fraseAEnviar;
                Socket socketUsuario = null;

                String u1 = usuario.substring(0, 1).toUpperCase();
                usuario = u1 + usuario.substring(1);
                for(Socket cliente:listaClientes.keySet()) {
                     if (listaClientes.get(cliente).equals(usuario)) {
                        socketUsuario = cliente;
                     }   
                }
                if (socketUsuario == null) {
                        contestar("El usuario " + usuario + " no existe");
                        return;
                }

                try {
                        salida = new ObjectOutputStream(socketUsuario.getOutputStream());
                } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }
                fraseAEnviar = "\t" + fraseCliente + "<" + usuario;
                System.out.println(fraseAEnviar);
                salida.writeObject(RSASender.cipher(fraseAEnviar, clavePrivada));

        }


        @Override
        public void run() {
                String fraseCliente = "";

                try {
                        entrada = new ObjectInputStream(socketCliente.getInputStream());
                        usuario = new String(RSAReceiver.decipher((byte[])entrada.readObject(), clavePrivada));
                        listaClientes.put(socketCliente, usuario);
                } catch (IOException | ClassNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }
                

                do {
                        try {
                                fraseCliente = new String(RSAReceiver.decipher((byte[])entrada.readObject(), clavePrivada));
                                if (fraseCliente.startsWith("!") || fraseCliente.startsWith("@")) {
                                        ejecutarComandos(fraseCliente);
                                } else if (fraseCliente.contains("exit")) {
                                        contestar(fraseCliente);
                                } else {
                                        contestarTodos(fraseCliente);
                                }

                        } catch (IOException | ClassNotFoundException e) {
                                // TODO Auto-generated catch block
                                System.out.println("El usuario " + usuario + " ha sido eliminado");
                                return;
                        } catch (Exception e) {
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

        private void ejecutarComandos(String comando) throws Exception{

                String mensaje;
                //Eliminamos los espacios al inicio y al final de la frase
                comando = comando.trim();
                if (comando.startsWith("!ping")) {
                        mensaje = comando.substring(comando.indexOf(" ") + 1);
                        contestarTodos("**" + mensaje.toUpperCase() + "**");
                }else if (comando.startsWith("@")) {
                        String user = comando.substring(1, comando.indexOf(" "));
                        mensaje = comando.substring(comando.indexOf(" "));
                        contestarUsuario(user, mensaje);
                }else if(comando.startsWith("!userList")){
                        mensaje = "";
                        for(Socket cliente:listaClientes.keySet()) {
                                mensaje += listaClientes.get(cliente) + ", ";
                           }
                        contestar(mensaje.substring(0, mensaje.length() - 2));
                }else if(comando.startsWith("!deleteUser")){
                        Socket socketUsuario = null;
                        String user = comando.substring(comando.indexOf(" ") + 1);
                        System.out.println("-" + user + "-");
                        for(Socket cliente:listaClientes.keySet()) {
                                if (listaClientes.get(cliente).equals(user)) {
                                   socketUsuario = cliente;
                                   System.out.println("-" + listaClientes.get(cliente) + "-");
                                }   
                           }
                           if (socketUsuario == null) {
                                   contestar("El usuario " + user + " no existe");          
                           }else{
                                contestarTodos("El usuario " + user + " va a ser eliminado");
                                contestarUsuario(user, "exit");
                                listaClientes.remove(socketUsuario);
                           }
           
                }else if(comando.startsWith("!userTime")){
                        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                        mensaje = "Llevas conectado " + (timestamp.getTime() - tiempoUsuario.getTime()) / 1000.0 + " segundos";
                        contestar(mensaje);
                }else if(comando.startsWith("!serverTime")){   
                        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                        mensaje = "El servidor lleva activo " + (timestamp.getTime() - ServidorSocket.tiempoServidor.getTime()) / 1000.0 + " segundos";
                        contestar(mensaje);
                }else if(comando.startsWith("!listaComandos")){
                        String listaAEnviar = "";   
                        for (int i = 0; i < listaComandos.length; i++) {
                                listaAEnviar += listaComandos[i] + ", ";
                        }
                        contestar(listaAEnviar.substring(0, listaAEnviar.length() - 2));
                }else {
                        mensaje = "El comando " + comando + " es desconocido";
                        contestar(mensaje);
                }

        }

}