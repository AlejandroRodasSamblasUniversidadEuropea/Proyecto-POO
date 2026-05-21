package modelo;

import java.time.LocalDate;

/**
 * Representa una sesión de juego, ya sea en curso o finalizada.
 * <p>
 * Almacena todos los datos necesarios para guardar y reanudar una partida:
 * el juego, los jugadores, sus puntuaciones, el turno actual, si está
 * terminada y el estado interno del juego serializado como texto.
 * </p>
 *
 * <h3>Formato de serialización en {@code partidas.txt}</h3>
 * <pre>id;nombreJuego;fecha;finalizada;turno;user1,pts1;user2,pts2;...|estadoGuardado</pre>
 * <p>El campo {@code finalizada} es {@code 1} si la partida terminó o {@code 0} si sigue en curso.</p>
 * <p>El estado específico del juego se separa del resto mediante el carácter {@code |}.</p>
 */
public class Partida {

    /** Identificador único de la partida. */
    private int id;

    /** Juego al que corresponde esta partida (Ahorcado o Pasapalabra). */
    private Juego juego;

    /** Array de jugadores que participan en la partida. */
    private Jugador[] jugadores;

    /** Puntuaciones de cada jugador, en el mismo orden que el array de jugadores. */
    private int[] puntuaciones;

    /** Indica si la partida ha terminado. */
    private boolean finalizada;

    /** Fecha en la que se inició la partida. */
    private LocalDate fecha;

    /** Índice del jugador cuyo turno es actualmente. */
    private int turnoActual;

    /**
     * Estado interno del juego serializado como texto.
     * <ul>
     *   <li>Ahorcado: {@code "PALABRA;letrasUsadas;fallos"}</li>
     *   <li>Pasapalabra: {@code "A=C;B=P;C=F;..."}</li>
     * </ul>
     */
    private String estadoGuardado;

    /**
     * Crea una nueva partida con estado inicial vacío.
     * La fecha se establece automáticamente al día actual.
     *
     * @param id       identificador único de la partida.
     * @param juego    juego al que pertenece esta partida.
     * @param jugadores array de jugadores participantes.
     */
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

    /**
     * Crea una partida con todos sus datos ya conocidos (uso al cargar desde fichero).
     *
     * @param id             identificador único de la partida.
     * @param juego          juego al que pertenece.
     * @param jugadores      array de jugadores participantes.
     * @param puntuaciones   puntuaciones actuales de cada jugador.
     * @param finalizada     {@code true} si la partida ya ha terminado.
     * @param fecha          fecha de inicio de la partida.
     * @param turnoActual    índice del jugador con turno activo.
     * @param estadoGuardado estado interno del juego serializado.
     */
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

    /**
     * Devuelve el identificador único de la partida.
     *
     * @return id de la partida.
     */
    public int getId() { return id; }

    /**
     * Devuelve el juego al que pertenece esta partida.
     *
     * @return instancia de {@link Juego}.
     */
    public Juego getJuego() { return juego; }

    /**
     * Devuelve el array de jugadores participantes.
     *
     * @return array de {@link Jugador}.
     */
    public Jugador[] getJugadores() { return jugadores; }

    /**
     * Devuelve el array de puntuaciones, en el mismo orden que los jugadores.
     *
     * @return array de enteros con las puntuaciones.
     */
    public int[] getPuntuaciones() { return puntuaciones; }

    /**
     * Indica si la partida ha finalizado.
     *
     * @return {@code true} si la partida está terminada.
     */
    public boolean isFinalizada() { return finalizada; }

    /**
     * Devuelve la fecha de inicio de la partida.
     *
     * @return fecha como {@link LocalDate}.
     */
    public LocalDate getFecha() { return fecha; }

    /**
     * Devuelve el índice del jugador cuyo turno es actualmente.
     *
     * @return índice en el array de jugadores.
     */
    public int getTurnoActual() { return turnoActual; }

    /**
     * Devuelve el estado interno del juego serializado como texto.
     *
     * @return cadena de estado del juego.
     */
    public String getEstadoGuardado() { return estadoGuardado; }

    /**
     * Actualiza la puntuación de un jugador concreto por su índice.
     * Si el índice está fuera de rango, la operación no tiene efecto.
     *
     * @param indiceJugador índice del jugador en el array (0-based).
     * @param puntos        nueva puntuación a asignar.
     */
    public void setPuntuacion(int indiceJugador, int puntos) {
        if (indiceJugador >= 0 && indiceJugador < puntuaciones.length) {
            this.puntuaciones[indiceJugador] = puntos;
        }
    }

    /**
     * Marca la partida como finalizada.
     */
    public void finalizarPartida() {
        this.finalizada = true;
    }

    /**
     * Actualiza el índice del jugador con turno activo.
     *
     * @param turno nuevo índice de turno.
     */
    public void setTurnoActual(int turno) {
        this.turnoActual = turno;
    }

    /**
     * Actualiza el estado interno del juego serializado.
     *
     * @param estado nueva cadena de estado.
     */
    public void setEstadoGuardado(String estado) {
        this.estadoGuardado = estado;
    }

    /**
     * Devuelve la puntuación de un jugador identificado por su nombre de usuario.
     * Si el jugador no participa en esta partida, devuelve 0.
     *
     * @param username nombre de usuario del jugador.
     * @return puntuación del jugador, o 0 si no participa.
     */
    public int getPuntuacionDeJugador(String username) {
        for (int i = 0; i < jugadores.length; i++) {
            if (jugadores[i].getUsername().equals(username)) {
                return puntuaciones[i];
            }
        }
        return 0;
    }

    /**
     * Comprueba si un jugador dado participa en esta partida.
     *
     * @param username nombre de usuario del jugador a buscar.
     * @return {@code true} si el jugador está en la lista de participantes.
     */
    public boolean participaJugador(String username) {
        for (Jugador j : jugadores) {
            if (j.getUsername().equals(username)) return true;
        }
        return false;
    }

    /**
     * Serializa la partida a una línea de texto para guardar en {@code partidas.txt}.
     * <p>Formato: {@code id;nombreJuego;fecha;finalizada;turno;user1,pts1;user2,pts2;...|estadoGuardado}</p>
     *
     * @return cadena con todos los datos de la partida.
     */
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

    /**
     * Devuelve una representación legible de la partida con id, juego, fecha,
     * estado y puntuaciones de todos los jugadores.
     *
     * @return cadena con resumen de la partida.
     */
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
