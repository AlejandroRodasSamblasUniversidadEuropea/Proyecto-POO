package modelo;

/**
 * Implementacion del juego Ahorcado.
 * El jugador adivina letras de una palabra oculta.
 * Tiene un numero maximo de fallos (por defecto 6).
 * Puntuacion final = letras acertadas - fallos.
 *
 * Estado guardado:
 * "palabra;letrasUsadas;fallosActuales"
 * Ejemplo: "PROGRAMACION;P,A,O,X,Z;2"
 */
public class Ahorcado extends Juego {

    private static final int MAX_FALLOS = 6;

    // Lista de palabras posibles para elegir al azar
    private String[] palabras = {
        "PROGRAMACION", "HERENCIA", "POLIMORFISMO", "CLASE",
        "OBJETO", "METODO", "VARIABLE", "COMPILADOR",
        "INTERFAZ", "BUCLE", "ARRAY", "RECURSION"
    };

    public Ahorcado() {
        super("Ahorcado");
    }

    public int getMaxFallos() {
        return MAX_FALLOS;
    }

    // Elige una palabra aleatoria de la lista
    public String elegirPalabraAleatoria() {
        int indice = (int) (Math.random() * palabras.length);
        return palabras[indice];
    }

    // Construye el estado inicial con una palabra aleatoria
    // Formato: "PALABRA;;0"  (sin letras usadas, sin fallos)
    public String crearEstadoInicial() {
        return elegirPalabraAleatoria() + ";;0";
    }

    // Extrae la palabra del estado guardado
    public String getPalabra(String estado) {
        return estado.split(";")[0];
    }

    // Extrae las letras ya usadas del estado guardado
    public String getLetrasUsadas(String estado) {
        String[] partes = estado.split(";", -1);
        return partes.length > 1 ? partes[1] : "";
    }

    // Extrae el numero de fallos del estado guardado
    public int getFallos(String estado) {
        String[] partes = estado.split(";", -1);
        try {
            return Integer.parseInt(partes[2]);
        } catch (Exception e) {
            return 0;
        }
    }

    // Construye la palabra con guiones para las letras no adivinadas
    // Ej: "P_OG_AM_CION" si solo se han acertado P, O, A
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

    // Devuelve true si la letra ya fue usada
    public boolean letraYaUsada(String estado, char letra) {
        return getLetrasUsadas(estado).toUpperCase().contains(String.valueOf(Character.toUpperCase(letra)));
    }

    // Registra una letra y devuelve el nuevo estado
    public String jugarLetra(String estado, char letra) {
        letra = Character.toUpperCase(letra);
        String palabra = getPalabra(estado);
        String letrasUsadas = getLetrasUsadas(estado);
        int fallos = getFallos(estado);

        // Añadimos la letra a las usadas
        if (!letrasUsadas.isEmpty()) {
            letrasUsadas += "," + letra;
        } else {
            letrasUsadas = String.valueOf(letra);
        }

        // Si la letra no esta en la palabra, suma un fallo
        if (!palabra.contains(String.valueOf(letra))) {
            fallos++;
        }

        return palabra + ";" + letrasUsadas + ";" + fallos;
    }

    // Comprueba si el jugador ha ganado (todas las letras de la palabra adivinadas)
    public boolean haGanado(String estado) {
        String palabra = getPalabra(estado);
        String letrasUsadas = getLetrasUsadas(estado).toUpperCase();
        for (char c : palabra.toCharArray()) {
            if (!letrasUsadas.contains(String.valueOf(c))) return false;
        }
        return true;
    }

    // Comprueba si el jugador ha perdido (demasiados fallos)
    public boolean haPerdido(String estado) {
        return getFallos(estado) >= MAX_FALLOS;
    }

    // Calcula la puntuacion: letras correctas - fallos
    public int calcularPuntuacion(String estado) {
        String palabra = getPalabra(estado);
        String letrasUsadas = getLetrasUsadas(estado).toUpperCase();
        int aciertos = 0;
        for (char c : palabra.toCharArray()) {
            if (letrasUsadas.contains(String.valueOf(c))) aciertos++;
        }
        return aciertos - getFallos(estado);
    }

    @Override
    public Partida iniciarPartida(Jugador[] jugadores) {
        Partida p = new Partida(0, this, jugadores);
        p.setEstadoGuardado(crearEstadoInicial());
        return p;
    }
}
