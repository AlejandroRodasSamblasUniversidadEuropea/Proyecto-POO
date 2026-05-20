package modelo;

/**
 * Representa una pregunta del rosco de Pasapalabra.
 * Cada pregunta tiene una letra, el enunciado y la respuesta correcta.
 */
public class Pregunta {
    private char letra;
    private String enunciado;
    private String respuesta;

    public Pregunta(char letra, String enunciado, String respuesta) {
        this.letra = letra;
        this.enunciado = enunciado;
        this.respuesta = respuesta.toUpperCase().trim();
    }

    public char getLetra() {
        return letra;
    }

    public String getEnunciado() {
        return enunciado;
    }

    // Comprueba si la respuesta del jugador es correcta (ignora mayusculas/minusculas)
    public boolean esCorrecta(String respuestaJugador) {
        return this.respuesta.equalsIgnoreCase(respuestaJugador.trim());
    }

    // Formato para guardar en preguntas.txt: letra;enunciado;respuesta
    public String toStringEnFichero() {
        return letra + ";" + enunciado + ";" + respuesta;
    }

    @Override
    public String toString() {
        return "Letra " + letra + ": " + enunciado;
    }
}
