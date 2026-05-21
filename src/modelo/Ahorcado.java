package modelo;

/**
 * Implementación del juego del Ahorcado.
 * <p>
 * El jugador adivina las letras de una palabra oculta elegida al azar.
 * El juego termina cuando el jugador adivina todas las letras (victoria)
 * o acumula el número máximo de fallos permitidos (derrota).
 * </p>
 *
 * <h3>Puntuación</h3>
 * <pre>puntuacion = letras_correctas_adivinadas - numero_de_fallos</pre>
 *
 * <h3>Formato del estado guardado</h3>
 * <pre>PALABRA;letrasUsadas;fallosActuales</pre>
 * <p>Ejemplo: {@code PROGRAMACION;P,A,O,X,Z;2}</p>
 * <p>Si no hay letras usadas, el segundo campo queda vacío: {@code HERENCIA;;0}</p>
 */
public class Ahorcado extends Juego {

    /** Número máximo de fallos antes de perder la partida. */
    private static final int MAX_FALLOS = 6;

    /** Palabras disponibles para elegir aleatoriamente al inicio de cada partida. */
    private String[] palabras = {
        "PROGRAMACION", "HERENCIA", "POLIMORFISMO", "CLASE",
        "OBJETO", "METODO", "VARIABLE", "COMPILADOR",
        "INTERFAZ", "BUCLE", "ARRAY", "RECURSION"
    };

    /**
     * Construye una instancia del juego Ahorcado.
     */
    public Ahorcado() {
        super("Ahorcado");
    }

    /**
     * Devuelve el número máximo de fallos permitidos antes de perder.
     *
     * @return número máximo de fallos (siempre 6).
     */
    public int getMaxFallos() {
        return MAX_FALLOS;
    }

    /**
     * Elige una palabra aleatoria de la lista de palabras disponibles.
     *
     * @return palabra seleccionada al azar en mayúsculas.
     */
    public String elegirPalabraAleatoria() {
        int indice = (int) (Math.random() * palabras.length);
        return palabras[indice];
    }

    /**
     * Construye el estado inicial de una partida nueva.
     * Se elige una palabra al azar y se inicializa con cero fallos y sin letras usadas.
     *
     * @return estado inicial con formato {@code "PALABRA;;0"}.
     */
    public String crearEstadoInicial() {
        return elegirPalabraAleatoria() + ";;0";
    }

    /**
     * Extrae la palabra oculta del estado guardado.
     *
     * @param estado cadena de estado con formato {@code "PALABRA;letrasUsadas;fallos"}.
     * @return la palabra que el jugador debe adivinar.
     */
    public String getPalabra(String estado) {
        return estado.split(";")[0];
    }

    /**
     * Extrae las letras ya utilizadas del estado guardado.
     * Las letras están separadas por comas.
     *
     * @param estado cadena de estado con formato {@code "PALABRA;letrasUsadas;fallos"}.
     * @return cadena con las letras usadas separadas por comas (puede estar vacía).
     */
    public String getLetrasUsadas(String estado) {
        String[] partes = estado.split(";", -1);
        return partes.length > 1 ? partes[1] : "";
    }

