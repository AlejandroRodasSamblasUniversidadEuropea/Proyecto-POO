package control;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import modelo.Jugador;
import modelo.Partida;
import modelo.SistemaJuegos;
import modelo.Usuario;

/**
 * Contiene la lógica exclusiva del administrador del sistema.
 * <p>
 * Genera los textos formateados para mostrar en el panel de administración:
 * rankings por juego y la información detallada de todos los usuarios con
 * sus últimas partidas.
 * </p>
 */
public class ControladorAdmin {

    /** Sistema central con el que se consultan usuarios y partidas. */
    private SistemaJuegos sj;

    /**
     * Construye el controlador de administración.
     *
     * @param sj sistema central de juegos.
     */
    public ControladorAdmin(SistemaJuegos sj) {
        this.sj = sj;
    }

    /**
     * Genera el ranking de un juego como texto formateado.
     * <p>
     * Obtiene todas las partidas finalizadas del juego indicado, las ordena
     * de mayor a menor puntuación (jugador principal) y las numera por posición.
     * </p>
     *
     * @param nombreJuego nombre del juego cuyo ranking se desea consultar (e.g. "Ahorcado").
     * @return cadena de texto con el ranking, o un mensaje indicando que no hay partidas.
     */
    public String getRankingDeJuego(String nombreJuego) {
        ArrayList<Partida> partidas = sj.getPartidasDeJuego(nombreJuego);

        if (partidas.isEmpty()) {
            return "No hay partidas finalizadas de " + nombreJuego + ".";
        }

        Collections.sort(partidas, new Comparator<Partida>() {
            @Override
            public int compare(Partida p1, Partida p2) {
                return p2.getPuntuaciones()[0] - p1.getPuntuaciones()[0];
            }
        });

        StringBuilder sb = new StringBuilder();
        sb.append("=== RANKING: ").append(nombreJuego.toUpperCase()).append(" ===\n\n");
        int posicion = 1;
        for (Partida p : partidas) {
            for (int i = 0; i < p.getJugadores().length; i++) {
                sb.append(posicion).append(". ");
                sb.append(p.getJugadores()[i].getUsername());
                sb.append(" → ").append(p.getPuntuaciones()[i]).append(" pts");
                sb.append("  [").append(p.getFecha()).append("]\n");
                posicion++;
            }
        }
        return sb.toString();
    }

    /**
     * Genera un resumen de todos los usuarios del sistema con sus últimas partidas.
     * <p>
     * Por cada usuario se muestran hasta las últimas 5 partidas con el nombre del juego,
     * la fecha, la puntuación y si está en curso o finalizada.
     * </p>
     *
     * @return cadena de texto con la información de todos los usuarios.
     */
    public String getInfoTodosLosUsuarios() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== LISTA DE USUARIOS ===\n\n");

        for (Usuario u : sj.usuarios) {
            sb.append("● ").append(u.toString()).append("\n");
            ArrayList<Partida> partidas = sj.getPartidasDeJugador(u.getUsername());
            if (partidas.isEmpty()) {
                sb.append("  (Sin partidas jugadas)\n");
            } else {
                int inicio = Math.max(0, partidas.size() - 5);
                for (int i = inicio; i < partidas.size(); i++) {
                    Partida p = partidas.get(i);
                    sb.append("  - ").append(p.getJuego().getNombre());
                    sb.append(", ").append(p.getFecha());
                    sb.append(", ").append(p.getPuntuacionDeJugador(u.getUsername())).append(" pts");
                    sb.append(p.isFinalizada() ? "" : " (en curso)");
                    sb.append("\n");
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
