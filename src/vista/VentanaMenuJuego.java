package vista;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

import control.ControladorPrincipal;
import modelo.Juego;

/**
 * Ventana interna que muestra los juegos disponibles en la plataforma.
 * <p>
 * Genera dinámicamente un botón por cada juego registrado en el sistema.
 * Al pulsar un botón, se cierra esta ventana y se inicia una nueva partida
 * del juego seleccionado a través del {@link control.ControladorJuego}.
 * </p>
 */
public class VentanaMenuJuego extends JInternalFrame {

    /** Controlador principal que arranca la partida al seleccionar un juego. */
    private ControladorPrincipal cp;

    /** Lista de juegos disponibles; se genera un botón por cada uno. */
    private ArrayList<Juego> juegos;

    /**
     * Construye la ventana de selección de juego.
     *
     * @param cp     controlador principal de la aplicación.
     * @param juegos lista de juegos disponibles para mostrar como botones.
     */
    public VentanaMenuJuego(ControladorPrincipal cp, ArrayList<Juego> juegos) {
        super("Seleccionar juego", true, true, true, true);
        this.cp     = cp;
        this.juegos = juegos;
        crearVista();
    }

    /**
     * Construye y añade los componentes visuales de la ventana:
     * un título y un botón por cada juego disponible.
     * Al pulsar un botón, se llama a {@link control.ControladorJuego#iniciarNuevaPartida(String)}.
     */
    private void crearVista() {
        setSize(350, 250);
        setLocation(150, 100);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titulo = new JLabel("Elige el juego:", JLabel.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(titulo, BorderLayout.NORTH);

        JPanel panelBotones = new JPanel(new GridLayout(juegos.size(), 1, 5, 5));
        for (Juego juego : juegos) {
            JButton btn = new JButton(juego.getNombre());
            final String nombreJuego = juego.getNombre();
            btn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dispose();
                    cp.ctrlJuego.iniciarNuevaPartida(nombreJuego);
                }
            });
            btn.setFont(new Font("Arial", Font.PLAIN, 14));
            panelBotones.add(btn);
        }
        panel.add(panelBotones, BorderLayout.CENTER);

        getContentPane().add(panel);
        setVisible(true);
    }
}
