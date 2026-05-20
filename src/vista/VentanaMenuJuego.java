package vista;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

import control.ControladorPrincipal;
import modelo.Juego;

/**
 * Ventana interna que muestra los juegos disponibles.
 * El jugador elige el juego y el controlador arranca la partida.
 */
public class VentanaMenuJuego extends JInternalFrame {

    private ControladorPrincipal cp;
    private ArrayList<Juego> juegos;

    public VentanaMenuJuego(ControladorPrincipal cp, ArrayList<Juego> juegos) {
        super("Seleccionar juego", true, true, true, true);
        this.cp = cp;
        this.juegos = juegos;
        crearVista();
    }

    private void crearVista() {
        setSize(350, 250);
        setLocation(150, 100);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titulo = new JLabel("Elige el juego:", JLabel.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(titulo, BorderLayout.NORTH);

        // Botones dinamicos, uno por cada juego disponible
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
