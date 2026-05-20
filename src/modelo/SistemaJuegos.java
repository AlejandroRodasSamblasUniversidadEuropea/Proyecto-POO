package modelo;

import java.util.ArrayList;
import control.ControladorFicheros;
import control.ControladorPrincipal;
import vista.VentanaPrincipal;

/**
 * Clase central del sistema.
 * Contiene las listas de usuarios, partidas y juegos.
 * Aqui esta el main() que arranca la aplicacion.
 */
public class SistemaJuegos {

    public ArrayList<Usuario> usuarios;
    public ArrayList<Partida> partidas;
    public ArrayList<Juego> juegos;

    public SistemaJuegos() {
        usuarios = new ArrayList<>();
        partidas = new ArrayList<>();
        juegos = new ArrayList<>();
    }

    // Registra los juegos disponibles en la aplicacion
    // Se llama desde el main antes de arrancar la interfaz
    public void registrarJuegos(ArrayList<Pregunta> preguntasPasapalabra) {
        juegos.add(new Pasapalabra(preguntasPasapalabra));
        juegos.add(new Ahorcado());
    }

    // Busca un juego por su nombre
    public Juego buscarJuego(String nombre) {
        for (Juego j : juegos) {
            if (j.getNombre().equalsIgnoreCase(nombre)) return j;
        }
        return null;
    }

    // Genera el siguiente id disponible para una nueva partida
    public int generarIdPartida() {
        int maxId = 0;
        for (Partida p : partidas) {
            if (p.getId() > maxId) maxId = p.getId();
        }
        return maxId + 1;
    }

    // Devuelve las partidas no finalizadas de un jugador
    public ArrayList<Partida> getPartidasEnCurso(String username) {
        ArrayList<Partida> resultado = new ArrayList<>();
        for (Partida p : partidas) {
            if (!p.isFinalizada() && p.participaJugador(username)) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    // Devuelve todas las partidas de un jugador (para estadisticas)
    public ArrayList<Partida> getPartidasDeJugador(String username) {
        ArrayList<Partida> resultado = new ArrayList<>();
        for (Partida p : partidas) {
            if (p.participaJugador(username)) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    // Devuelve todas las partidas finalizadas de un juego concreto (para ranking)
    public ArrayList<Partida> getPartidasDeJuego(String nombreJuego) {
        ArrayList<Partida> resultado = new ArrayList<>();
        for (Partida p : partidas) {
            if (p.isFinalizada() && p.getJuego().getNombre().equalsIgnoreCase(nombreJuego)) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    // Para el dialogo de lista de usuarios
    public String usuariosToString() {
        String resultado = "USUARIOS:\n";
        for (Usuario u : usuarios) {
            resultado += u + "\n";
        }
        return resultado;
    }

    // ---- MAIN: punto de entrada de la aplicacion ----
    public static void main(String[] args) {
        // 1. Crea el sistema central
        SistemaJuegos sj = new SistemaJuegos();

        // 2. Crea el controlador de ficheros
        ControladorFicheros cf = new ControladorFicheros(sj);

        // 3. Carga usuarios desde usuarios.txt
        cf.cargarUsuariosDesdeFichero();

        // 4. Carga preguntas de Pasapalabra desde preguntas.txt
        ArrayList<Pregunta> preguntas = cf.cargarPreguntasDesdeFichero();

        // 5. Registra los juegos disponibles
        sj.registrarJuegos(preguntas);

        // 6. Carga partidas guardadas desde partidas.txt
        cf.cargarPartidasDesdeFichero();

        // 7. Muestra en consola los datos cargados (util para depuracion)
        System.out.println("Usuarios cargados: " + sj.usuarios.size());
        System.out.println("Partidas cargadas: " + sj.partidas.size());
        System.out.println("Juegos disponibles: " + sj.juegos.size());

        // 8. Crea el controlador principal y la ventana
        ControladorPrincipal mainCTRL = new ControladorPrincipal(cf, sj);
        mainCTRL.ventanaPrincipal = new VentanaPrincipal(mainCTRL);
        mainCTRL.ventanaPrincipal.setVisible(true);
    }
}
