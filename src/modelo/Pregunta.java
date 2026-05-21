package modelo;

/**
 * Representa una pregunta del rosco de {@link Pasapalabra}.
 * <p>
 * Cada pregunta está asociada a una letra del abecedario, tiene un enunciado
 * y una respuesta correcta. La comparación de respuestas ignora mayúsculas
 * y espacios innecesarios.
 * </p>
 *
 * <p>Formato de serialización en {@code preguntas.txt}:</p>
 * <pre>letra;enunciado;respuesta</pre>
 * <p>Ejemplo: {@code A;Con esta letra: Lenguaje de programacion de Apple;SWIFT}</p>
 */
public class Pregunta {

    /** Letra del abecedario a la que pertenece esta pregunta. */
    private char letra;

    /** Texto de la pregunta que se muestra al jugador. */
    private String enunciado;

    /** Respuesta correcta normalizada (en mayúsculas y sin espacios laterales). */
    private String respuesta;

    /**
     * Construye una pregunta con la letra, enunciado y respuesta indicados.
     * La respuesta se normaliza a mayúsculas y se eliminan los espacios laterales.
     *
     * @param letra     letra del abecedario asociada a la pregunta.
     * @param enunciado texto de la pregunta.
     * @param respuesta respuesta correcta (se convierte a mayúsculas internamente).
     */
    public Pregunta(char letra, String enunciado, String respuesta) {
        this.letra = letra;
        this.enunciado = enunciado;
        this.respuesta = respuesta.toUpperCase().trim();
    }

    /**
     * Devuelve la letra del abecedario asociada a esta pregunta.
     *
     * @return la letra de la pregunta.
     */
    public char getLetra() {
        return letra;
    }

    /**
     * Devuelve el enunciado de la pregunta.
     *
     * @return texto de la pregunta.
     */
    public String getEnunciado() {
        return enunciado;
    }

    /**
     * Comprueba si la respuesta del jugador es correcta.
     * La comparación ignora diferencias entre mayúsculas/minúsculas
     * y espacios laterales.
     *
     * @param respuestaJugador respuesta introducida por el jugador.
     * @return {@code true} si la respuesta coincide con la correcta; {@code false} en caso contrario.
     */
    public boolean esCorrecta(String respuestaJugador) {
        return this.respuesta.equalsIgnoreCase(respuestaJugador.trim());
    }

    /**
     * Serializa la pregunta al formato de línea del fichero {@code preguntas.txt}.
     * <p>Formato: {@code letra;enunciado;respuesta}</p>
     *
     * @return cadena con los datos de la pregunta listos para guardar en disco.
     */
    public String toStringEnFichero() {
        return letra + ";" + enunciado + ";" + respuesta;
    }

    /**
     * Devuelve una representación legible de la pregunta.
     *
     * @return cadena con la letra y el enunciado.
     */
    @Override
    public String toString() {
        return "Letra " + letra + ": " + enunciado;
    }
}
