package modelo;

import java.util.ArrayList;
import control.ControladorFicheros;
import control.ControladorPrincipal;
import vista.VentanaPrincipal;

/**
 * Clase central del sistema de juegos.
 * <p>
 * Actúa como repositorio principal: mantiene las listas de usuarios registrados,
 * partidas (en curso y finalizadas) y juegos disponibles. También contiene el
 * método {@link #main(String[])} que arranca la aplicación.
 * </p>
 *
 * <h3>Secuencia de arranque</h3>
 * <ol>
 *   <li>Crear instancia de {@code SistemaJuegos}.</li>
 *   <li>Cargar usuarios desde {@code data/usuarios.txt}.</li>
 *   <li>Cargar preguntas de Pasapalabra desde {@code data/preguntas.txt}.</li>
 *   <li>Registrar los juegos disponibles.</li>
 *   <li>Cargar partidas guardadas desde {@code data/partidas.txt}.</li>
 *   <li>Crear el {@link ControladorPrincipal} y mostrar la {@link VentanaPrincipal}.</li>
 * </ol>
 */
public class SistemaJuegos {

    /** Lista de todos los usuarios registrados en el sistema. */
    public ArrayList<Usuario> usuarios;

    /** Lista de todas las partidas, tanto en curso como finalizadas. */
    public ArrayList<Partida> partidas;

    /** Lista de juegos disponibles en la plataforma. */
    public ArrayList<Juego> juegos;

    /**
     * Construye el sistema de juegos con listas vacías.
     */
    public SistemaJuegos() {
        usuarios = new ArrayList<>();
        partidas = new ArrayList<>();
        juegos   = new ArrayList<>();
    }

    /**
     * Registra los juegos disponibles en la plataforma.
     * Debe llamarse desde el {@link #main(String[])} antes de mostrar la interfaz.
     *
     * @param preguntasPasapalabra lista de preguntas cargadas para el juego Pasapalabra.
     */
    public void registrarJuegos(ArrayList<Pregunta> preguntasPasapalabra) {
        juegos.add(new Pasapalabra(preguntasPasapalabra));
        juegos.add(new Ahorcado());
    }

    /**
     * Busca un juego registrado por su nombre (sin distinción de mayúsculas).
     *
     * @param nombre nombre del juego a buscar (e.g. "Ahorcado", "Pasapalabra").
     * @return la instancia de {@link Juego} si existe, o {@code null} si no se encuentra.
     */
    public Juego buscarJuego(String nombre) {
        for (Juego j : juegos) {
            if (j.getNombre().equalsIgnoreCase(nombre)) return j;
        }
        return null;
    }

    /**
     * Genera el siguiente identificador disponible para una nueva partida.
     * El id es el máximo id existente más uno. Si no hay partidas, devuelve 1.
     *
     * @return nuevo id único para la próxima partida.
     */
    public int generarIdPartida() {
        int maxId = 0;
        for (Partida p : partidas) {
            if (p.getId() > maxId) maxId = p.getId();
        }
        return maxId + 1;
    }

    /**
     * Devuelve las partidas no finalizadas en las que participa un jugador.
     * Se utiliza para mostrar al jugador sus partidas guardadas disponibles.
     *
     * @param username nombre de usuario del jugador.
     * @return lista de partidas en curso del jugador.
     */
    public ArrayList<Partida> getPartidasEnCurso(String username) {
        ArrayList<Partida> resultado = new ArrayList<>();
        for (Partida p : partidas) {
            if (!p.isFinalizada() && p.participaJugador(username)) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    /**
     * Devuelve todas las partidas (en curso y finalizadas) en las que ha participado un jugador.
     * Se utiliza para mostrar las estadísticas personales del jugador.
     *
     * @param username nombre de usuario del jugador.
     * @return lista de todas las partidas del jugador.
     */
    public ArrayList<Partida> getPartidasDeJugador(String username) {
        ArrayList<Partida> resultado = new ArrayList<>();
        for (Partida p : partidas) {
            if (p.participaJugador(username)) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    /**
     * Devuelve todas las partidas finalizadas de un juego concreto.
     * Se utiliza para generar el ranking de un juego en el panel de administración.
     *
     * @param nombreJuego nombre del juego (e.g. "Ahorcado", "Pasapalabra").
     * @return lista de partidas finalizadas del juego indicado.
     */
    public ArrayList<Partida> getPartidasDeJuego(String nombreJuego) {
        ArrayList<Partida> resultado = new ArrayList<>();
        for (Partida p : partidas) {
            if (p.isFinalizada() && p.getJuego().getNombre().equalsIgnoreCase(nombreJuego)) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    /**
     * Devuelve una representación textual de todos los usuarios del sistema.
     * Se usa para mostrar la lista de usuarios mediante un diálogo.
     *
     * @return cadena con todos los usuarios, uno por línea.
     */
    public String usuariosToString() {
        String resultado = "USUARIOS:\n";
        for (Usuario u : usuarios) {
            resultado += u + "\n";
        }
        return resultado;
    }

    /**
     * Punto de entrada de la aplicación.
     * <p>
     * Inicializa el sistema, carga los datos desde disco, registra los juegos
     * y muestra la ventana principal.
     * </p>
     *
     * @param args argumentos de línea de comandos (no se utilizan).
     */
    public static void main(String[] args) {
        SistemaJuegos sj = new SistemaJuegos();

        ControladorFicheros cf = new ControladorFicheros(sj);

        cf.cargarUsuariosDesdeFichero();

        ArrayList<Pregunta> preguntas = cf.cargarPreguntasDesdeFichero();

        sj.registrarJuegos(preguntas);

        cf.cargarPartidasDesdeFichero();

        System.out.println("Usuarios cargados: " + sj.usuarios.size());
        System.out.println("Partidas cargadas: " + sj.partidas.size());
        System.out.println("Juegos disponibles: " + sj.juegos.size());

        ControladorPrincipal mainCTRL = new ControladorPrincipal(cf, sj);
        mainCTRL.ventanaPrincipal = new VentanaPrincipal(mainCTRL);
        mainCTRL.ventanaPrincipal.setVisible(true);
    }
}
