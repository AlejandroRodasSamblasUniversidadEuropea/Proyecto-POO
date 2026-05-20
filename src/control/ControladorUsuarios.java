package control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import modelo.Admin;
import modelo.Jugador;
import modelo.Usuario;
import vista.VentanaUsuario;

/**
 * Controla las acciones de la ventana de login y registro.
 * Cuando el login es exitoso, guarda el usuario en ControladorPrincipal
 * para que el resto de la aplicacion sepa quien esta activo.
 */
public class ControladorUsuarios implements ActionListener {

    private ControladorPrincipal ctrlMain;
    public VentanaUsuario userWin;

    public ControladorUsuarios(ControladorPrincipal ctrlMain, String comando) {
        this.ctrlMain = ctrlMain;
    }

    // Comprueba si el username y password coinciden con algun usuario registrado
    public boolean login(String username, String password) {
        Usuario u = ctrlMain.ctrlFiles.buscarUsuario(username);
        return (u != null && u.comprobarPassword(password));
    }

    // Registra un nuevo jugador si las contraseñas coinciden y el username no existe
    public boolean registrarJugador(String username, String password, String password2) {
        if (!password.equals(password2)) return false;
        if (ctrlMain.ctrlFiles.buscarUsuario(username) != null) return false; // ya existe
        Jugador j = new Jugador(username, password);
        ctrlMain.ctrlFiles.addUsuario(j);
        return true;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String comando = e.getActionCommand();
        System.out.println("Evento: " + comando);

        switch (comando) {

            case "AUTENTICAR":
                String user = userWin.user.getText().trim();
                String pass = new String(userWin.passwd.getPassword());

                if (login(user, pass)) {
                    // Guardamos el usuario activo en el controlador principal
                    ctrlMain.usuarioActivo = ctrlMain.ctrlFiles.buscarUsuario(user);
                    System.out.println("Login OK: " + user);
                    userWin.dispose();
                    // Actualizamos la ventana principal segun el tipo de usuario
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
