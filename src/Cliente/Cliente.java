/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Cliente;

import AES.GeneratePrivateKey;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

/**
 *
 * @author misa
 */
public class Cliente {
   public static String IP_SERVER;
   principal vent;
   DataInputStream entrada = null;
   DataOutputStream salida = null;
   DataInputStream entrada2 = null;
   Socket comunication = null;//para la comunicacion
   Socket comunication2 = null;//para recibir msg
   String nomCliente;
   String resp_duplicada;
   GeneratePrivateKey pk;
   Integer num_intentos = 0;
   
   JPasswordField pf = new JPasswordField();
   
    public Cliente(principal vent) throws IOException
   {      
      this.vent=vent;
   }
    
    public void conexion() throws IOException 
   {
        try {
                comunication = new Socket(Cliente.IP_SERVER, 8081);
                comunication2 = new Socket(Cliente.IP_SERVER, 8082);
                entrada = new DataInputStream(comunication.getInputStream());
                salida = new DataOutputStream(comunication.getOutputStream());
                entrada2 = new DataInputStream(comunication2.getInputStream());
            do{
                nomCliente = JOptionPane.showInputDialog("Introducir Nick :", "");
                if(nomCliente == null){
                    System.exit(0);
                }
                if(nomCliente.length()==0){
                    JOptionPane.showMessageDialog(null, "Debes especificar un nombre de usuario");
                }
                if(nomCliente.length()>0){
                    salida.writeUTF(nomCliente);
                    resp_duplicada = entrada.readUTF();
                    if(resp_duplicada.equals("true")){
                        JOptionPane.showMessageDialog(null, "Nombre de usuario ya existente en el Chat, escriba otro.");
                    }
                }
            }while(nomCliente.length()==0 || resp_duplicada.equals("true"));
            vent.setUsuario(nomCliente);
            this.setFalseLlave();
            genPrivateKey();
        }catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Servidor no encontrado.");
            System.out.println("\tEl servidor no esta levantado");
            Cliente.IP_SERVER = JOptionPane.showInputDialog("Introducir IP del Servidor :","localhost");
            principal w = new principal();
            w.setVisible(true);
        }
        new threadCliente(entrada2, vent).start();
   } 
    public String getNombre()
   {
       String n = nomCliente.toString();
       return n;
   }
    
    public void setPK(String pk){
        vent.cif_dec_key = pk;
    }
    
    public void setFalseLlave(){
        try{
            //Enviamos a thread Servidor
            System.out.println("Reiniciando Valores de Todos los usuarios Dentro menos yo mismo");
            salida.writeInt(6);
        }catch(IOException ex){
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void genPrivateKey(){
        //Generando Private Key de 32 bits
        pk = new GeneratePrivateKey();
        String pvk = pk.beta.toString();
        vent.p = pk.p ;
        vent.q = pk.alpha;
        this.setPK(pvk);
       try{
            //Enviamos a thread Servidor
            salida.writeInt(5);
            salida.writeUTF(pvk);
        }catch(IOException ex){
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void enviaPrivateKey(String pvk){
        try{
            //Enviamos a thread Servidor
            salida.writeInt(5);
            salida.writeUTF(pvk);
        }catch(IOException ex){
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //Lista de Usuarios actuales
   public Vector<String> pedirUsuarios()
   {
      Vector<String> users = new Vector();
      try {
          //Se envia al ThreadServidor
         salida.writeInt(2);
         int numUsers=entrada.readInt();
         for(int i=0;i<numUsers;i++)
            users.add(entrada.readUTF());
      } catch (IOException ex) {
         Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
      }
      return users;
   }
   
   //Eliminar usuario
   public void eliminaUsuario(String usuario){
       try{
           //Envio de Variables al Thread del Servidor
           salida.writeInt(4);
           salida.writeUTF(usuario);
       }catch(IOException e){
           System.out.println("Error eliminando "+ e);
       }
   }
   
   //Se envia un mensaje
   public void flujo(String mens) 
   {
      try {             
         System.out.println("el mensaje enviado desde el cliente es :" + mens);
         salida.writeInt(1);
         salida.writeUTF(mens);
      } catch (IOException e) {
         System.out.println("error...." + e);
      }
   }
   
   //Se envia un mensaje privado
   public void flujo(String amigo,String mens) 
   {
      try {             
         System.out.println("el mensaje enviado desde el cliente es :" + mens);
         salida.writeInt(3);//opcion de mensage a amigo
         salida.writeUTF(amigo);
         salida.writeUTF(mens);
      } catch (IOException e) {
         System.out.println("error...." + e);
      }
   }
}
