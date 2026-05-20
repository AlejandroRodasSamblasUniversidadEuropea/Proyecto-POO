package vista;

import javax.swing.*;
import control.ControladorUsuarios;

/**
 * Ventana interna para login y registro de usuarios.
 * Muestra campos de usuario y contraseña.
 * Si el comando es "NEW_USER" muestra un campo extra para repetir contraseña.
 */
public class VentanaUsuario extends JInternalFrame {

    public ControladorUsuarios controlador;
    public JTextField user;
    public JPasswordField passwd;
    public JPasswordField passwd2; // solo para registro
    public JButton validar;

    public VentanaUsuario(ControladorUsuarios c, String comando) {
        super("", true, true, true, true);
        this.controlador = c;
        this.setLocation(100, 80);
        this.crearVista(comando);
    }

    public void crearVista(String comando) {
        JPanel panel = new JPanel();

        user   = new JTextField(20);
        passwd = new JPasswordField(20);

        panel.add(new JLabel("Username:"));
        panel.add(user);
        panel.add(new JLabel("Password:"));
        panel.add(passwd);

        if (comando.equals("LOGIN")) {
            validar = new JButton("Autenticar");
            validar.setActionCommand("AUTENTICAR");
            this.setTitle("Iniciar sesion");
        } else {
            passwd2 = new JPasswordField(20);
            validar = new JButton("Registrar");
            validar.setActionCommand("REGISTRAR");
            panel.add(new JLabel("Repetir Password:"));
            panel.add(passwd2);
            this.setTitle("Registrar nuevo usuario");
        }

        validar.addActionListener(controlador);
        panel.add(validar);

        this.setSize(380, comando.equals("LOGIN") ? 150 : 190);
        this.getContentPane().add(panel);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setResizable(false);
        this.setVisible(true);
    }
}
