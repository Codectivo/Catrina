/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Cliente;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Vector;
import javax.swing.AbstractListModel;
import javax.swing.JList;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.math.BigInteger;

//Import AES
import AES.GenEncription;
import AES.EncriptionException;
import AES.GeneratePrivateKey;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
/**
 *
 * @author misa
 */
public class principal extends javax.swing.JFrame implements ActionListener {

    /**
     * Creates new form principal
     */
    String nomUsuario;
    Vector<String> nomUsers;
    VentanaPrivada ventPrivada;
    Cliente cliente;
    GenEncription gen;
    public String cif_dec_key;
    public String llave_rondas = null;
    public BigInteger llaves_usuarios = new BigInteger("0");
    boolean llave_cambiada = true;
    int num_activos = 0;
    public BigInteger p = null;
    public BigInteger q = null;
    JPasswordField pf = new JPasswordField();
    String clav;
    int okCxl;
    public principal() throws IOException{
//        setResizable(false);
        initComponents();
        Dimension tamFrame = this.getSize();
        Dimension tamPantalla = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((tamPantalla.width-tamFrame.width)/2, (tamPantalla.height-tamFrame.height)/2);
        txtMensaje.requestFocus();
        txtMensaje.addActionListener(this);
        btnPrivado.addActionListener(this);
        btnEnviar.addActionListener(this);
        btnLimpiar.addActionListener(this);
        //Se hace la conexion con el ThreadCliente
        cliente = new Cliente(this);
        cliente.conexion();
        nomUsers=new Vector();
        listaActivos(cliente.pedirUsuarios());
        ventPrivada=new VentanaPrivada(cliente, this);
        //Iniciar AES
        gen = new GenEncription();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        add(jPanel1, BorderLayout.CENTER);
        this.addWindowListener(new WindowListener(){         
            public void windowClosing(WindowEvent e) {
                cliente.eliminaUsuario(nomUsuario);
            }
            public void windowClosed(WindowEvent e) {}         
            public void windowOpened(WindowEvent e) {}
            public void windowIconified(WindowEvent e) {}
            public void windowDeiconified(WindowEvent e) {}
            public void windowActivated(WindowEvent e) {}
            public void windowDeactivated(WindowEvent e) {}
        });
        /******** ESCUCHADOR CUANDO CAMBIAMOS TAMAÑO DE VENTANA PRINCIPAL *******************/
        this.addComponentListener(new ComponentAdapter() {
                    public void componentResized(ComponentEvent e) {
                        System.out.println(principal.this.jLabel1.getLocation().x);
                        int sum_location = principal.this.jLabel1.getLocation().x + 1;
                        System.out.println(sum_location);
                        principal.this.jLabel1.setLocation(sum_location, 10);
                        repaint();
              }
          }); 
        setVisible(true);
    }

    public void setUsuario(String txt){
        lblUsuario.setText("Conversación de Grupo: ");
        nomUsuario = txt;
        setTitle("Bienvenido "+nomUsuario);
    }
    
    public void listaActivos(Vector datos){
        nomUsers=datos;
        ponerDatosList(this.lstActivos, nomUsers);
        num_activos = datos.size();
    }
    
    //Mostrar Mensaje en la Ventana principal
     public void mostrarMsg(String msg)
     {
            String[] msg_separado = msg.split(">");
            try {
                //desencriptando MSJ
                String msg_desencripta = gen.desencripta(principal.this.llave_rondas.substring(0, 32), msg_separado[1]);
                 this.panMostrar.append(msg_separado[0]+" >"+ msg_desencripta + "\n");
            } catch (EncriptionException ex) {
                Logger.getLogger(principal.class.getName()).log(Level.SEVERE, null, ex);
            }
     }
     
    public void agregarUser(String user)
     {
        nomUsers.add(user);
        ponerDatosList(this.lstActivos, nomUsers);
        JOptionPane.showMessageDialog(null, user+" se ha unido a la conversación.");
     }
    
     public void retirraUser(String user)
     {  
        nomUsers.remove(user);
        ponerDatosList(this.lstActivos, nomUsers);
     }
    
    private void ponerDatosList(JList list,final Vector datos)
    {
        list.setModel(new AbstractListModel() {            
            @Override
            public int getSize(){ 
                num_activos = datos.size();
                return datos.size();
            }
            @Override
            public Object getElementAt(int i) { return datos.get(i); }
        });
    }
    
