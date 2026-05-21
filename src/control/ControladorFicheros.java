package control;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

import modelo.Admin;
import modelo.Jugador;
import modelo.Partida;
import modelo.Pregunta;
import modelo.SistemaJuegos;
import modelo.Usuario;
import modelo.Juego;

/**
 * Gestiona la lectura y escritura de datos persistentes en ficheros de texto.
 * <p>
 * Todos los ficheros se almacenan en la carpeta {@code data/}, que se crea
 * automáticamente si no existe al arrancar la aplicación.
 * </p>
 *
 * <h3>Ficheros gestionados</h3>
 * <ul>
 *   <li>{@code data/usuarios.txt} — un usuario por línea: {@code username;password;tipo;}</li>
 *   <li>{@code data/preguntas.txt} — una pregunta por línea: {@code letra;enunciado;respuesta}</li>
 *   <li>{@code data/partidas.txt} — una partida por línea (ver {@link Partida#toStringEnFichero()})</li>
 * </ul>
 */
public class ControladorFicheros {

    /** Ruta al fichero de usuarios. */
    public final String rutaUsuarios  = "data" + File.separator + "usuarios.txt";

    /** Ruta al fichero de partidas. */
    public final String rutaPartidas  = "data" + File.separator + "partidas.txt";

    /** Ruta al fichero de preguntas de Pasapalabra. */
    public final String rutaPreguntas = "data" + File.separator + "preguntas.txt";

    /** Sistema central al que se añaden los datos cargados. */
    public SistemaJuegos sj;

    /**
     * Construye el controlador de ficheros y crea la carpeta {@code data/} si no existe.
     *
     * @param sj sistema central de juegos.
     */
    public ControladorFicheros(SistemaJuegos sj) {
        this.sj = sj;
        new File("data").mkdirs();
    }

    // ============================================================
    // USUARIOS
    // ============================================================

