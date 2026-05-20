package modelo;

/**
 * Clase abstracta que representa un juego disponible en la plataforma.
 * <p>
 * Define el contrato común a todos los juegos: tener un nombre y saber
 * cómo iniciar una nueva partida con un conjunto de jugadores.
 * </p>
 *
 * <p>Subclases concretas:</p>
 * <ul>
 *   <li>{@link Ahorcado} — juego de adivinar letras de una palabra oculta.</li>
 *   <li>{@link Pasapalabra} — rosco de preguntas por letras del abecedario.</li>
 * </ul>
 */
public abstract class Juego {

    /** Nombre identificador del juego (e.g. "Ahorcado", "Pasapalabra"). */
    protected String nombre;

    /**
     * Construye un juego con el nombre indicado.
     *
     * @param nombre nombre del juego.
     */
    public Juego(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Devuelve el nombre del juego.
     *
     * @return nombre del juego.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Crea una nueva {@link Partida} inicializada con el estado de comienzo del juego.
     * <p>
     * Cada subclase implementa su propia lógica de inicialización (elección de
     * palabra aleatoria para Ahorcado, estado inicial del rosco para Pasapalabra, etc.).
     * </p>
     *
     * @param jugadores array de jugadores que participarán en la partida.
     * @return una nueva {@link Partida} lista para jugar.
     */
    public abstract Partida iniciarPartida(Jugador[] jugadores);
}
