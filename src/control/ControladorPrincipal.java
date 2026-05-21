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
 * Controlador principal de la aplicación.
 * <p>
 * Implementa {@link ActionListener} para capturar todos los eventos generados
 * por los menús y botones de la ventana principal, y delega cada acción al
 * controlador o vista correspondiente.
 * </p>
 *
 * <h3>Comandos gestionados</h3>
 * <ul>
 *   <li>{@code LOGIN} / {@code NEW_USER} — abre la ventana de autenticación o registro.</li>
 *   <li>{@code LOGOUT} — cierra la sesión del usuario activo.</li>
 *   <li>{@code SHOW_USERS} — muestra la lista de todos los usuarios.</li>
 *   <li>{@code NUEVA_PARTIDA} — abre el selector de juego.</li>
 *   <li>{@code CONTINUAR_PARTIDA} — muestra las partidas guardadas del usuario activo.</li>
 *   <li>{@code STATS_JUGADOR} — abre la ventana de estadísticas del jugador.</li>
 *   <li>{@code ADMIN_PANEL} — abre el panel de administración (solo admins).</li>
 * </ul>
 */
public class ControladorPrincipal implements ActionListener {

    /** Ventana principal de la aplicación (MDI con JDesktopPane). */
    public VentanaPrincipal ventanaPrincipal;

    /** Controlador encargado de la lectura y escritura en ficheros. */
    public ControladorFicheros ctrlFiles;

    /** Controlador de autenticación y registro de usuarios. */
    public ControladorUsuarios ctrlUsers;

    /** Controlador que gestiona el inicio, pausa y reanudación de partidas. */
    public ControladorJuego ctrlJuego;

    /** Controlador con la lógica exclusiva del administrador. */
    public ControladorAdmin ctrlAdmin;

    /** Sistema central con las listas de usuarios, partidas y juegos. */
    public SistemaJuegos sj;

    /**
     * Usuario que tiene la sesión actualmente iniciada.
     * Es {@code null} si no hay ninguna sesión activa.
     */
    public Usuario usuarioActivo;

    /**
     * Construye el controlador principal con las dependencias indicadas.
     * Inicializa también los subcontroladores de juego y administración.
     *
     * @param cf controlador de ficheros ya inicializado.
     * @param sj sistema de juegos con los datos cargados.
     */
    public ControladorPrincipal(ControladorFicheros cf, SistemaJuegos sj) {
        this.ctrlFiles = cf;
        this.sj        = sj;
        this.ctrlJuego = new ControladorJuego(this);
        this.ctrlAdmin = new ControladorAdmin(sj);
    }

    /**
     * Maneja los eventos de acción generados por los elementos de la interfaz.
     * Distribuye la ejecución según el comando del evento.
     *
     * @param e evento de acción con el comando asociado.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String comando = e.getActionCommand();
        System.out.println("Evento detectado: " + comando);

        switch (comando) {

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

            case "NUEVA_PARTIDA":
                VentanaMenuJuego vMenu = new VentanaMenuJuego(this, sj.juegos);
                ventanaPrincipal.getContentPane().add(vMenu);
                vMenu.setVisible(true);
                try { vMenu.setSelected(true); } catch (Exception ex) { /* ignorar */ }
                break;

            case "CONTINUAR_PARTIDA":
                mostrarPartidasEnCurso();
                break;

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

    /**
     * Muestra un diálogo con las partidas en curso del usuario activo para que elija
     * cuál desea continuar. Si no hay sesión iniciada o no hay partidas guardadas,
     * muestra el mensaje de aviso correspondiente.
     */
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
