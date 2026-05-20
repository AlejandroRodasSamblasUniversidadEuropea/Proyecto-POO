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
 * Gestiona la lectura y escritura de datos en ficheros .txt
 *
 * Ficheros usados:
 *   - data/usuarios.txt  → un usuario por linea: username;password;tipo;
 *   - data/partidas.txt  → una partida por linea (ver Partida.toStringEnFichero())
 *   - data/preguntas.txt → una pregunta por linea: letra;enunciado;respuesta
 */
public class ControladorFicheros {

    public final String rutaUsuarios  = "data" + File.separator + "usuarios.txt";
    public final String rutaPartidas  = "data" + File.separator + "partidas.txt";
    public final String rutaPreguntas = "data" + File.separator + "preguntas.txt";

    public SistemaJuegos sj;

    public ControladorFicheros(SistemaJuegos sj) {
        this.sj = sj;
        // Crea la carpeta data si no existe
        new File("data").mkdirs();
    }

    // ============================================================
    // USUARIOS
    // ============================================================

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
                sc.nextLine(); // consume el salto de linea

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

    public void addUsuario(Usuario usuario) {
        sj.usuarios.add(usuario);
        guardarUsuariosEnFichero();
    }

    public Usuario buscarUsuario(String username) {
        for (Usuario u : sj.usuarios) {
            if (u.getUsername().equals(username)) return u;
        }
        return null;
    }

    // ============================================================
    // PREGUNTAS DE PASAPALABRA
    // ============================================================

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
                    char letra = partes[0].trim().charAt(0);
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

    // Preguntas de ejemplo por si no existe el fichero
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

    public void cargarPartidasDesdeFichero() {
        File fichero = new File(rutaPartidas);
        if (!fichero.exists()) return;

        try {
            Scanner sc = new Scanner(new FileReader(fichero));
            while (sc.hasNextLine()) {
                String linea = sc.nextLine().trim();
                if (linea.isEmpty()) continue;

                // Separamos estado guardado del resto usando el separador |
                String[] partePrincipalYEstado = linea.split("\\|", 2);
                String partePrincipal = partePrincipalYEstado[0];
                String estadoGuardado = partePrincipalYEstado.length > 1 ? partePrincipalYEstado[1] : "";

                // Formato: id;nombreJuego;fecha;finalizada;turno;user1,pts1;user2,pts2;...
                String[] campos = partePrincipal.split(";");
                if (campos.length < 6) continue;

                int id              = Integer.parseInt(campos[0].trim());
                String nombreJuego  = campos[1].trim();
                LocalDate fecha     = LocalDate.parse(campos[2].trim());
                boolean finalizada  = campos[3].trim().equals("1");
                int turno           = Integer.parseInt(campos[4].trim());

                // Jugadores y puntuaciones: desde el campo 5 en adelante
                ArrayList<Jugador> jugadoresLista = new ArrayList<>();
                ArrayList<Integer> puntuacionesLista = new ArrayList<>();

                for (int i = 5; i < campos.length; i++) {
                    String[] parJugador = campos[i].split(",");
                    if (parJugador.length == 2) {
                        String username = parJugador[0].trim();
                        int puntos = Integer.parseInt(parJugador[1].trim());
                        Usuario u = buscarUsuario(username);
                        if (u instanceof Jugador) {
                            jugadoresLista.add((Jugador) u);
                            puntuacionesLista.add(puntos);
                        }
                    }
                }

                if (jugadoresLista.isEmpty()) continue;

                Jugador[] jugadores = jugadoresLista.toArray(new Jugador[0]);
                int[] puntuaciones = new int[puntuacionesLista.size()];
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

    // Añade una partida nueva a la lista y la guarda en disco
    public void addPartida(Partida partida) {
        sj.partidas.add(partida);
        guardarPartidasEnFichero();
    }

    // Actualiza una partida existente (por id) y guarda en disco
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
