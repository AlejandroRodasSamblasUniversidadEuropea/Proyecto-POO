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
    
    private JPanel panelJuegos;

    ControladorPrincipal cp;

    public VentanaPrincipal(ControladorPrincipal cp) {
        this.cp = cp;
        crearVista();
    }

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

    //Botones:
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

    // Centrar el panel en el desktop
    panelJuegos.setBounds(280, 200, 340, 260);
    desktop.add(panelJuegos);
    this.setContentPane(desktop);

    // ---- MENUS (igual que antes) ----
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

    menuJuegos = new JMenu("Juegos");

    itemNuevaPartida     = new JMenuItem("Nueva partida");
    itemContinuarPartida = new JMenuItem("Continuar partida guardada");

    itemNuevaPartida.setActionCommand("NUEVA_PARTIDA");
    itemContinuarPartida.setActionCommand("CONTINUAR_PARTIDA");

    itemNuevaPartida.addActionListener(cp);
    itemContinuarPartida.addActionListener(cp);

    menuJuegos.add(itemNuevaPartida);
    menuJuegos.add(itemContinuarPartida);

    menuEstadisticas = new JMenu("Estadisticas");

    itemStatsJugador = new JMenuItem("Mis partidas");
    itemStatsJugador.setActionCommand("STATS_JUGADOR");
    itemStatsJugador.addActionListener(cp);
    menuEstadisticas.add(itemStatsJugador);

    menuAdmin = new JMenu("Administracion");

    itemAdminPanel = new JMenuItem("Panel de administrador");
    itemAdminPanel.setActionCommand("ADMIN_PANEL");
    itemAdminPanel.addActionListener(cp);
    menuAdmin.add(itemAdminPanel);

    menuBar = new JMenuBar();
    menuBar.add(menuUsuarios);
    menuBar.add(menuJuegos);
    menuBar.add(menuEstadisticas);
    this.setJMenuBar(menuBar);

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
        panelJuegos.setVisible(haySession && !esAdmin);
    }
}
