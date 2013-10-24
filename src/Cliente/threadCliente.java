/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Cliente;

import java.io.DataInputStream;
import java.io.IOException;

/**
 *
 * @author misa
 */
public class threadCliente extends Thread{
   DataInputStream entrada;
   principal vcli;
   
   public threadCliente (DataInputStream entrada, principal vcli) throws IOException
   {
      this.entrada=entrada;
      this.vcli=vcli;
   }
   
   public void run(){
       String menser="", amigo="";
       int opcion = 0;
       while(true){
           try{
            opcion=entrada.readInt();
            switch(opcion)
                {
               case 1:
                   //Se recibe mensajes o eventos de parte del Threadservidor
                  menser=entrada.readUTF();
                  System.out.println("ECO del servidor:"+menser);
                  vcli.mostrarMsg(menser);
                  vcli.bajarScroll();
                  break;
               case 2:
                   //Se agrega usuario a la lista.
                  menser=entrada.readUTF();
                  System.out.println("Switch 2 con variable: " + menser);
                  vcli.agregarUser(menser);                  
                  break;
               case 3:
                   //Se lee el mensaje del Thread Servidor
                  amigo=entrada.readUTF();
                  menser=entrada.readUTF();
                  vcli.mensageAmigo(amigo,menser);
                  System.out.println("ECO del servidor:"+menser);
                  break;
               case 4:
                   //Se elimina un usuario quitandolo de la lista
                   menser=entrada.readUTF();
                   System.out.println("Eliminando: " + menser);
                   vcli.retirraUser(menser);
                   //Generar nuevas llaves para los usuarios aun dentro
                   vcli.reniciandoValores();
                   vcli.generandoOtraPK();
                   break;
               case 5:
                   //Recibiendo PK de todos los usuarios
                   //Se tienen que cambiar todas la Claves aqui
                   String pk = entrada.readUTF();
                   System.out.println("Recibiendo PK para generar llave de cifrado." + pk);
                   vcli.generandoOtraPK();
                   vcli.generandoLlaveAES(pk);
                   break;
               case 6:
                   //Se reinician los valores de todos los Usuarios conectados a la Red
                   System.out.println("Me estan reiniciando mis valores :D");
                   vcli.reniciandoValores();
                   break;
                }
            }
           catch (IOException e){
            System.out.println("Error en la comunicacion "+"Informacion para el usuario");
            break;
            }
       }
       System.out.println("se desconecto el servidor");
   } 
}
