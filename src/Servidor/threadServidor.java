
package Servidor;

import java.net.*;
import java.io.*;
import java.net.*;
import java.util.*;

/**
 *
 * @author misa
 * Esta seccion manejara mensajes para la consola del SERVIDOR, todos los mensajes
 * requeridos se mandaran a la ventana de la consola del servidor, no tiene
 * nada que ver con el threadCliente, unicamente se elimina en un vector
 * de servidor mas no el threadCliente
 */
public class threadServidor extends Thread{
    Socket scli = null;
    Socket scli2 = null;
    DataInputStream entrada=null;
    DataOutputStream salida=null;
    DataOutputStream salida2=null;
    public static Vector<threadServidor> clientesActivos=new Vector();	
    String nameUser;
    Servidor serv;
    boolean usu_duplicado;
    
    //Constructor del socket
    public threadServidor(Socket scliente, Socket scliente2, Servidor serv){
        scli = scliente;
        scli2 = scliente2;
        this.serv = serv;
        nameUser = "";
        //Agregar al arreglo de 'Clientes'
        clientesActivos.add(this);        
        serv.mostrarEnConsola("Conexion del socket creada: " + this);			
    }
    
    //getters
    public String getNameUser(){
        return nameUser;
    }
    
    //Setters
    public void setNameUser(String name)
     {
       nameUser=name;
     }
    
    public void run(){
        int opcion=0, numUsers=0;
        String amigo, mencli, usuario;
        serv.mostrarEnConsola("En espera de mensajes");
        try{
            entrada=new DataInputStream(scli.getInputStream());
            salida=new DataOutputStream(scli.getOutputStream());
            salida2=new DataOutputStream(scli2.getOutputStream());
            String name = entrada.readUTF();
            usu_duplicado = usuariosDuplicados(name);
            if( usu_duplicado == false){
                this.setNameUser(name);
                enviaUserActivos();
                salida.writeUTF("false");
            }else{
                salida.writeUTF("true");
            }
        }catch(IOException e){
            e.printStackTrace();
        }
        if(usu_duplicado == false){
            while(true){
                try{
                    //Tipo de opcion
                    String nombre;
                    opcion=entrada.readInt();
                    System.out.println("Opcion servidor "+opcion);
                    //Las opciones las recibe el Thread Cliente.
                    switch(opcion){
                        case 1:
                            //envio de mensage a todos
                            mencli = entrada.readUTF();
                            serv.mostrarEnConsola("Mensaje recibido " + mencli);
                            enviaMsg(mencli);
                            break;
                        case 2:
                            //envio de lista de activos
                            serv.mostrarEnConsola("Enviando lista de activos ");
                            numUsers = clientesActivos.size();
                            salida.writeInt(numUsers);
                            for (int i = 0; i < numUsers; i++) {
                                salida.writeUTF(clientesActivos.get(i).nameUser);
                            }
                            break;
                        case 3:
                            // envia mensaje a uno solo
                            amigo = entrada.readUTF();//captura nombre de amigo
                            mencli = entrada.readUTF();//mensage enviado
                            enviaMsg(amigo, mencli);
                            break;
                        case 4: //Envia Elimina usuario
                            usuario = entrada.readUTF();
                            System.out.println("Switch servidor 4 "+usuario);
                            numUsers = clientesActivos.size();
                            //For unicamente para mostrar enla consola
                            for (int i = 0; i < numUsers; i++) {
                                nombre = clientesActivos.get(i).nameUser;
                                if( nombre.equals( usuario)){
                                    serv.mostrarEnConsola("Usuario eliminado > "+usuario);
                                    continue;
                                }
                            }
                            //Envio de parametros con mi nueva lista
                            enviarNuevosActivos(usuario);
                            break;
                        case 5:
                            //Enviamos llave de quien entro a los Usuarios conectados a la Red
                            String pk = entrada.readUTF();
                            System.out.println("Recibiendo PK para propagarla con todos los usuarios" + pk);
                            enviandoLlave(pk);
                            break;
                        case 6:
                            //Reiniciamos las llaves de todos los usuarios conectado a la Red
                            setFalseLlave();
                            break;
                    }
                }catch(IOException e){
                    serv.mostrarEnConsola("El cliente termino la conexion");
                    break;
                }
            }
            serv.mostrarEnConsola("Se removio un usuario");
            clientesActivos.removeElement(this);
            try{
                //Cerramos socket
                serv.mostrarEnConsola("Se desconecto un usuario");
                scli.close();
            }catch(Exception et){
                serv.mostrarEnConsola("No se pudo cerrar el socket "+ et);
            }
        }
    }
    
