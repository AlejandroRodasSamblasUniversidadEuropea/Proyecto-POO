package modelo;

import java.util.ArrayList;

/**
 * Implementación del juego Pasapalabra.
 * <p>
 * El jugador responde preguntas de cultura general; cada pregunta está
 * asociada a una letra del abecedario. El jugador puede responder o pasar
 * (pasapalabra) y volver a la pregunta más adelante.
 * </p>
 *
 * <h3>Puntuación por pregunta</h3>
 * <ul>
 *   <li>Respuesta correcta: {@code +1} punto.</li>
 *   <li>Respuesta incorrecta: {@code -1} punto.</li>
 *   <li>Pasapalabra (pendiente): {@code 0} puntos.</li>
 * </ul>
 *
 * <h3>Formato del estado guardado</h3>
 * <p>Para cada letra, su resultado separado por punto y coma:</p>
 * <pre>A=C;B=P;C=F;D=P;...</pre>
 * <p>Donde {@code C} = correcta, {@code F} = fallada, {@code P} = pendiente.</p>
 */
public class Pasapalabra extends Juego {

    /** Lista de preguntas del rosco, cargadas desde {@code data/preguntas.txt}. */
    private ArrayList<Pregunta> preguntas;

    /**
     * Construye una instancia del juego Pasapalabra con la lista de preguntas indicada.
     *
     * @param preguntas lista de preguntas del rosco, una por letra del abecedario.
     */
    public Pasapalabra(ArrayList<Pregunta> preguntas) {
        super("Pasapalabra");
        this.preguntas = preguntas;
    }

    /**
     * Devuelve la lista completa de preguntas del rosco.
     *
     * @return lista de {@link Pregunta} del juego.
     */
    public ArrayList<Pregunta> getPreguntas() {
        return preguntas;
    }

    /**
     * Busca y devuelve la pregunta correspondiente a una letra concreta.
     *
     * @param letra letra del abecedario cuya pregunta se desea obtener.
     * @return la {@link Pregunta} asociada a esa letra, o {@code null} si no existe.
     */
    public Pregunta getPreguntaPorLetra(char letra) {
        for (Pregunta p : preguntas) {
            if (Character.toUpperCase(p.getLetra()) == Character.toUpperCase(letra)) {
                return p;
            }
        }
        return null;
    }

    /**
     * Genera el estado inicial del rosco: todas las letras marcadas como pendientes.
     *
     * @return estado inicial con formato {@code "A=P;B=P;C=P;..."}.
     */
    public String crearEstadoInicial() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < preguntas.size(); i++) {
            char letra = Character.toUpperCase(preguntas.get(i).getLetra());
            sb.append(letra).append("=P");
            if (i < preguntas.size() - 1) sb.append(";");
        }
        return sb.toString();
    }

    /**
     * Calcula la puntuación total a partir del estado actual del rosco.
     * <p>
     * Suma {@code +1} por cada letra correcta ({@code C}) y
     * resta {@code -1} por cada letra fallada ({@code F}).
     * Las letras pendientes ({@code P}) no modifican la puntuación.
     * </p>
     *
     * @param estado cadena de estado con formato {@code "A=C;B=P;C=F;..."}.
     * @return puntuación acumulada (puede ser negativa).
     */
    public int calcularPuntuacion(String estado) {
        if (estado == null || estado.isEmpty()) return 0;
        int puntos = 0;
        String[] partes = estado.split(";");
        for (String parte : partes) {
            if (parte.contains("=")) {
                String resultado = parte.split("=")[1];
                if (resultado.equals("C")) puntos++;
                else if (resultado.equals("F")) puntos--;
            }
        }
        return puntos;
    }

    /**
     * Comprueba si el rosco está completo, es decir, si no quedan letras pendientes.
     *
     * @param estado cadena de estado con formato {@code "A=C;B=P;C=F;..."}.
     * @return {@code true} si no hay ninguna letra con estado {@code P} (pendiente).
     */
    public boolean roscoCompleto(String estado) {
        return !estado.contains("=P");
    }

    /**
     * Crea una nueva {@link Partida} de Pasapalabra con todas las letras pendientes.
     *
     * @param jugadores array de jugadores que participarán en la partida.
     * @return nueva partida de Pasapalabra lista para jugar.
     */
    @Override
    public Partida iniciarPartida(Jugador[] jugadores) {
        Partida p = new Partida(0, this, jugadores);
        p.setEstadoGuardado(crearEstadoInicial());
        return p;
    }
}
