package vista;

import javax.swing.JInternalFrame;

/**
 * Clase abstracta base para todas las ventanas de juego.
 * <p>
 * Define el contrato común que deben cumplir las vistas de juego concretas:
 * tener un título y construir su interfaz mediante {@link #crearVista()}.
 * Todas las ventanas de juego son {@link JInternalFrame} dentro del
 * {@code JDesktopPane} de la {@link VentanaPrincipal}.
 * </p>
 *
 * <p>Subclases concretas:</p>
 * <ul>
 *   <li>{@link VentanaAhorcado} — interfaz del juego del Ahorcado.</li>
 *   <li>{@link VentanaPasapalabra} — interfaz del rosco de Pasapalabra.</li>
 * </ul>
 */
public abstract class VentanaPartida extends JInternalFrame {

    /**
     * Construye la ventana de partida con el título indicado.
     * El tamaño por defecto es 600 × 500 píxeles.
     *
     * @param titulo título de la ventana interna.
     */
    public VentanaPartida(String titulo) {
        super(titulo, true, true, true, true);
        setSize(600, 500);
    }

    /**
     * Construye y añade todos los componentes visuales de la ventana de juego.
     * Cada subclase implementa su propia interfaz con los controles necesarios.
     */
    public abstract void crearVista();
}