    public void mensageAmigo(String amigo,String msg){
        /*Envio de mensajes PRIVADOS
         */
        ventPrivada.setAmigo(amigo);           
        ventPrivada.mostrarMsg(msg);        
        ventPrivada.setVisible(true); 
     }
    
    public void generandoLlaveAES(String pk){
        /*Se Multiplican las llaves en base a las rondas.
         */
        if(!pk.equals(this.cif_dec_key)){
            //Multiplicar
            String val_mult;
            BigInteger val = new BigInteger(pk);
            if(this.llave_rondas == null)
                val_mult = this.cif_dec_key;
            else
                val_mult = this.llave_rondas;
            BigInteger val2 = new BigInteger(val_mult);
            this.llaves_usuarios = val.multiply(val2);
            System.out.println("Valores con  "+val_mult+"  r =" + this.llaves_usuarios);
            this.llave_rondas = this.llaves_usuarios.toString();
        }
    }
    
    public void generandoOtraPK(){
        /*Se genera una nueva llave PK de 32 cada que un usuario Entra.
         */
        //Generando Private Key de 32 bits
        if(this.llave_cambiada == false){
            GeneratePrivateKey pk = new GeneratePrivateKey();
            String pvk = pk.beta.toString();
            cliente.setPK(pvk);
            System.out.println("Se me ha generado Otra PK: " + pvk);
            this.llave_cambiada = true;
            cliente.enviaPrivateKey(pvk);
            if(this.num_activos == 1){
                this.llave_rondas = null;
            }
        }
    }
    
    public void reniciandoValores(){
        /*Reiniciando Valores de las Llaves de todos los usuarios cada que entre un Usuario nuevo.
         */
        this.llave_cambiada = false;
        this.llaves_usuarios = new BigInteger("0");
        this.cif_dec_key = null;
        this.llave_rondas = null;
    }
    
    public void bajarScroll(){
        panMostrar.setCaretPosition(panMostrar.getDocument().getLength());
    }

