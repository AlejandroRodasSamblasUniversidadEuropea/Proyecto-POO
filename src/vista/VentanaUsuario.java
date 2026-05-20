package vista;

import javax.swing.*;
import control.ControladorUsuarios;

/**
 * Ventana interna para el inicio de sesión y el registro de nuevos usuarios.
 * <p>
 * Se comporta de forma diferente según el comando con el que se abra:
 * </p>
 * <ul>
 *   <li>{@code "LOGIN"} — muestra campos de usuario y contraseña con el botón "Autenticar".</li>
 *   <li>{@code "NEW_USER"} — añade un tercer campo para confirmar la contraseña y el botón "Registrar".</li>
 * </ul>
 *
 * <p>Los eventos del botón son delegados a {@link ControladorUsuarios}.</p>
 */
public class VentanaUsuario extends JInternalFrame {

    /** Controlador que gestiona la autenticación y el registro. */
    public ControladorUsuarios controlador;

    /** Campo de texto para introducir el nombre de usuario. */
    public JTextField user;

    /** Campo de contraseña principal. */
    public JPasswordField passwd;

    /**
     * Campo de confirmación de contraseña; solo visible en modo registro ({@code "NEW_USER"}).
     * Es {@code null} cuando la ventana se abre en modo login.
     */
    public JPasswordField passwd2;

    /** Botón de acción (Autenticar o Registrar según el modo). */
    public JButton validar;

    /**
     * Construye la ventana de usuario en el modo indicado.
     *
     * @param c       controlador que recibirá los eventos del botón.
     * @param comando modo de apertura: {@code "LOGIN"} o {@code "NEW_USER"}.
     */
    public VentanaUsuario(ControladorUsuarios c, String comando) {
        super("", true, true, true, true);
        this.controlador = c;
        this.setLocation(100, 80);
        this.crearVista(comando);
    }

    /**
     * Construye y añade los componentes visuales según el modo de la ventana.
     *
     * @param comando {@code "LOGIN"} para mostrar solo usuario/contraseña;
     *                {@code "NEW_USER"} para mostrar también la confirmación de contraseña.
     */
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
