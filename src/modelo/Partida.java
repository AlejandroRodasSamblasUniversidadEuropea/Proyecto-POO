package modelo;

import java.time.LocalDate;

/**
 * Representa una partida jugada o en curso.
 * Guarda quienes juegan, las puntuaciones, si esta terminada y la fecha.
 *
 * Formato en partidas.txt (una linea por partida):
 * id;nombreJuego;fecha;finalizada;turno;user1,puntuacion1;user2,puntuacion2;...
 * El estado especifico de cada juego se guarda al final separado por |
 */
public class Partida {
    private int id;
    private Juego juego;
    private Jugador[] jugadores;
    private int[] puntuaciones;
    private boolean finalizada;
    private LocalDate fecha;
    private int turnoActual; // indice del jugador cuyo turno es

    // Estado interno del juego serializado como texto (para guardar/continuar)
    private String estadoGuardado;

    public Partida(int id, Juego juego, Jugador[] jugadores) {
        this.id = id;
        this.juego = juego;
        this.jugadores = jugadores;
        this.puntuaciones = new int[jugadores.length];
        this.finalizada = false;
        this.fecha = LocalDate.now();
        this.turnoActual = 0;
        this.estadoGuardado = "";
    }

    // Constructor para cargar desde fichero (con todos los datos ya conocidos)
    public Partida(int id, Juego juego, Jugador[] jugadores, int[] puntuaciones,
                   boolean finalizada, LocalDate fecha, int turnoActual, String estadoGuardado) {
        this.id = id;
        this.juego = juego;
        this.jugadores = jugadores;
        this.puntuaciones = puntuaciones;
        this.finalizada = finalizada;
        this.fecha = fecha;
        this.turnoActual = turnoActual;
        this.estadoGuardado = estadoGuardado;
    }

    public int getId() { return id; }
    public Juego getJuego() { return juego; }
    public Jugador[] getJugadores() { return jugadores; }
    public int[] getPuntuaciones() { return puntuaciones; }
    public boolean isFinalizada() { return finalizada; }
    public LocalDate getFecha() { return fecha; }
    public int getTurnoActual() { return turnoActual; }
    public String getEstadoGuardado() { return estadoGuardado; }

    public void setPuntuacion(int indiceJugador, int puntos) {
        if (indiceJugador >= 0 && indiceJugador < puntuaciones.length) {
            this.puntuaciones[indiceJugador] = puntos;
        }
    }

    public void finalizarPartida() {
        this.finalizada = true;
    }

    public void setTurnoActual(int turno) {
        this.turnoActual = turno;
    }

    public void setEstadoGuardado(String estado) {
        this.estadoGuardado = estado;
    }

    // Devuelve la puntuacion de un jugador concreto por su username
    public int getPuntuacionDeJugador(String username) {
        for (int i = 0; i < jugadores.length; i++) {
            if (jugadores[i].getUsername().equals(username)) {
                return puntuaciones[i];
            }
        }
        return 0;
    }

    // Comprueba si un jugador participa en esta partida
    public boolean participaJugador(String username) {
        for (Jugador j : jugadores) {
            if (j.getUsername().equals(username)) return true;
        }
        return false;
    }

    // Serializa la partida a una linea de texto para guardar en fichero
    // Formato: id;nombreJuego;fecha;finalizada;turno;user1,pts1;user2,pts2;...|estadoGuardado
    public String toStringEnFichero() {
        StringBuilder sb = new StringBuilder();
        sb.append(id).append(";");
        sb.append(juego.getNombre()).append(";");
        sb.append(fecha.toString()).append(";");
        sb.append(finalizada ? "1" : "0").append(";");
        sb.append(turnoActual).append(";");
        for (int i = 0; i < jugadores.length; i++) {
            sb.append(jugadores[i].getUsername()).append(",").append(puntuaciones[i]);
            if (i < jugadores.length - 1) sb.append(";");
        }
        sb.append("|").append(estadoGuardado);
        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(id).append("] ").append(juego.getNombre());
        sb.append(" - ").append(fecha);
        sb.append(finalizada ? " (Terminada)" : " (En curso)");
        sb.append("\n");
        for (int i = 0; i < jugadores.length; i++) {
            sb.append("  ").append(jugadores[i].getUsername())
              .append(": ").append(puntuaciones[i]).append(" pts\n");
        }
        return sb.toString();
    }
}