    /**
     * Extrae el número de fallos acumulados del estado guardado.
     *
     * @param estado cadena de estado con formato {@code "PALABRA;letrasUsadas;fallos"}.
     * @return número de fallos actuales; 0 si no se puede parsear.
     */
    public int getFallos(String estado) {
        String[] partes = estado.split(";", -1);
        try {
            return Integer.parseInt(partes[2]);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Construye la representación visible de la palabra, sustituyendo con {@code _}
     * las letras que aún no han sido adivinadas.
     * <p>Ejemplo: si la palabra es "PROGRAMACION" y se adivinaron P, O, A,
     * el resultado es {@code "P _ O G _ A M A C I O N"}.</p>
     *
     * @param estado cadena de estado con formato {@code "PALABRA;letrasUsadas;fallos"}.
     * @return la palabra con guiones bajos para letras no adivinadas.
     */
    public String getPalabraMostrada(String estado) {
        String palabra = getPalabra(estado);
        String letrasUsadas = getLetrasUsadas(estado).toUpperCase();
        StringBuilder sb = new StringBuilder();
        for (char c : palabra.toCharArray()) {
            if (letrasUsadas.contains(String.valueOf(c))) {
                sb.append(c);
            } else {
                sb.append("_");
            }
            sb.append(" ");
        }
        return sb.toString().trim();
    }

    /**
     * Comprueba si una letra ya ha sido utilizada en esta partida.
     *
     * @param estado cadena de estado con formato {@code "PALABRA;letrasUsadas;fallos"}.
     * @param letra  letra a comprobar (mayúscula o minúscula).
     * @return {@code true} si la letra ya fue jugada anteriormente.
     */
    public boolean letraYaUsada(String estado, char letra) {
        return getLetrasUsadas(estado).toUpperCase()
                .contains(String.valueOf(Character.toUpperCase(letra)));
    }

    /**
     * Registra una letra jugada y devuelve el nuevo estado de la partida.
     * Si la letra no está en la palabra, incrementa el contador de fallos.
     * La letra se convierte a mayúscula antes de procesarse.
     *
     * @param estado cadena de estado actual con formato {@code "PALABRA;letrasUsadas;fallos"}.
     * @param letra  letra que el jugador desea jugar.
     * @return nuevo estado actualizado tras jugar la letra.
     */
    public String jugarLetra(String estado, char letra) {
        letra = Character.toUpperCase(letra);
        String palabra = getPalabra(estado);
        String letrasUsadas = getLetrasUsadas(estado);
        int fallos = getFallos(estado);

        if (!letrasUsadas.isEmpty()) {
            letrasUsadas += "," + letra;
        } else {
            letrasUsadas = String.valueOf(letra);
        }

        if (!palabra.contains(String.valueOf(letra))) {
            fallos++;
        }

        return palabra + ";" + letrasUsadas + ";" + fallos;
    }

    /**
     * Comprueba si el jugador ha ganado (ha adivinado todas las letras de la palabra).
     *
     * @param estado cadena de estado con formato {@code "PALABRA;letrasUsadas;fallos"}.
     * @return {@code true} si todas las letras de la palabra han sido adivinadas.
     */
    public boolean haGanado(String estado) {
        String palabra = getPalabra(estado);
        String letrasUsadas = getLetrasUsadas(estado).toUpperCase();
        for (char c : palabra.toCharArray()) {
            if (!letrasUsadas.contains(String.valueOf(c))) return false;
        }
        return true;
    }

    /**
     * Comprueba si el jugador ha perdido (ha superado el número máximo de fallos).
     *
     * @param estado cadena de estado con formato {@code "PALABRA;letrasUsadas;fallos"}.
     * @return {@code true} si el número de fallos es igual o superior al máximo permitido.
     */
    public boolean haPerdido(String estado) {
        return getFallos(estado) >= MAX_FALLOS;
    }

    /**
     * Calcula la puntuación final de la partida.
     * <p>Fórmula: {@code puntuacion = letras_correctas - fallos}</p>
     *
     * @param estado cadena de estado con formato {@code "PALABRA;letrasUsadas;fallos"}.
     * @return puntuación calculada (puede ser negativa si hay muchos fallos).
     */
    public int calcularPuntuacion(String estado) {
        String palabra = getPalabra(estado);
        String letrasUsadas = getLetrasUsadas(estado).toUpperCase();
        int aciertos = 0;
        for (char c : palabra.toCharArray()) {
            if (letrasUsadas.contains(String.valueOf(c))) aciertos++;
        }
        return aciertos - getFallos(estado);
    }

    /**
     * Crea una nueva {@link Partida} de Ahorcado inicializada con una palabra aleatoria.
     *
     * @param jugadores array de jugadores que participarán en la partida.
     * @return nueva partida de Ahorcado lista para jugar.
     */
    @Override
    public Partida iniciarPartida(Jugador[] jugadores) {
        Partida p = new Partida(0, this, jugadores);
        p.setEstadoGuardado(crearEstadoInicial());
        return p;
    }
}
