package vista;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import control.ControladorPrincipal;
import modelo.Ahorcado;
import modelo.Partida;

/**
 * Ventana interna del juego del Ahorcado.
 * <p>
 * Muestra la palabra oculta con guiones bajos, el dibujo ASCII del ahorcado
 * que crece con cada fallo, las letras ya usadas y el campo para introducir
 * la siguiente letra.
 * </p>
 *
 * <h3>Interacción con el usuario</h3>
 * <ul>
 *   <li>El jugador escribe una letra en el campo de texto y pulsa "Jugar letra" o Enter.</li>
 *   <li>Si gana o pierde, se muestra un diálogo y la ventana se cierra.</li>
 *   <li>"Guardar y pausar" guarda el estado actual sin finalizar la partida.</li>
 * </ul>
 *
 * <p>El estado del juego se persiste en disco tras cada jugada a través de
 * {@link control.ControladorFicheros#actualizarPartida(Partida)}.</p>
 */
public class VentanaAhorcado extends VentanaPartida {

    /** Partida asociada a esta ventana. */
    private Partida partida;

    /** Instancia del juego Ahorcado con la lógica de negocio. */
    private Ahorcado juego;

    /** Controlador principal para persistir y finalizar la partida. */
    private ControladorPrincipal cp;

    /**
     * Estado actual del juego serializado como {@code "PALABRA;letrasUsadas;fallos"}.
     * Se actualiza tras cada jugada.
     */
    private String estado;

    private JLabel labelPalabra;
    private JLabel labelFallos;
    private JLabel labelLetrasUsadas;
    private JTextField campoLetra;
    private JButton botonJugar;
    private JButton botonPausar;
    private DibujoAhorcado panelDibujo;

    /**
     * Construye la ventana del Ahorcado y la inicializa con el estado guardado de la partida.
     *
     * @param partida partida (nueva o continuada) cuyo estado se mostrará.
     * @param cp      controlador principal para persistir cambios y finalizar la partida.
     */
    public VentanaAhorcado(Partida partida, ControladorPrincipal cp) {
        super("Ahorcado");
        this.partida = partida;
        this.juego   = (Ahorcado) partida.getJuego();
        this.cp      = cp;
        this.estado  = partida.getEstadoGuardado();
        crearVista();
        actualizarVista();
    }

