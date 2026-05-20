package model.match;

import model.game.Game;
import model.game.GameState;
import model.stats.MatchResult;
import model.user.User;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Representa una sesión de juego activa o pausada.
 * Es la unidad que se persiste a disco cuando el jugador pausa la partida,
 * y que se recupera para reanudarla en una ejecución posterior.
 *
 * Contiene una referencia al objeto {@link Game} (que a su vez guarda todo
 * el estado interno del juego) y la lista de participantes.
 *
 * Al finalizar, genera un {@link MatchResult} por cada participante.
 *
 * Implementa Serializable para su almacenamiento mediante FileMatchDAO.
 */
public class Match implements Serializable {

    private static final long serialVersionUID = 1L;

    // ─── Atributos ────────────────────────────────────────────────────────────

    /** Identificador único de esta partida. */
    private final UUID matchId;

    /** Juego asociado a esta partida (contiene toda la lógica y estado). */
    private final Game game;

    /** Jugadores que participan en esta partida (al menos uno). */
    private final List<User> participants;

    /** Fecha y hora en que se inició la partida. */
    private final LocalDateTime startTime;

    /** Estado actual de la partida (refleja el estado del juego). */
    private GameState state;

    // ─── Constructor ──────────────────────────────────────────────────────────

    /**
     * Crea una nueva partida para el juego y los participantes indicados.
     * La partida queda en estado IN_PROGRESS y se llama a {@code initGame()}.
     *
     * @param game         instancia del juego a jugar
     * @param participants lista de usuarios que participan (ya autenticados)
     * @throws IllegalArgumentException si la lista de participantes está vacía
     */
    public Match(Game game, List<User> participants) {
        if (participants == null || participants.isEmpty()) {
            throw new IllegalArgumentException("La partida debe tener al menos un participante.");
        }
        this.matchId = UUID.randomUUID();
        this.game = game;
        this.participants = new ArrayList<>(participants);
        this.startTime = LocalDateTime.now();
        this.state = GameState.IN_PROGRESS;

        // Registrar jugadores en el juego y arrancarlo
        for (User u : participants) {
            game.addPlayer(u);
        }
        game.initGame();
        game.setState(GameState.IN_PROGRESS);
    }

    // ─── Métodos de ciclo de vida ─────────────────────────────────────────────

    /**
     * Pausa la partida.
     * Delega la pausa al juego interno y actualiza el estado de la partida.
     * Después de llamar a este método, FileMatchDAO debe serializar este objeto.
     */
    public void save() {
        game.pause();
        this.state = GameState.PAUSED;
    }

    /**
     * Reanuda una partida previamente pausada.
     * Delega la reanudación al juego interno y actualiza el estado.
     */
    public void resume() {
        game.resume();
        this.state = GameState.IN_PROGRESS;
    }

    /**
     * Finaliza la partida y genera un {@link MatchResult} por cada participante.
     * Marca el juego y la partida como FINISHED.
     *
     * La puntuación de cada jugador se obtiene mediante {@code game.calculateScore(user)}.
     * Se considera ganador al jugador con mayor puntuación; en caso de empate,
     * todos los jugadores empatados se marcan como ganadores.
     *
     * @return lista de resultados, uno por participante
     */
    public List<MatchResult> finish() {
        game.setState(GameState.FINISHED);
        this.state = GameState.FINISHED;

        // Calcular puntuaciones
        int maxScore = Integer.MIN_VALUE;
        int[] scores = new int[participants.size()];
        for (int i = 0; i < participants.size(); i++) {
            scores[i] = game.calculateScore(participants.get(i));
            if (scores[i] > maxScore) {
                maxScore = scores[i];
            }
        }

        // Generar resultados
        List<MatchResult> results = new ArrayList<>();
        for (int i = 0; i < participants.size(); i++) {
            boolean won = (scores[i] == maxScore);
            results.add(new MatchResult(matchId, game.getGameName(), scores[i], LocalDateTime.now(), won));
        }
        return results;
    }

    // ─── Getters ──────────────────────────────────────────────────────────────

    /** @return identificador único de la partida */
    public UUID getMatchId() {
        return matchId;
    }

    /** @return juego asociado a esta partida */
    public Game getGame() {
        return game;
    }

    /** @return lista de participantes */
    public List<User> getParticipants() {
        return participants;
    }

    /** @return fecha y hora de inicio de la partida */
    public LocalDateTime getStartTime() {
        return startTime;
    }

    /** @return estado actual de la partida */
    public GameState getState() {
        return state;
    }
}