package control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import modelo.Admin;
import modelo.Jugador;
import modelo.Partida;
import modelo.SistemaJuegos;
import modelo.Usuario;
import vista.VentanaAdmin;
import vista.VentanaEstadisticas;
import vista.VentanaMenuJuego;
import vista.VentanaPrincipal;
import vista.VentanaUsuario;

/**
 * Controlador principal: detecta todos los eventos de la ventana principal
 * y coordina el resto de controladores.
 *
 * Guarda el usuario que ha iniciado sesion en "usuarioActivo".
 */
public class ControladorPrincipal implements ActionListener {

    public VentanaPrincipal ventanaPrincipal;
    public ControladorFicheros ctrlFiles;
    public ControladorUsuarios ctrlUsers;
    public ControladorJuego ctrlJuego;
    public ControladorAdmin ctrlAdmin;
    public SistemaJuegos sj;

    // El usuario que ha iniciado sesion (null si nadie lo ha hecho)
    public Usuario usuarioActivo;

    public ControladorPrincipal(ControladorFicheros cf, SistemaJuegos sj) {
        this.ctrlFiles = cf;
        this.sj = sj;
        this.ctrlJuego = new ControladorJuego(this);
        this.ctrlAdmin  = new ControladorAdmin(sj);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String comando = e.getActionCommand();
        System.out.println("Evento detectado: " + comando);

        switch (comando) {

            // ---- USUARIOS ----
            case "LOGIN":
            case "NEW_USER":
                ctrlUsers = new ControladorUsuarios(this, comando);
                VentanaUsuario ventanaUser = new VentanaUsuario(ctrlUsers, comando);
                ctrlUsers.userWin = ventanaUser;
                ventanaPrincipal.getContentPane().add(ventanaUser);
                ventanaUser.setVisible(true);
                break;

            case "LOGOUT":
                usuarioActivo = null;
                ventanaPrincipal.actualizarMenuSegunUsuario(null);
                JOptionPane.showMessageDialog(ventanaPrincipal, "Sesion cerrada.");
                break;

            case "SHOW_USERS":
                JOptionPane.showMessageDialog(ventanaPrincipal, sj.usuariosToString());
                break;

            // ---- JUEGOS ----
            case "NUEVA_PARTIDA":
                // Muestra el selector de juego
                VentanaMenuJuego vMenu = new VentanaMenuJuego(this, sj.juegos);
                ventanaPrincipal.getContentPane().add(vMenu);
                vMenu.setVisible(true);
                try { vMenu.setSelected(true); } catch (Exception ex) { /* ignorar */ }
                break;

            case "CONTINUAR_PARTIDA":
                mostrarPartidasEnCurso();
                break;

            // ---- ESTADISTICAS ----
            case "STATS_JUGADOR":
                if (usuarioActivo == null) {
                    JOptionPane.showMessageDialog(ventanaPrincipal, "Inicia sesion primero.");
                } else {
                    VentanaEstadisticas vStats = new VentanaEstadisticas(this);
                    ventanaPrincipal.getContentPane().add(vStats);
                    vStats.setVisible(true);
                    try { vStats.setSelected(true); } catch (Exception ex) { /* ignorar */ }
                }
                break;

            // ---- ADMIN ----
            case "ADMIN_PANEL":
                if (usuarioActivo instanceof Admin) {
                    VentanaAdmin vAdmin = new VentanaAdmin(this);
                    ventanaPrincipal.getContentPane().add(vAdmin);
                    vAdmin.setVisible(true);
                    try { vAdmin.setSelected(true); } catch (Exception ex) { /* ignorar */ }
                } else {
                    JOptionPane.showMessageDialog(ventanaPrincipal, "Acceso denegado.");
                }
                break;
        }
    }

    // Muestra las partidas en curso del usuario activo para que elija continuar una
    private void mostrarPartidasEnCurso() {
        if (usuarioActivo == null) {
            JOptionPane.showMessageDialog(ventanaPrincipal, "Inicia sesion primero.");
            return;
        }
        ArrayList<Partida> enCurso = sj.getPartidasEnCurso(usuarioActivo.getUsername());
        if (enCurso.isEmpty()) {
            JOptionPane.showMessageDialog(ventanaPrincipal, "No tienes partidas guardadas.");
            return;
        }

        String[] opciones = new String[enCurso.size()];
        for (int i = 0; i < enCurso.size(); i++) {
            Partida p = enCurso.get(i);
            opciones[i] = "[" + p.getId() + "] " + p.getJuego().getNombre()
                        + " - " + p.getFecha() + " (" + p.getPuntuaciones()[0] + " pts)";
        }

        String elegida = (String) JOptionPane.showInputDialog(
            ventanaPrincipal,
            "Elige la partida a continuar:",
            "Continuar partida",
            JOptionPane.QUESTION_MESSAGE,
            null,
            opciones,
            opciones[0]
        );

        if (elegida != null) {
            int indice = java.util.Arrays.asList(opciones).indexOf(elegida);
            ctrlJuego.continuarPartida(enCurso.get(indice));
        }
    }
}
