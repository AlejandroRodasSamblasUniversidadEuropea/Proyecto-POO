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
 * Controla el inicio, pausa y reanudacion de partidas.
 * Crea la ventana de juego correcta segun el juego elegido.
 */
public class ControladorJuego {

    private ControladorPrincipal ctrlMain;
    private SistemaJuegos sj;
    private ControladorFicheros ctrlFiles;

    public ControladorJuego(ControladorPrincipal ctrlMain) {
        this.ctrlMain = ctrlMain;
        this.sj = ctrlMain.sj;
        this.ctrlFiles = ctrlMain.ctrlFiles;
    }

    /**
     * Inicia una nueva partida del juego indicado.
     * Si el juego es multijugador, pide el login del segundo jugador.
     */
    public void iniciarNuevaPartida(String nombreJuego) {
        if (ctrlMain.usuarioActivo == null) {
            JOptionPane.showMessageDialog(ctrlMain.ventanaPrincipal,
                "Debes iniciar sesion primero.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Solo los jugadores (no admin) pueden jugar
        if (!(ctrlMain.usuarioActivo instanceof Jugador)) {
            JOptionPane.showMessageDialog(ctrlMain.ventanaPrincipal,
                "El administrador no puede jugar partidas.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Jugador jugadorPrincipal = (Jugador) ctrlMain.usuarioActivo;
        Jugador[] jugadores = new Jugador[]{ jugadorPrincipal };

        // Asignamos id y creamos la partida
        modelo.Juego juego = sj.buscarJuego(nombreJuego);
        if (juego == null) {
            JOptionPane.showMessageDialog(ctrlMain.ventanaPrincipal,
                "Juego no encontrado.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Partida partida = juego.iniciarPartida(jugadores);
        int nuevoId = sj.generarIdPartida();
        // Como Partida no tiene setId, creamos una nueva con el id correcto
        partida = new Partida(nuevoId, juego, jugadores);
        partida.setEstadoGuardado(
            juego instanceof Pasapalabra
                ? ((Pasapalabra) juego).crearEstadoInicial()
                : ((Ahorcado) juego).crearEstadoInicial()
        );

        ctrlFiles.addPartida(partida);
        abrirVentanaJuego(partida);
    }

    /**
     * Reanuda una partida guardada.
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
     * Abre la ventana del juego correspondiente a la partida.
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
     * Guarda el estado actual de una partida y la cierra.
     */
    public void pausarPartida(Partida partida) {
        ctrlFiles.actualizarPartida(partida);
        JOptionPane.showMessageDialog(ctrlMain.ventanaPrincipal,
            "Partida guardada. Puedes continuarla mas tarde.", "Guardado", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Finaliza la partida, guarda puntuacion y cierra la ventana.
     */
    public void finalizarPartida(Partida partida) {
        partida.finalizarPartida();
        ctrlFiles.actualizarPartida(partida);
    }
}
