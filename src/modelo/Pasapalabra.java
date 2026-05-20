package modelo;

import java.util.ArrayList;

/**
 * Implementacion del juego Pasapalabra.
 * El jugador responde preguntas una por una.
 * Cada pregunta empieza por una letra del abecedario.
 * Acierto: +1 punto. Fallo: -1 punto. Pasapalabra: 0 puntos, se deja para el final.
 *
 * Estado guardado: para cada letra, el resultado ('C'=correcta, 'F'=fallada, 'P'=pendiente)
 * Ejemplo: "A=C;B=P;C=F;D=P;..."
 */
public class Pasapalabra extends Juego {

    // Lista de preguntas cargada desde el fichero
    private ArrayList<Pregunta> preguntas;

    public Pasapalabra(ArrayList<Pregunta> preguntas) {
        super("Pasapalabra");
        this.preguntas = preguntas;
    }

    public ArrayList<Pregunta> getPreguntas() {
        return preguntas;
    }

    // Devuelve la pregunta para una letra concreta, o null si no existe
    public Pregunta getPreguntaPorLetra(char letra) {
        for (Pregunta p : preguntas) {
            if (Character.toUpperCase(p.getLetra()) == Character.toUpperCase(letra)) {
                return p;
            }
        }
        return null;
    }

    // Crea un estado inicial: todas las letras pendientes
    // Formato: "A=P;B=P;C=P;..."
    public String crearEstadoInicial() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < preguntas.size(); i++) {
            char letra = Character.toUpperCase(preguntas.get(i).getLetra());
            sb.append(letra).append("=P");
            if (i < preguntas.size() - 1) sb.append(";");
        }
        return sb.toString();
    }

    // Calcula la puntuacion a partir del estado (C=+1, F=-1, P=0)
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

    // Comprueba si el rosco esta completo (no hay ninguna 'P')
    public boolean roscoCompleto(String estado) {
        return !estado.contains("=P");
    }

    @Override
    public Partida iniciarPartida(Jugador[] jugadores) {
        // El id se asignara desde el controlador al guardar
        Partida p = new Partida(0, this, jugadores);
        p.setEstadoGuardado(crearEstadoInicial());
        return p;
    }
}
