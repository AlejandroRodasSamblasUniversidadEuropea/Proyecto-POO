package vista;

import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;

import control.ControladorPrincipal;
import modelo.Partida;

/**
 * Ventana interna que muestra el historial de partidas del jugador activo.
 * <p>
 * Presenta una tabla con todas las partidas del usuario (tanto terminadas como
 * en curso) con las columnas: juego, fecha, puntuación y estado.
 * En la parte inferior se muestra un resumen con el total de partidas,
 * cuántas están terminadas y cuántas siguen en curso.
 * </p>
 *
 * <p>Solo es accesible para usuarios de tipo {@link modelo.Jugador}.</p>
 */
public class VentanaEstadisticas extends JInternalFrame {

    /** Controlador principal del que se obtiene el usuario activo y sus partidas. */
    private ControladorPrincipal cp;

    /**
     * Construye la ventana de estadísticas para el usuario activo.
     *
     * @param cp controlador principal con la referencia al usuario activo y al sistema.
     */
    public VentanaEstadisticas(ControladorPrincipal cp) {
        super("Mis estadisticas", true, true, true, true);
        this.cp = cp;
        crearVista();
    }

    /**
     * Construye y añade todos los componentes visuales:
     * un título, la tabla de partidas y el resumen en la parte inferior.
     * Si el jugador no tiene partidas, muestra un mensaje informativo.
     */
    private void crearVista() {
        setSize(500, 400);
        setLocation(100, 80);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titulo = new JLabel("Partidas de: " + cp.usuarioActivo.getUsername(), JLabel.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(titulo, BorderLayout.NORTH);

        ArrayList<Partida> misPartidas = cp.sj.getPartidasDeJugador(cp.usuarioActivo.getUsername());

        if (misPartidas.isEmpty()) {
            panel.add(new JLabel("No has jugado ninguna partida todavia.", JLabel.CENTER),
                      BorderLayout.CENTER);
        } else {
            String[] columnas = {"Juego", "Fecha", "Puntuacion", "Estado"};
            String[][] datos  = new String[misPartidas.size()][4];

            for (int i = 0; i < misPartidas.size(); i++) {
                Partida p  = misPartidas.get(i);
                datos[i][0] = p.getJuego().getNombre();
                datos[i][1] = p.getFecha().toString();
                datos[i][2] = p.getPuntuacionDeJugador(cp.usuarioActivo.getUsername()) + " pts";
                datos[i][3] = p.isFinalizada() ? "Terminada" : "En curso";
            }

            JTable tabla = new JTable(datos, columnas) {
                @Override
                public boolean isCellEditable(int r, int c) { return false; }
            };
            tabla.setFont(new Font("Arial", Font.PLAIN, 13));
            tabla.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
            tabla.setRowHeight(22);

            panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        }

        long terminadas = misPartidas.stream().filter(Partida::isFinalizada).count();
        JLabel resumen = new JLabel("Total: " + misPartidas.size()
            + " partidas  |  Terminadas: " + terminadas
            + "  |  En curso: " + (misPartidas.size() - terminadas), JLabel.CENTER);
        resumen.setFont(new Font("Arial", Font.ITALIC, 12));
        panel.add(resumen, BorderLayout.SOUTH);

        getContentPane().add(panel);
        setVisible(true);
    }
}
