package vista;

import javax.swing.JInternalFrame;

/**
 * Clase abstracta de la que heredan todas las ventanas de juego.
 * Cada juego concreto implementa crearVista() con su propia interfaz.
 */
public abstract class VentanaPartida extends JInternalFrame {

    public VentanaPartida(String titulo) {
        super(titulo, true, true, true, true);
        setSize(600, 500);
    }

    public abstract void crearVista();
}
