package model.game;

import model.user.User;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Clase abstracta base para todos los juegos de la aplicación.
 * Define el contrato que deben cumplir todas las implementaciones de juego
 * y proporciona la lógica común de turnos y ciclo de vida de la partida.
 *
 * Patrón: Template Method — el flujo initGame() → processInput() → isGameOver()
 *         → calculateScore() es fijo; los pasos concretos los implementa cada subclase.
 *
 * Implementa Serializable para permitir la pausa y reanudación entre ejecuciones.
 */
public abstract class Game implements Serializable {

    private static final long serialVersionUID = 1L;

    // ─── Atributos ────────────────────────────────────────────────────────────

    // Identificador único del tipo de juego (ej. "PASAPALABRA", "HANGMAN"). 
    protected String gameId;

    // Estado actual del ciclo de vida de la partida. 
    protected GameState state;

    // Lista de jugadores participantes en esta partida. 
    protected List<User> players;

    // Índice del jugador que tiene el turno actual en {@code players}. 
    protected int currentTurnIndex;

    // ─── Constructor ──────────────────────────────────────────────────────────

    /**
     * Inicializa el juego con estado NOT_STARTED y sin jugadores.
     * Los jugadores se añaden posteriormente mediante {@link #addPlayer(User)}.
     */
    public Game() {
        this.players = new ArrayList<>();
        this.state = GameState.NOT_STARTED;
        this.currentTurnIndex = 0;
    }

    // ─── Métodos abstractos ───────────────────────────────────────────────────

    /**
     * Inicializa el tablero, preguntas o estado interno del juego.
     * Se llama una única vez antes de la primera jugada.
     */
    public abstract void initGame();

    /**
     * Procesa la entrada del jugador actual (letra, palabra, movimiento, etc.).
     *
     * @param input cadena de texto introducida por el jugador
     */
    public abstract void processInput(String input);

    /**
     * Calcula la puntuación obtenida por un jugador al finalizar la partida.
     *
     * @param user el jugador cuya puntuación se calcula
     * @return puntuación numérica del jugador
     */
    public abstract int calculateScore(User user);

    /**
     * Determina si la partida ha terminado según las reglas del juego.
     *
     * @return {@code true} si la partida ha concluido
     */
    public abstract boolean isGameOver();

    /**
     * Devuelve el nombre legible del juego (ej. "Pasapalabra", "Ahorcado").
     *
     * @return nombre del juego
     */
    public abstract String getGameName();

    /**
     * Número máximo de jugadores permitidos en este juego.
     * Cada subclase define su propio límite (ej. Pasapalabra = 1, Ahorcado = 2).
     *
     * @return número máximo de jugadores
     */
    public abstract int getMaxPlayers();

    /**
     * Exporta el estado interno del juego a un mapa serializable.
     * Se usa para guardar la partida en pausa.
     *
     * @return mapa con las claves y valores que representan el estado del juego
     */
    public abstract Map<String, Object> getSaveableState();

    /**
     * Restaura el estado interno del juego a partir de un mapa previamente guardado.
     * Se usa al reanudar una partida pausada.
     *
     * @param state mapa con el estado guardado
     */
    public abstract void restoreFromState(Map<String, Object> state);

    // ─── Métodos concretos ────────────────────────────────────────────────────

    /**
     * Añade un jugador a la partida.
     * Lanza excepción si se supera el número máximo de jugadores.
     *
     * @param user jugador a añadir
     * @throws IllegalStateException si ya se alcanzó el máximo de jugadores
     */
    public void addPlayer(User user) {
        if (players.size() >= getMaxPlayers()) {
            throw new IllegalStateException(
                "Número máximo de jugadores alcanzado: " + getMaxPlayers()
            );
        }
        players.add(user);
    }

    /**
     * Devuelve el jugador que tiene el turno en este momento.
     *
     * @return usuario con el turno actual
     * @throws IllegalStateException si no hay jugadores registrados
     */
    public User getCurrentPlayer() {
        if (players.isEmpty()) {
            throw new IllegalStateException("No hay jugadores en la partida.");
        }
        return players.get(currentTurnIndex);
    }

    /**
     * Avanza el turno al siguiente jugador en modo circular.
     * Si solo hay un jugador, el índice permanece en 0.
     */
    public void nextTurn() {
        if (!players.isEmpty()) {
            currentTurnIndex = (currentTurnIndex + 1) % players.size();
        }
    }

    /**
     * Pausa la partida cambiando el estado a PAUSED.
     * Solo tiene efecto si la partida estaba IN_PROGRESS.
     */
    public void pause() {
        if (state == GameState.IN_PROGRESS) {
            state = GameState.PAUSED;
        }
    }

    /**
     * Reanuda la partida cambiando el estado a IN_PROGRESS.
     * Solo tiene efecto si la partida estaba PAUSED.
     */
    public void resume() {
        if (state == GameState.PAUSED) {
            state = GameState.IN_PROGRESS;
        }
    }

    // ─── Getters y setters ────────────────────────────────────────────────────

    /** @return identificador del tipo de juego */
    public String getGameId() {
        return gameId;
    }

    /** @return estado actual del juego */
    public GameState getState() {
        return state;
    }

    /** @param state nuevo estado del juego */
    public void setState(GameState state) {
        this.state = state;
    }

    /** @return lista de jugadores de la partida */
    public List<User> getPlayers() {
        return players;
    }

    /** @return índice del jugador con el turno actual */
    public int getCurrentTurnIndex() {
        return currentTurnIndex;
    }
}