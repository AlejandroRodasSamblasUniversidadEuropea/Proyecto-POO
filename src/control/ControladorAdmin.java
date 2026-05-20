package control;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import modelo.Jugador;
import modelo.Partida;
import modelo.SistemaJuegos;
import modelo.Usuario;

/**
 * Logica exclusiva del administrador.
 * Genera los textos para mostrar rankings y estadisticas de usuarios.
 */
public class ControladorAdmin {

    private SistemaJuegos sj;

    public ControladorAdmin(SistemaJuegos sj) {
        this.sj = sj;
    }

    /**
     * Devuelve el ranking de un juego como texto.
     * Muestra las partidas finalizadas ordenadas de mayor a menor puntuacion.
     */
    public String getRankingDeJuego(String nombreJuego) {
        ArrayList<Partida> partidas = sj.getPartidasDeJuego(nombreJuego);

        if (partidas.isEmpty()) {
            return "No hay partidas finalizadas de " + nombreJuego + ".";
        }

        // Ordenamos por la primera puntuacion (jugador principal) de mayor a menor
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
     * Devuelve la informacion de todos los usuarios con sus ultimas partidas.
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
                // Mostramos las ultimas 5 partidas
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