    /**
     * Construye y añade todos los componentes visuales de la ventana:
     * panel superior con la palabra y el contador de fallos, panel central
     * con el dibujo del ahorcado y panel inferior con el campo de letra y botones.
     */
    @Override
    public void crearVista() {
        setSize(600, 520);
        setLocation(60, 40);
        setLayout(new BorderLayout(10, 10));

        JPanel panelSuperior = new JPanel(new GridLayout(3, 1));
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        labelPalabra = new JLabel("", JLabel.CENTER);
        labelPalabra.setFont(new Font("Courier New", Font.BOLD, 24));

        labelFallos = new JLabel("", JLabel.CENTER);
        labelFallos.setFont(new Font("Arial", Font.PLAIN, 14));

        labelLetrasUsadas = new JLabel("", JLabel.CENTER);
        labelLetrasUsadas.setFont(new Font("Arial", Font.ITALIC, 12));

        panelSuperior.add(labelPalabra);
        panelSuperior.add(labelFallos);
        panelSuperior.add(labelLetrasUsadas);

        panelDibujo = new DibujoAhorcado();
        panelDibujo.setPreferredSize(new Dimension(200, 220));

        JPanel panelInferior = new JPanel(new FlowLayout());
        panelInferior.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        campoLetra = new JTextField(3);
        campoLetra.setFont(new Font("Arial", Font.BOLD, 18));

        botonJugar  = new JButton("Jugar letra");
        botonPausar = new JButton("Guardar y pausar");

        botonJugar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { jugarLetra(); }
        });
        botonPausar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { pausar(); }
        });
        campoLetra.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { jugarLetra(); }
        });

        panelInferior.add(new JLabel("Escribe una letra:"));
        panelInferior.add(campoLetra);
        panelInferior.add(botonJugar);
        panelInferior.add(botonPausar);

        add(panelSuperior, BorderLayout.NORTH);
        add(panelDibujo,   BorderLayout.CENTER);
        add(panelInferior, BorderLayout.SOUTH);
    }

    /**
     * Refresca todos los componentes visuales con el estado actual del juego:
     * la palabra mostrada, el contador de fallos, las letras usadas y el dibujo.
     */
    private void actualizarVista() {
        labelPalabra.setText(juego.getPalabraMostrada(estado));
        int fallos = juego.getFallos(estado);
        labelFallos.setText("Fallos: " + fallos + " / " + juego.getMaxFallos()
            + "  |  Jugador: " + partida.getJugadores()[0].getUsername());
        String usadas = juego.getLetrasUsadas(estado).replace(",", "  ");
        labelLetrasUsadas.setText("Letras usadas: " + (usadas.isEmpty() ? "(ninguna)" : usadas));
        panelDibujo.setFallos(fallos);
        panelDibujo.repaint();
        campoLetra.setText("");
        campoLetra.requestFocus();
    }

    /**
     * Procesa la letra introducida por el jugador.
     * Valida que sea una única letra, comprueba si ya fue usada,
     * actualiza el estado del juego y detecta si la partida ha terminado.
     * Persiste el estado en disco tras cada jugada.
     */
    private void jugarLetra() {
        String texto = campoLetra.getText().trim().toUpperCase();
        if (texto.isEmpty() || texto.length() != 1 || !Character.isLetter(texto.charAt(0))) {
            JOptionPane.showMessageDialog(this, "Escribe solo UNA letra.");
            return;
        }
        char letra = texto.charAt(0);

        if (juego.letraYaUsada(estado, letra)) {
            JOptionPane.showMessageDialog(this, "Esa letra ya la usaste.");
            return;
        }

        estado = juego.jugarLetra(estado, letra);

        partida.setPuntuacion(0, juego.calcularPuntuacion(estado));
        partida.setEstadoGuardado(estado);
        cp.ctrlFiles.actualizarPartida(partida);

        actualizarVista();

        if (juego.haGanado(estado)) {
            cp.ctrlJuego.finalizarPartida(partida);
            JOptionPane.showMessageDialog(this,
                "¡Has ganado! La palabra era: " + juego.getPalabra(estado)
                + "\nPuntuacion: " + juego.calcularPuntuacion(estado),
                "Ahorcado", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else if (juego.haPerdido(estado)) {
            cp.ctrlJuego.finalizarPartida(partida);
            JOptionPane.showMessageDialog(this,
                "Has perdido. La palabra era: " + juego.getPalabra(estado),
                "Ahorcado", JOptionPane.ERROR_MESSAGE);
            dispose();
        }
    }

    /**
     * Guarda el estado actual de la partida y cierra la ventana sin finalizarla.
     * El jugador podrá reanudarla más tarde desde "Continuar partida guardada".
     */
    private void pausar() {
        partida.setEstadoGuardado(estado);
        cp.ctrlJuego.pausarPartida(partida);
        dispose();
    }

    // ---- Clase interna: dibuja el ahorcado según el número de fallos ----

    /**
     * Panel que dibuja el ahorcado (patíbulo y partes del cuerpo) de forma progresiva
     * según el número de fallos acumulados.
     * <p>
     * El dibujo usa {@link Graphics2D} con trazos de 3 píxeles de grosor.
     * Se añade una parte del cuerpo por cada fallo (hasta 6):
     * cabeza, cuerpo, brazo izquierdo, brazo derecho, pierna izquierda, pierna derecha.
     * </p>
     */
    private static class DibujoAhorcado extends JPanel {

        /** Número de fallos actuales; determina qué partes del cuerpo se dibujan. */
        private int fallos = 0;

        /**
         * Actualiza el número de fallos para refrescar el dibujo.
         *
         * @param fallos número de fallos actuales (0-6).
         */
        public void setFallos(int fallos) { this.fallos = fallos; }

        /**
         * Dibuja el patíbulo y las partes del cuerpo del ahorcado según los fallos actuales.
         *
         * @param g contexto gráfico proporcionado por Swing.
         */
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(Color.BLACK);
            Graphics2D g2 = (Graphics2D) g;
            g2.setStroke(new BasicStroke(3));

            int ox = 30, oy = getHeight() - 20;

            g.drawLine(ox, oy, ox + 120, oy);
            g.drawLine(ox + 50, oy, ox + 50, oy - 180);
            g.drawLine(ox + 50, oy - 180, ox + 130, oy - 180);
            g.drawLine(ox + 130, oy - 180, ox + 130, oy - 150);

            int cx = ox + 130, cy = oy - 150;

            if (fallos >= 1) g.drawOval(cx - 15, cy, 30, 30);
            if (fallos >= 2) g.drawLine(cx, cy + 30, cx, cy + 80);
            if (fallos >= 3) g.drawLine(cx, cy + 45, cx - 20, cy + 65);
            if (fallos >= 4) g.drawLine(cx, cy + 45, cx + 20, cy + 65);
            if (fallos >= 5) g.drawLine(cx, cy + 80, cx - 20, cy + 110);
            if (fallos >= 6) g.drawLine(cx, cy + 80, cx + 20, cy + 110);
        }
    }
}