    public static void main(String args[]) throws IOException{
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jMenu1 = new javax.swing.JMenu();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        panMostrar = new javax.swing.JTextArea();
        lblUsuario = new javax.swing.JLabel();
        txtMensaje = new javax.swing.JTextField();
        btnEnviar = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        btnLimpiar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        lstActivos = new javax.swing.JList();
        jLabel2 = new javax.swing.JLabel();
        btnPrivado = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();

        jMenu1.setText("jMenu1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setPreferredSize(new java.awt.Dimension(1700, 1700));

        panMostrar.setColumns(50);
        panMostrar.setEditable(false);
        panMostrar.setRows(5);
        panMostrar.setPreferredSize(new java.awt.Dimension(655, 85));
        jScrollPane2.setViewportView(panMostrar);

        lblUsuario.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblUsuario.setText("Conversación de Grupo:");

        txtMensaje.setMinimumSize(new java.awt.Dimension(0, 0));
        txtMensaje.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMensajeActionPerformed(evt);
            }
        });

        btnEnviar.setText("Enviar");
        btnEnviar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEnviarActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Ubuntu", 1, 32)); // NOI18N
        jLabel1.setText("Catrina");
        jLabel1.setToolTipText("");
        jLabel1.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);

        btnLimpiar.setText("Limpiar");

        lstActivos.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(lstActivos);

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel2.setText("Usuarios:");

        btnPrivado.setText("Enviar");
        btnPrivado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrivadoActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Ubuntu", 0, 12)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Seleccionar usuario");

        jLabel4.setFont(new java.awt.Font("Ubuntu", 0, 12)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("para iniciar chat privado.");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addComponent(lblUsuario)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(txtMensaje, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 1106, Short.MAX_VALUE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnEnviar)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnLimpiar)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(jLabel2)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 124, Short.MAX_VALUE)
                                .addComponent(btnPrivado, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(539, 539, 539)
                        .addComponent(jLabel1)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(lblUsuario, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addGap(22, 22, 22))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 410, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnPrivado)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel4)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addComponent(txtMensaje, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnEnviar)
                    .addComponent(btnLimpiar))
                .addContainerGap())
        );

        jMenu2.setText("Archivo");

        jMenuItem1.setText("Cerrar sesión");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem1);

        jMenuBar1.add(jMenu2);

        jMenu3.setText("Herramientas");

        jMenuItem2.setText("Ver llaves");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem2);

        jMenuBar1.add(jMenu3);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 1266, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 639, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnEnviarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEnviarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnEnviarActionPerformed

    private void btnPrivadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrivadoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnPrivadoActionPerformed

    private void txtMensajeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMensajeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtMensajeActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        cliente.eliminaUsuario(nomUsuario);
        System.exit(0);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        okCxl = 0;
        String llavp;
        if(this.llave_rondas == null){
            llavp = "Ninguna";
        }else{
            llavp = this.llave_rondas.substring(0, 32);
        }
        JTextArea textAreaKeys = new JTextArea();
        if(this.llave_rondas != null){
            textAreaKeys.setText("Beta final ó total:\n " + this.llave_rondas  +  "\n\nValor de p:\n "+this.p.toString()+"\n"+"\nValor del generador:\n"+this.q.toString()+"\n\nLlave de AES-256: \n"+llavp);
        }else{    
            textAreaKeys.setText( "Beta final ó total:\n Ninguna \n\nValor de p:\n "+this.p.toString()+"\n"+"\nValor del generador:\n"+this.q.toString()+"\n\nLlave de AES-256: \n"+llavp);
        }
        textAreaKeys.setColumns(60);
        textAreaKeys.setOpaque(false);
        textAreaKeys.setEditable(false);
        textAreaKeys.setLineWrap(true);
        textAreaKeys.setWrapStyleWord(true);
        textAreaKeys.setSize(textAreaKeys.getPreferredSize().width, 1);
        JOptionPane.showMessageDialog(null, textAreaKeys, "Llaves", JOptionPane.INFORMATION_MESSAGE );
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    /**
     * @param args the command line arguments
     */
   
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEnviar;
    private javax.swing.JButton btnLimpiar;
    private javax.swing.JButton btnPrivado;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblUsuario;
    private javax.swing.JList lstActivos;
    private javax.swing.JTextArea panMostrar;
    private javax.swing.JTextField txtMensaje;
    // End of variables declaration//GEN-END:variables

    //Control de los botones y eventos
    @Override
    public void actionPerformed(ActionEvent evt) {
        //Enviar mensaje a ventana principal
        if(evt.getSource()==this.btnEnviar || evt.getSource()==this.txtMensaje){
           if("".equals(this.txtMensaje.getText())) {
                JOptionPane.showMessageDialog(null, "Por favor introduce un texto");
           }else{
                if(principal.this.llave_rondas == null){
                    JOptionPane.showMessageDialog(null, "No existen mas usuarios para chatear");
                    txtMensaje.setText("");
                }else{
                    //Enviar mensaje
                    String mensaje = txtMensaje.getText();        
                    //Encirptar mensaje
                    try {
                        String key_s = principal.this.llave_rondas.substring(0, 32);
                        System.out.println("Llave cifrado " + key_s);
                        String msj_encripta = gen.encripta(key_s, mensaje);
                        //Enviamos MSJ encriptado
                        cliente.flujo(msj_encripta);
                    } catch (EncriptionException ex) {
                        Logger.getLogger(principal.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    //Bajar scroll
                    panMostrar.setCaretPosition(panMostrar.getDocument().getLength());
                    //Limpiar input mensaje
                    txtMensaje.setText("");
                }
            }
        }else if(evt.getSource()==this.btnPrivado){
            int pos=this.lstActivos.getSelectedIndex();
            if( pos>=0 ){
                if(principal.this.llave_rondas != null){
                    if(nomUsers.get(pos).equals(nomUsuario)){
                        JOptionPane.showMessageDialog(null, "No puedes Chatear contigo mismo");
                    }else{
                        ventPrivada.setAmigo(nomUsers.get(pos));           
                        ventPrivada.setVisible(true);
                    }
                }else{
                    JOptionPane.showMessageDialog(null, "No existen mas usuarios para chatear");
                }
            }
        } else if(evt.getSource() == this.btnLimpiar){
             this.panMostrar.setText("");
             txtMensaje.requestFocus();
        }
    }
}
