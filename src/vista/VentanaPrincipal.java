package vista;

import javax.swing.*;
import control.ControladorPrincipal;
import modelo.Admin;
import modelo.Usuario;

import java.awt.GridLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Component;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;

/**
 * Ventana principal de la aplicación.
 * <p>
 * Implementa una interfaz de documentos múltiples (MDI) usando un
 * {@link JDesktopPane} como contenedor de las ventanas internas ({@link JInternalFrame}).
 * Contiene la barra de menú y el panel central con los botones de acceso rápido a los juegos.
 * </p>
 *
 * <h3>Comportamiento dinámico del menú</h3>
 * <ul>
 *   <li>Sin sesión: solo "Iniciar sesión" y "Registrarse" están activos.</li>
 *   <li>Jugador activo: se habilitan los menús de Juegos y Estadísticas.</li>
 *   <li>Admin activo: aparece el menú de Administración y se ocultan Juegos y Estadísticas.</li>
 * </ul>
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

    /**
     * Panel central con botones de acceso rápido a cada juego disponible.
     * Solo es visible cuando hay un jugador con sesión iniciada.
     */
    private JPanel panelJuegos;

    /** Controlador principal que recibe los eventos de los menús y botones. */
    ControladorPrincipal cp;

    /**
     * Construye la ventana principal y la inicializa con el controlador indicado.
     *
     * @param cp controlador principal de la aplicación.
     */
    public VentanaPrincipal(ControladorPrincipal cp) {
        this.cp = cp;
        crearVista();
    }

    /**
     * Construye y configura todos los componentes visuales de la ventana principal:
     * el desktop, el panel de juegos y la barra de menú con todas sus opciones.
     * Al finalizar, actualiza el estado del menú para el caso sin sesión iniciada.
     */
    public void crearVista() {
        setTitle("Plataforma de Juegos - Universidad Europea");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JDesktopPane desktop = new JDesktopPane();
        desktop.setBackground(new Color(25, 25, 40));

        // ---- PANEL DE JUEGOS ----
        panelJuegos = new JPanel();
        panelJuegos.setLayout(new BoxLayout(panelJuegos, BoxLayout.Y_AXIS));
        panelJuegos.setOpaque(false);

        JLabel titulo = new JLabel("¿Qué quieres jugar?");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titulo.setForeground(new Color(220, 220, 255));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 32, 0));
        panelJuegos.add(titulo);

        for (modelo.Juego j : cp.sj.juegos) {
            JButton btn = new JButton(j.getNombre());
            btn.setFont(new Font("Segoe UI", Font.BOLD, 18));
            btn.setForeground(new Color(25, 25, 40));
            btn.setBackground(new Color(129, 140, 248));
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.setBorder(BorderFactory.createEmptyBorder(16, 60, 20, 60));
            btn.setOpaque(true);
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setMaximumSize(new Dimension(320, 60));

            final String nombre = j.getNombre();
            btn.addActionListener(e -> cp.ctrlJuego.iniciarNuevaPartida(nombre));

            btn.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    btn.setBackground(new Color(165, 180, 252));
                }
                public void mouseExited(java.awt.event.MouseEvent e) {
                    btn.setBackground(new Color(129, 140, 248));
                }
            });

            panelJuegos.add(btn);
            panelJuegos.add(Box.createVerticalStrut(16));
        }

        panelJuegos.setVisible(false);
        panelJuegos.setBounds(280, 200, 340, 260);
        desktop.add(panelJuegos);
        this.setContentPane(desktop);

        // ---- MENÚ USUARIOS ----
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

        // ---- MENÚ JUEGOS ----
        menuJuegos = new JMenu("Juegos");

        itemNuevaPartida     = new JMenuItem("Nueva partida");
        itemContinuarPartida = new JMenuItem("Continuar partida guardada");

        itemNuevaPartida.setActionCommand("NUEVA_PARTIDA");
        itemContinuarPartida.setActionCommand("CONTINUAR_PARTIDA");

        itemNuevaPartida.addActionListener(cp);
        itemContinuarPartida.addActionListener(cp);

        menuJuegos.add(itemNuevaPartida);
        menuJuegos.add(itemContinuarPartida);

        // ---- MENÚ ESTADÍSTICAS ----
        menuEstadisticas = new JMenu("Estadisticas");

        itemStatsJugador = new JMenuItem("Mis partidas");
        itemStatsJugador.setActionCommand("STATS_JUGADOR");
        itemStatsJugador.addActionListener(cp);
        menuEstadisticas.add(itemStatsJugador);

        // ---- MENÚ ADMINISTRACIÓN ----
        menuAdmin = new JMenu("Administracion");

        itemAdminPanel = new JMenuItem("Panel de administrador");
        itemAdminPanel.setActionCommand("ADMIN_PANEL");
        itemAdminPanel.addActionListener(cp);
        menuAdmin.add(itemAdminPanel);

        // ---- BARRA DE MENÚ ----
        menuBar = new JMenuBar();
        menuBar.add(menuUsuarios);
        menuBar.add(menuJuegos);
        menuBar.add(menuEstadisticas);
        this.setJMenuBar(menuBar);

        actualizarMenuSegunUsuario(null);
    }

    /**
     * Activa o desactiva las opciones del menú y el panel de juegos según el usuario activo.
     * <p>
     * Este método se llama desde {@link control.ControladorUsuarios} tras el login y el logout,
     * y desde el constructor para establecer el estado inicial (sin sesión).
     * </p>
     *
     * @param usuario usuario que acaba de iniciar sesión, o {@code null} si se ha cerrado la sesión.
     */
    public void actualizarMenuSegunUsuario(Usuario usuario) {
        boolean haySession = (usuario != null);
        boolean esAdmin    = (usuario instanceof Admin);

        itemLogin.setEnabled(!haySession);
        itemRegistro.setEnabled(!haySession);

        itemLogout.setEnabled(haySession);

        menuJuegos.setEnabled(haySession && !esAdmin);
        menuEstadisticas.setEnabled(haySession && !esAdmin);

        if (esAdmin) {
            if (menuBar.getMenuCount() < 4) {
                menuBar.add(menuAdmin);
                menuBar.revalidate();
            }
        } else {
            menuBar.remove(menuAdmin);
            menuBar.revalidate();
        }

        if (haySession) {
            setTitle("Plataforma de Juegos  —  " + usuario.getUsername()
                + (esAdmin ? " [ADMIN]" : " [Jugador]"));
        } else {
            setTitle("Plataforma de Juegos - Universidad Europea");
        }
        panelJuegos.setVisible(haySession && !esAdmin);
    }
}
