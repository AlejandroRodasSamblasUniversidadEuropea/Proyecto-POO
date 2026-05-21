package control;

import javax.swing.JOptionPane;

import modelo.Ahorcado;
import modelo.Jugador;
import modelo.Partida;
import modelo.Pasapalabra;
import modelo.SistemaJuegos;
import modelo.Usuario;
import vista.VentanaAhorcado;
import vista.VentanaPasapalabra;
import vista.VentanaPartida;

/**
 * Controla el ciclo de vida de una partida: inicio, pausa y finalización.
 * <p>
 * Se encarga de crear la {@link Partida} con el estado inicial correcto,
 * abrir la ventana de juego adecuada según el tipo de juego, y actualizar
 * el estado en el sistema de ficheros al pausar o finalizar.
 * </p>
 */
public class ControladorJuego {

    /** Controlador principal que proporciona acceso a la ventana y al usuario activo. */
    private ControladorPrincipal ctrlMain;

    /** Sistema central con las listas de juegos y partidas. */
    private SistemaJuegos sj;

    /** Controlador de ficheros para persistir los cambios de estado. */
    private ControladorFicheros ctrlFiles;

    /**
     * Construye el controlador de juego a partir del controlador principal.
     *
     * @param ctrlMain controlador principal de la aplicación.
     */
    public ControladorJuego(ControladorPrincipal ctrlMain) {
        this.ctrlMain  = ctrlMain;
        this.sj        = ctrlMain.sj;
        this.ctrlFiles = ctrlMain.ctrlFiles;
    }

    /**
     * Inicia una nueva partida del juego indicado para el usuario activo.
     * <p>
     * Solo los jugadores (no los administradores) pueden iniciar partidas.
     * La partida se registra en el sistema y se persiste en disco antes de abrir la ventana.
     * </p>
     *
     * @param nombreJuego nombre del juego a iniciar (e.g. "Ahorcado", "Pasapalabra").
     */
    public void iniciarNuevaPartida(String nombreJuego) {
        if (ctrlMain.usuarioActivo == null) {
            JOptionPane.showMessageDialog(ctrlMain.ventanaPrincipal,
                "Debes iniciar sesion primero.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!(ctrlMain.usuarioActivo instanceof Jugador)) {
            JOptionPane.showMessageDialog(ctrlMain.ventanaPrincipal,
                "El administrador no puede jugar partidas.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Jugador jugadorPrincipal = (Jugador) ctrlMain.usuarioActivo;
        Jugador[] jugadores      = new Jugador[]{ jugadorPrincipal };

        modelo.Juego juego = sj.buscarJuego(nombreJuego);
        if (juego == null) {
            JOptionPane.showMessageDialog(ctrlMain.ventanaPrincipal,
                "Juego no encontrado.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int nuevoId = sj.generarIdPartida();
        Partida partida = new Partida(nuevoId, juego, jugadores);
        partida.setEstadoGuardado(
            juego instanceof Pasapalabra
                ? ((Pasapalabra) juego).crearEstadoInicial()
                : ((Ahorcado) juego).crearEstadoInicial()
        );

        ctrlFiles.addPartida(partida);
        abrirVentanaJuego(partida);
    }

    /**
     * Reanuda una partida previamente guardada abriendo su ventana de juego.
     * Si la partida ya está finalizada, muestra un aviso y no hace nada.
     *
     * @param partida partida que se desea continuar.
     */
    public void continuarPartida(Partida partida) {
        if (partida.isFinalizada()) {
            JOptionPane.showMessageDialog(ctrlMain.ventanaPrincipal,
                "Esa partida ya esta terminada.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        abrirVentanaJuego(partida);
    }

    /**
     * Determina el tipo de juego de la partida y abre la ventana correspondiente
     * ({@link VentanaAhorcado} o {@link VentanaPasapalabra}).
     * La ventana se añade al desktop de la ventana principal y se pone en primer plano.
     *
     * @param partida partida cuya ventana se debe abrir.
     */
    private void abrirVentanaJuego(Partida partida) {
        VentanaPartida ventana = null;
        String nombreJuego = partida.getJuego().getNombre();

        if (nombreJuego.equals("Pasapalabra")) {
            ventana = new VentanaPasapalabra(partida, ctrlMain);
        } else if (nombreJuego.equals("Ahorcado")) {
            ventana = new VentanaAhorcado(partida, ctrlMain);
        } else {
            JOptionPane.showMessageDialog(ctrlMain.ventanaPrincipal,
                "Juego no soportado: " + nombreJuego);
            return;
        }

        ctrlMain.ventanaPrincipal.getContentPane().add(ventana);
        ventana.setVisible(true);
        try { ventana.setSelected(true); } catch (Exception ex) { /* ignorar */ }
    }

    /**
     * Guarda el estado actual de la partida en disco sin marcarla como finalizada.
     * Muestra un diálogo de confirmación al jugador.
     *
     * @param partida partida que se desea pausar y guardar.
     */
    public void pausarPartida(Partida partida) {
        ctrlFiles.actualizarPartida(partida);
        JOptionPane.showMessageDialog(ctrlMain.ventanaPrincipal,
            "Partida guardada. Puedes continuarla mas tarde.", "Guardado", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Marca la partida como finalizada y guarda el estado definitivo en disco.
     * Debe llamarse cuando el juego concluye (victoria, derrota o rosco completo).
     *
     * @param partida partida que se desea finalizar.
     */
    public void finalizarPartida(Partida partida) {
        partida.finalizarPartida();
        ctrlFiles.actualizarPartida(partida);
    }
}
