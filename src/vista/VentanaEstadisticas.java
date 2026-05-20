package vista;

import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;

import control.ControladorPrincipal;
import modelo.Partida;

/**
 * Muestra las partidas del jugador que tiene la sesion iniciada.
 * Para cada partida: juego, fecha, puntuacion y si esta terminada o en curso.
 */
public class VentanaEstadisticas extends JInternalFrame {

    private ControladorPrincipal cp;

    public VentanaEstadisticas(ControladorPrincipal cp) {
        super("Mis estadisticas", true, true, true, true);
        this.cp = cp;
        crearVista();
    }

    private void crearVista() {
        setSize(500, 400);
        setLocation(100, 80);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titulo = new JLabel("Partidas de: " + cp.usuarioActivo.getUsername(), JLabel.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(titulo, BorderLayout.NORTH);

        // Obtenemos las partidas del jugador activo
        ArrayList<Partida> misPartidas = cp.sj.getPartidasDeJugador(cp.usuarioActivo.getUsername());

        if (misPartidas.isEmpty()) {
            panel.add(new JLabel("No has jugado ninguna partida todavia.", JLabel.CENTER),
                      BorderLayout.CENTER);
        } else {
            // Tabla con las partidas
            String[] columnas = {"Juego", "Fecha", "Puntuacion", "Estado"};
            String[][] datos = new String[misPartidas.size()][4];

            for (int i = 0; i < misPartidas.size(); i++) {
                Partida p = misPartidas.get(i);
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

        // Resumen
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
