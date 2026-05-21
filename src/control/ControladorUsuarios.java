package control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import modelo.Admin;
import modelo.Jugador;
import modelo.Usuario;
import vista.VentanaUsuario;

/**
 * Controla las acciones de la ventana de login y registro de usuarios.
 * <p>
 * Escucha los eventos de los botones "Autenticar" y "Registrar" de
 * {@link VentanaUsuario}. Cuando el login es exitoso, actualiza el usuario
 * activo en {@link ControladorPrincipal} y refresca el menú de la
 * ventana principal.
 * </p>
 *
 * <h3>Comandos gestionados</h3>
 * <ul>
 *   <li>{@code AUTENTICAR} — valida las credenciales e inicia sesión.</li>
 *   <li>{@code REGISTRAR} — crea un nuevo jugador si los datos son válidos.</li>
 * </ul>
 */
public class ControladorUsuarios implements ActionListener {

    /** Referencia al controlador principal para actualizar el usuario activo y el menú. */
    private ControladorPrincipal ctrlMain;

    /** Ventana de login/registro asociada a este controlador. */
    public VentanaUsuario userWin;

    /**
     * Construye el controlador de usuarios.
     *
     * @param ctrlMain controlador principal de la aplicación.
     * @param comando  comando que originó la apertura de la ventana ({@code "LOGIN"} o {@code "NEW_USER"}).
     */
    public ControladorUsuarios(ControladorPrincipal ctrlMain, String comando) {
        this.ctrlMain = ctrlMain;
    }

    /**
     * Comprueba si las credenciales proporcionadas corresponden a un usuario registrado.
     *
     * @param username nombre de usuario introducido.
     * @param password contraseña introducida.
     * @return {@code true} si el usuario existe y la contraseña es correcta.
     */
    public boolean login(String username, String password) {
        Usuario u = ctrlMain.ctrlFiles.buscarUsuario(username);
        return (u != null && u.comprobarPassword(password));
    }

    /**
     * Registra un nuevo jugador en el sistema si los datos son válidos.
     * Las condiciones para que el registro sea exitoso son:
     * <ul>
     *   <li>Ambas contraseñas deben coincidir.</li>
     *   <li>El nombre de usuario no debe estar ya registrado.</li>
     * </ul>
     *
     * @param username nombre de usuario deseado.
     * @param password contraseña elegida.
     * @param password2 confirmación de la contraseña.
     * @return {@code true} si el jugador fue registrado correctamente; {@code false} en caso contrario.
     */
    public boolean registrarJugador(String username, String password, String password2) {
        if (!password.equals(password2)) return false;
        if (ctrlMain.ctrlFiles.buscarUsuario(username) != null) return false;
        Jugador j = new Jugador(username, password);
        ctrlMain.ctrlFiles.addUsuario(j);
        return true;
    }

    /**
     * Maneja los eventos de acción de la ventana de usuario.
     * Procesa los comandos {@code AUTENTICAR} y {@code REGISTRAR}.
     *
     * @param e evento de acción con el comando asociado.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String comando = e.getActionCommand();
        System.out.println("Evento: " + comando);

        switch (comando) {

            case "AUTENTICAR":
                String user = userWin.user.getText().trim();
                String pass = new String(userWin.passwd.getPassword());

                if (login(user, pass)) {
                    ctrlMain.usuarioActivo = ctrlMain.ctrlFiles.buscarUsuario(user);
                    System.out.println("Login OK: " + user);
                    userWin.dispose();
                    ctrlMain.ventanaPrincipal.actualizarMenuSegunUsuario(ctrlMain.usuarioActivo);
                    JOptionPane.showMessageDialog(ctrlMain.ventanaPrincipal,
                        "Bienvenido, " + user + "!", "Login", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(userWin,
                        "Usuario o contraseña incorrectos.", "Error", JOptionPane.ERROR_MESSAGE);
                }
                break;

            case "REGISTRAR":
                String newUser  = userWin.user.getText().trim();
                String newPass  = new String(userWin.passwd.getPassword());
                String newPass2 = new String(userWin.passwd2.getPassword());

                if (newUser.isEmpty()) {
                    JOptionPane.showMessageDialog(userWin, "El nombre de usuario no puede estar vacio.");
                    break;
                }
                if (registrarJugador(newUser, newPass, newPass2)) {
                    JOptionPane.showMessageDialog(userWin, "Usuario registrado correctamente.");
                    userWin.dispose();
                } else if (!newPass.equals(newPass2)) {
                    JOptionPane.showMessageDialog(userWin, "Las contraseñas no coinciden.");
                } else {
                    JOptionPane.showMessageDialog(userWin, "Ese nombre de usuario ya existe.");
                }
                break;
        }
    }
}
