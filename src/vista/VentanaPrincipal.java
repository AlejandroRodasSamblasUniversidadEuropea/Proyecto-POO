package vista;

import javax.swing.*;
import control.ControladorPrincipal;
import modelo.Admin;
import modelo.Usuario;

/**
 * Ventana principal de la aplicacion.
 * Usa un JDesktopPane para mostrar ventanas internas (JInternalFrame).
 * El menu cambia segun si hay sesion iniciada y si el usuario es admin o jugador.
 */
public class VentanaPrincipal extends JFrame {

    private JMenuBar menuBar;
    private JMenu menuUsuarios;
    private JMenu menuJuegos;
    private JMenu menuEstadisticas;
    private JMenu menuAdmin;

    private JMenuItem itemLogin;
    private JMenuItem itemRegistro;
    private JMenuItem itemLogout;
    private JMenuItem itemMostrar;
    private JMenuItem itemNuevaPartida;
    private JMenuItem itemContinuarPartida;
    private JMenuItem itemStatsJugador;
    private JMenuItem itemAdminPanel;

    ControladorPrincipal cp;

    public VentanaPrincipal(ControladorPrincipal cp) {
        this.cp = cp;
        crearVista();
    }

    public void crearVista() {
        setTitle("Plataforma de Juegos - Universidad Europea");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // centra la ventana

        // Panel de escritorio donde se colocan las ventanas internas
        JDesktopPane desktop = new JDesktopPane();
        this.setContentPane(desktop);

        // ---- MENU USUARIOS ----
        menuUsuarios = new JMenu("Usuarios");

        itemLogin    = new JMenuItem("Iniciar sesion");
        itemRegistro = new JMenuItem("Registrarse");
        itemLogout   = new JMenuItem("Cerrar sesion");
        itemMostrar  = new JMenuItem("Mostrar usuarios");

        itemLogin.setActionCommand("LOGIN");
        itemRegistro.setActionCommand("NEW_USER");
        itemLogout.setActionCommand("LOGOUT");
        itemMostrar.setActionCommand("SHOW_USERS");

        itemLogin.addActionListener(cp);
        itemRegistro.addActionListener(cp);
        itemLogout.addActionListener(cp);
        itemMostrar.addActionListener(cp);

        menuUsuarios.add(itemLogin);
        menuUsuarios.add(itemRegistro);
        menuUsuarios.addSeparator();
        menuUsuarios.add(itemLogout);
        menuUsuarios.add(itemMostrar);

        // ---- MENU JUEGOS ----
        menuJuegos = new JMenu("Juegos");

        itemNuevaPartida     = new JMenuItem("Nueva partida");
        itemContinuarPartida = new JMenuItem("Continuar partida guardada");

        itemNuevaPartida.setActionCommand("NUEVA_PARTIDA");
        itemContinuarPartida.setActionCommand("CONTINUAR_PARTIDA");

        itemNuevaPartida.addActionListener(cp);
        itemContinuarPartida.addActionListener(cp);

        menuJuegos.add(itemNuevaPartida);
        menuJuegos.add(itemContinuarPartida);

        // ---- MENU ESTADISTICAS ----
        menuEstadisticas = new JMenu("Estadisticas");

        itemStatsJugador = new JMenuItem("Mis partidas");
        itemStatsJugador.setActionCommand("STATS_JUGADOR");
        itemStatsJugador.addActionListener(cp);

        menuEstadisticas.add(itemStatsJugador);

        // ---- MENU ADMIN (solo visible para admin) ----
        menuAdmin = new JMenu("Administracion");

        itemAdminPanel = new JMenuItem("Panel de administrador");
        itemAdminPanel.setActionCommand("ADMIN_PANEL");
        itemAdminPanel.addActionListener(cp);

        menuAdmin.add(itemAdminPanel);

        // ---- MONTAMOS LA BARRA ----
        menuBar = new JMenuBar();
        menuBar.add(menuUsuarios);
        menuBar.add(menuJuegos);
        menuBar.add(menuEstadisticas);
        // El menu admin se anade solo cuando haya sesion de admin activa
        this.setJMenuBar(menuBar);

        // Estado inicial: menus de juego y stats desactivados hasta login
        actualizarMenuSegunUsuario(null);
    }

    /**
     * Activa o desactiva opciones del menu segun el usuario activo.
     * Se llama desde ControladorUsuarios tras el login y tras el logout.
     */
    public void actualizarMenuSegunUsuario(Usuario usuario) {
        boolean haySession = (usuario != null);
        boolean esAdmin    = (usuario instanceof Admin);

        // Login/registro disponible solo sin sesion
        itemLogin.setEnabled(!haySession);
        itemRegistro.setEnabled(!haySession);

        // Logout solo con sesion
        itemLogout.setEnabled(haySession);

        // Juegos y estadisticas solo con sesion y NO admin
        menuJuegos.setEnabled(haySession && !esAdmin);
        menuEstadisticas.setEnabled(haySession && !esAdmin);

        // Menu admin: solo si es admin
        if (esAdmin) {
            if (menuBar.getMenuCount() < 4) {
                menuBar.add(menuAdmin);
                menuBar.revalidate();
            }
        } else {
            // Quitamos el menu admin si estaba
            menuBar.remove(menuAdmin);
            menuBar.revalidate();
        }

        // Actualizamos titulo con el usuario activo
        if (haySession) {
            setTitle("Plataforma de Juegos  —  " + usuario.getUsername()
                + (esAdmin ? " [ADMIN]" : " [Jugador]"));
        } else {
            setTitle("Plataforma de Juegos - Universidad Europea");
        }
    }
}