    public boolean usuariosDuplicados(String usuario){
        int numUsers = clientesActivos.size();
        for (int i = 0; i < numUsers; i++) {
            String nombre = clientesActivos.get(i).nameUser;
                if( nombre.toUpperCase().equals( usuario.toUpperCase())){
                    return true;
                }
        }
        return false;
    }
    
    //Mensaje devuelto por el sevidor
    public void enviaMsg(String mencli2){
        threadServidor user=null;
        for(int i=0;i<clientesActivos.size();i++)
        {
           serv.mostrarEnConsola("MENSAJE DEVUELTO:"+mencli2);
           try
            {
              user=clientesActivos.get(i);
              user.salida2.writeInt(1);//opcion de mensage 
              user.salida2.writeUTF(""+this.getNameUser()+" >"+ mencli2);              
            }catch (IOException e) {e.printStackTrace();}
        }
    }
    
    public void enviandoLlave(String pk){
        threadServidor user = null;
        for(int i=0; i<clientesActivos.size(); i++){
            serv.mostrarEnConsola("B(i) propagando a todos los usuarios " + pk);
            try{
                user=clientesActivos.get(i);
                if(user==this)continue; //NO es necesario a mi mismo
                user.salida2.writeInt(5);
                user.salida2.writeUTF(pk);
            }catch (IOException e) {e.printStackTrace();}
        }
    }
    
    public void setFalseLlave(){
        threadServidor user = null;
        for(int i=0; i<clientesActivos.size(); i++){
            serv.mostrarEnConsola("Poniendo a todos en False ");
            try{
                user=clientesActivos.get(i);
                if(user==this)continue; //NO es necesario a mi mismo
                System.out.println("Poniendo a user en False " + user);
                user.salida2.writeInt(6);
            }catch (IOException e) {e.printStackTrace();}
        }
    }
    
    public void enviaUserActivos()
     {
        threadServidor user=null;
        for(int i=0;i<clientesActivos.size();i++)
        {           
           try
            {
              user=clientesActivos.get(i);
              if(user==this)continue;//ya se lo envie
              user.salida2.writeInt(2);//opcion de agregar Thread cliente
              user.salida2.writeUTF(this.getNameUser());	
            }catch (IOException e) {e.printStackTrace();}
        }
     }
        
    public void  enviarNuevosActivos(String usuario){
        threadServidor user=null;
        //Envio de parametros al Thread del Cliente
        for(int i=0;i<clientesActivos.size();i++)
        {           
           try
            {
              user=clientesActivos.get(i);
              if(user.getName().equals(usuario))continue;//ya se lo envie
              user.salida2.writeInt(4);//opcion de eliminar 
              user.salida2.writeUTF(this.getNameUser());	
            }catch (IOException e) {e.printStackTrace();}
        }
    }
    
    private void enviaMsg(String amigo, String mencli) 
   {
      threadServidor user=null;
        for(int i=0;i<clientesActivos.size();i++)
        {           
           try
            {
              user=clientesActivos.get(i);
              if(user.nameUser.equals(amigo))
              {
                 user.salida2.writeInt(3);//opcion de mensage amigo   
                 user.salida2.writeUTF(this.getNameUser());
                 user.salida2.writeUTF(""+this.getNameUser()+">"+mencli);
              }
            }catch (IOException e) {e.printStackTrace();}
        }
   }
    
    
}