    /**
     * Carga todos los usuarios desde {@code data/usuarios.txt} y los añade al sistema.
     * Si el fichero no existe, no hace nada.
     * <p>Formato de cada línea: {@code username;password;tipo;}</p>
     * <p>El tipo puede ser {@code admin} o {@code player}.</p>
     */
    public void cargarUsuariosDesdeFichero() {
        File fichero = new File(rutaUsuarios);
        if (!fichero.exists()) return;

        Scanner sc = null;
        try {
            sc = new Scanner(new FileReader(fichero));
            sc.useDelimiter(";");
            while (sc.hasNextLine()) {
                String username = sc.next().trim();
                String passwd   = sc.next().trim();
                String tipo     = sc.next().trim();
                sc.nextLine();

                if (tipo.equals("admin")) {
                    sj.usuarios.add(new Admin(username, passwd));
                } else {
                    sj.usuarios.add(new Jugador(username, passwd));
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("No se encontro el fichero de usuarios: " + rutaUsuarios);
        } finally {
            if (sc != null) sc.close();
        }
    }

    /**
     * Sobreescribe {@code data/usuarios.txt} con el estado actual de la lista de usuarios.
     * Llama a {@link Usuario#toStringInUserFile()} para serializar cada usuario.
     */
    public void guardarUsuariosEnFichero() {
        try {
            PrintWriter pw = new PrintWriter(new File(rutaUsuarios));
            for (Usuario u : sj.usuarios) {
                pw.println(u.toStringInUserFile());
            }
            pw.close();
        } catch (FileNotFoundException e) {
            System.out.println("Error al guardar usuarios: " + e.getMessage());
        }
    }

    /**
     * Añade un usuario a la lista del sistema y lo persiste en disco.
     *
     * @param usuario nuevo usuario a registrar.
     */
    public void addUsuario(Usuario usuario) {
        sj.usuarios.add(usuario);
        guardarUsuariosEnFichero();
    }

    /**
     * Busca un usuario por su nombre de usuario en la lista del sistema.
     *
     * @param username nombre de usuario a buscar.
     * @return el {@link Usuario} encontrado, o {@code null} si no existe.
     */
    public Usuario buscarUsuario(String username) {
        for (Usuario u : sj.usuarios) {
            if (u.getUsername().equals(username)) return u;
        }
        return null;
    }

    // ============================================================
    // PREGUNTAS DE PASAPALABRA
    // ============================================================

    /**
     * Carga las preguntas de Pasapalabra desde {@code data/preguntas.txt}.
     * Si el fichero no existe, genera preguntas de ejemplo y las guarda en disco.
     * <p>Formato de cada línea: {@code letra;enunciado;respuesta}</p>
     *
     * @return lista de {@link Pregunta} lista para usar en el juego.
     */
    public ArrayList<Pregunta> cargarPreguntasDesdeFichero() {
        ArrayList<Pregunta> lista = new ArrayList<>();
        File fichero = new File(rutaPreguntas);

        if (!fichero.exists()) {
            System.out.println("No existe preguntas.txt. Se usaran preguntas de ejemplo.");
            lista = crearPreguntasEjemplo();
            guardarPreguntasEnFichero(lista);
            return lista;
        }

        try {
            Scanner sc = new Scanner(new FileReader(fichero));
            while (sc.hasNextLine()) {
                String linea = sc.nextLine().trim();
                if (linea.isEmpty()) continue;
                String[] partes = linea.split(";", 3);
                if (partes.length == 3) {
                    char letra      = partes[0].trim().charAt(0);
                    String enunciado = partes[1].trim();
                    String respuesta = partes[2].trim();
                    lista.add(new Pregunta(letra, enunciado, respuesta));
                }
            }
            sc.close();
        } catch (FileNotFoundException e) {
            System.out.println("Error al leer preguntas: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Sobreescribe {@code data/preguntas.txt} con la lista de preguntas indicada.
     *
     * @param preguntas lista de preguntas a guardar.
     */
    private void guardarPreguntasEnFichero(ArrayList<Pregunta> preguntas) {
        try {
            PrintWriter pw = new PrintWriter(new File(rutaPreguntas));
            for (Pregunta p : preguntas) {
                pw.println(p.toStringEnFichero());
            }
            pw.close();
        } catch (FileNotFoundException e) {
            System.out.println("Error al guardar preguntas: " + e.getMessage());
        }
    }

    /**
     * Genera un conjunto de preguntas de ejemplo sobre programación orientada a objetos.
     * Se utiliza cuando {@code data/preguntas.txt} no existe.
     *
     * @return lista con preguntas predefinidas.
     */
    private ArrayList<Pregunta> crearPreguntasEjemplo() {
        ArrayList<Pregunta> lista = new ArrayList<>();
        lista.add(new Pregunta('A', "Con esta letra: Lenguaje de programacion de Apple para iOS", "SWIFT"));
        lista.add(new Pregunta('B', "Con esta letra: Instruccion que repite un bloque de codigo", "BUCLE"));
        lista.add(new Pregunta('C', "Con esta letra: Molde del que se crean los objetos en POO", "CLASE"));
        lista.add(new Pregunta('D', "Con esta letra: Estructura de datos con clave y valor", "DICCIONARIO"));
        lista.add(new Pregunta('E', "Con esta letra: Tipo de herencia donde una clase extiende otra", "EXTENSION"));
        lista.add(new Pregunta('F', "Con esta letra: Bloque de codigo reutilizable con nombre propio", "FUNCION"));
        lista.add(new Pregunta('H', "Con esta letra: Relacion entre clases en POO (padre-hijo)", "HERENCIA"));
        lista.add(new Pregunta('I', "Con esta letra: Contrato que una clase se compromete a cumplir en Java", "INTERFAZ"));
        lista.add(new Pregunta('J', "Con esta letra: Lenguaje de programacion orientado a objetos de Oracle", "JAVA"));
        lista.add(new Pregunta('M', "Con esta letra: Funcion dentro de una clase en POO", "METODO"));
        lista.add(new Pregunta('O', "Con esta letra: Instancia de una clase en POO", "OBJETO"));
        lista.add(new Pregunta('P', "Con esta letra: Capacidad de un objeto de tomar muchas formas", "POLIMORFISMO"));
        lista.add(new Pregunta('R', "Con esta letra: Tecnica en que una funcion se llama a si misma", "RECURSION"));
        lista.add(new Pregunta('V', "Con esta letra: Espacio de memoria con nombre para guardar datos", "VARIABLE"));
        return lista;
    }

    // ============================================================
    // PARTIDAS
    // ============================================================

    /**
     * Carga todas las partidas desde {@code data/partidas.txt} y las añade al sistema.
     * Si el fichero no existe, no hace nada.
     * <p>
     * Cada línea se divide en dos partes usando el separador {@code |}: los campos
     * principales (id, juego, fecha, etc.) y el estado guardado del juego.
     * </p>
     */
    public void cargarPartidasDesdeFichero() {
        File fichero = new File(rutaPartidas);
        if (!fichero.exists()) return;

        try {
            Scanner sc = new Scanner(new FileReader(fichero));
            while (sc.hasNextLine()) {
                String linea = sc.nextLine().trim();
                if (linea.isEmpty()) continue;

                String[] partePrincipalYEstado = linea.split("\\|", 2);
                String partePrincipal  = partePrincipalYEstado[0];
                String estadoGuardado  = partePrincipalYEstado.length > 1 ? partePrincipalYEstado[1] : "";

                String[] campos = partePrincipal.split(";");
                if (campos.length < 6) continue;

                int id             = Integer.parseInt(campos[0].trim());
                String nombreJuego = campos[1].trim();
                LocalDate fecha    = LocalDate.parse(campos[2].trim());
                boolean finalizada = campos[3].trim().equals("1");
                int turno          = Integer.parseInt(campos[4].trim());

                ArrayList<Jugador> jugadoresLista     = new ArrayList<>();
                ArrayList<Integer> puntuacionesLista  = new ArrayList<>();

                for (int i = 5; i < campos.length; i++) {
                    String[] parJugador = campos[i].split(",");
                    if (parJugador.length == 2) {
                        String username = parJugador[0].trim();
                        int puntos      = Integer.parseInt(parJugador[1].trim());
                        Usuario u = buscarUsuario(username);
                        if (u instanceof Jugador) {
                            jugadoresLista.add((Jugador) u);
                            puntuacionesLista.add(puntos);
                        }
                    }
                }

                if (jugadoresLista.isEmpty()) continue;

                Jugador[] jugadores  = jugadoresLista.toArray(new Jugador[0]);
                int[] puntuaciones   = new int[puntuacionesLista.size()];
                for (int i = 0; i < puntuacionesLista.size(); i++) {
                    puntuaciones[i] = puntuacionesLista.get(i);
                }

                Juego juego = sj.buscarJuego(nombreJuego);
                if (juego == null) continue;

                Partida partida = new Partida(id, juego, jugadores, puntuaciones,
                                              finalizada, fecha, turno, estadoGuardado);
                sj.partidas.add(partida);
            }
            sc.close();
        } catch (Exception e) {
            System.out.println("Error al cargar partidas: " + e.getMessage());
        }
    }

    /**
     * Sobreescribe {@code data/partidas.txt} con el estado actual de todas las partidas.
     */
    public void guardarPartidasEnFichero() {
        try {
            PrintWriter pw = new PrintWriter(new File(rutaPartidas));
            for (Partida p : sj.partidas) {
                pw.println(p.toStringEnFichero());
            }
            pw.close();
        } catch (FileNotFoundException e) {
            System.out.println("Error al guardar partidas: " + e.getMessage());
        }
    }

    /**
     * Añade una nueva partida a la lista del sistema y la persiste en disco.
     *
     * @param partida nueva partida a registrar.
     */
    public void addPartida(Partida partida) {
        sj.partidas.add(partida);
        guardarPartidasEnFichero();
    }

    /**
     * Actualiza una partida existente (identificada por su id) en la lista del sistema
     * y guarda los cambios en disco. Si no se encuentra la partida, no hace nada.
     *
     * @param partida partida con los datos actualizados.
     */
    public void actualizarPartida(Partida partida) {
        for (int i = 0; i < sj.partidas.size(); i++) {
            if (sj.partidas.get(i).getId() == partida.getId()) {
                sj.partidas.set(i, partida);
                break;
            }
        }
        guardarPartidasEnFichero();
    }
}
