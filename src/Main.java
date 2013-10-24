import Cliente.Cliente;
import Cliente.principal;
import java.io.IOException;
import javax.swing.JOptionPane;

public class Main{
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)throws IOException {
        // TODO code application logic here      
        //Abrir ventana del Server
        Cliente.IP_SERVER = JOptionPane.showInputDialog("Introducir IP del Servidor:","localhost");
        if(Cliente.IP_SERVER == null){
            System.exit(0);
        }
        principal w = new principal();
        w.setVisible(true);
    }
}