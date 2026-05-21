package vista;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import control.ControladorPrincipal;
import modelo.Partida;
import modelo.Pasapalabra;
import modelo.Pregunta;

/**
 * Ventana interna del juego Pasapalabra.
 * <p>
 * Muestra el rosco (rueda de letras dibujada en un panel) con cada letra
 * coloreada según su estado: azul (pregunta actual), verde (correcta),
 * rojo (fallada) o gris (pendiente).
 * </p>
 *
 * <h3>Interacción con el usuario</h3>
 * <ul>
 *   <li>"Responder" — evalúa la respuesta escrita y avanza a la siguiente pendiente.</li>
 *   <li>"Pasapalabra" — salta la pregunta sin penalizar y avanza a la siguiente pendiente.</li>
 *   <li>"Guardar y pausar" — guarda el estado sin finalizar la partida.</li>
 * </ul>
 *
 * <p>El estado del rosco se persiste en disco tras cada respuesta a través de
 * {@link control.ControladorFicheros#actualizarPartida(Partida)}.</p>
 */
public class VentanaPasapalabra extends VentanaPartida {

    /** Partida asociada a esta ventana. */
    private Partida partida;

    /** Instancia del juego Pasapalabra con la lógica de preguntas y puntuación. */
    private Pasapalabra juego;

    /** Controlador principal para persistir y finalizar la partida. */
    private ControladorPrincipal cp;

    /**
     * Array de resultados para cada pregunta del rosco:
     * {@code 'P'} = pendiente, {@code 'C'} = correcta, {@code 'F'} = fallada.
     */
    private char[] estadoLetras;

    /**
     * Índice de la pregunta actualmente activa en el array de preguntas.
     * Valor {@code -1} cuando el rosco está completo.
     */
    private int indicePreguntaActual;

    private JPanel panelRosco;
    private JLabel labelPregunta;
    private JLabel labelLetraActual;
    private JTextField campoRespuesta;
    private JButton botonResponder;
    private JButton botonPasapalabra;
    private JButton botonPausar;
    private JLabel labelPuntuacion;
    private JLabel labelEstado;

    /**
     * Construye la ventana de Pasapalabra, carga el estado guardado y muestra la primera
     * pregunta pendiente.
     *
     * @param partida partida (nueva o continuada) cuyo estado se mostrará.
     * @param cp      controlador principal para persistir cambios y finalizar la partida.
     */
    public VentanaPasapalabra(Partida partida, ControladorPrincipal cp) {
        super("Pasapalabra");
        this.partida = partida;
        this.juego   = (Pasapalabra) partida.getJuego();
        this.cp      = cp;
        cargarEstado();
        crearVista();
        actualizarVista();
    }

    /**
     * Deserializa el estado guardado de la partida al array {@link #estadoLetras}
     * y posiciona {@link #indicePreguntaActual} en la primera letra pendiente.
     */
    private void cargarEstado() {
        String estado  = partida.getEstadoGuardado();
        String[] partes = estado.split(";");
        estadoLetras = new char[partes.length];
        for (int i = 0; i < partes.length; i++) {
            if (partes[i].contains("=")) {
                estadoLetras[i] = partes[i].split("=")[1].charAt(0);
            } else {
                estadoLetras[i] = 'P';
            }
        }
        indicePreguntaActual = primeraPendiente();
    }

    /**
     * Busca la primera pregunta pendiente a partir del inicio del array.
     *
     * @return índice de la primera pregunta con estado {@code 'P'}, o {@code -1} si no hay ninguna.
     */
    private int primeraPendiente() {
        for (int i = 0; i < estadoLetras.length; i++) {
            if (estadoLetras[i] == 'P') return i;
        }
        return -1;
    }

    /**
     * Construye y añade todos los componentes visuales de la ventana:
     * panel superior con la letra y el enunciado, panel central con el rosco
     * y panel inferior con el campo de respuesta y los botones.
     */
    @Override
    public void crearVista() {
        setSize(680, 580);
        setLocation(50, 30);
        setLayout(new BorderLayout(10, 10));

        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        labelLetraActual = new JLabel("Letra: ?", JLabel.CENTER);
        labelLetraActual.setFont(new Font("Arial", Font.BOLD, 28));
        labelLetraActual.setForeground(Color.BLUE);

        labelPregunta = new JLabel("Pregunta", JLabel.CENTER);
        labelPregunta.setFont(new Font("Arial", Font.PLAIN, 14));

        panelSuperior.add(labelLetraActual, BorderLayout.NORTH);
        panelSuperior.add(labelPregunta, BorderLayout.CENTER);

        panelRosco = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                dibujarRosco(g);
            }
        };
        panelRosco.setPreferredSize(new Dimension(300, 300));

        JPanel panelInferior = new JPanel(new GridLayout(3, 1, 5, 5));
        panelInferior.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 20));

        campoRespuesta = new JTextField();
        campoRespuesta.setFont(new Font("Arial", Font.PLAIN, 16));

        JPanel panelBotones = new JPanel(new FlowLayout());
        botonResponder   = new JButton("Responder");
        botonPasapalabra = new JButton("Pasapalabra");
        botonPausar      = new JButton("Guardar y pausar");

        botonResponder.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { responder(); }
        });
        botonPasapalabra.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { pasapalabra(); }
        });
        botonPausar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { pausar(); }
        });

        panelBotones.add(botonResponder);
        panelBotones.add(botonPasapalabra);
        panelBotones.add(botonPausar);

        labelPuntuacion = new JLabel("Puntuacion: 0", JLabel.CENTER);
        labelEstado     = new JLabel("", JLabel.CENTER);
        labelEstado.setFont(new Font("Arial", Font.BOLD, 13));

        panelInferior.add(campoRespuesta);
        panelInferior.add(panelBotones);
        panelInferior.add(labelPuntuacion);

        add(panelSuperior, BorderLayout.NORTH);
        add(panelRosco,    BorderLayout.CENTER);
        add(panelInferior, BorderLayout.SOUTH);
    }

    /**
     * Dibuja el rosco circular con los colores de estado de cada letra.
     * <ul>
     *   <li>Azul — pregunta actualmente activa.</li>
     *   <li>Verde — respondida correctamente.</li>
     *   <li>Rojo — respondida incorrectamente.</li>
     *   <li>Gris — pendiente de responder.</li>
     * </ul>
     *
     * @param g contexto gráfico proporcionado por Swing.
     */
    private void dibujarRosco(Graphics g) {
        int n = estadoLetras.length;
        if (n == 0) return;

        int cx = panelRosco.getWidth() / 2;
        int cy = panelRosco.getHeight() / 2;
        int radio        = Math.min(cx, cy) - 30;
        int radioCirculo = 18;

        g.setFont(new Font("Arial", Font.BOLD, 12));

        for (int i = 0; i < n; i++) {
            double angulo = 2 * Math.PI * i / n - Math.PI / 2;
            int x = (int) (cx + radio * Math.cos(angulo));
            int y = (int) (cy + radio * Math.sin(angulo));

            char letra  = juego.getPreguntas().get(i).getLetra();
            char estado = estadoLetras[i];

            if (i == indicePreguntaActual) {
                g.setColor(Color.BLUE);
            } else if (estado == 'C') {
                g.setColor(new Color(0, 180, 0));
            } else if (estado == 'F') {
                g.setColor(Color.RED);
            } else {
                g.setColor(Color.LIGHT_GRAY);
            }

            g.fillOval(x - radioCirculo, y - radioCirculo, radioCirculo * 2, radioCirculo * 2);
            g.setColor(Color.BLACK);
            g.drawOval(x - radioCirculo, y - radioCirculo, radioCirculo * 2, radioCirculo * 2);

            FontMetrics fm     = g.getFontMetrics();
            String letraStr    = String.valueOf(Character.toUpperCase(letra));
            g.setColor(Color.WHITE);
            g.drawString(letraStr, x - fm.stringWidth(letraStr) / 2, y + fm.getAscent() / 2 - 2);
        }
    }

    /**
     * Refresca la letra activa, el enunciado de la pregunta actual y la puntuación.
     * Si el rosco está completo, muestra "Rosco completado" en los labels.
     */
    private void actualizarVista() {
        if (indicePreguntaActual >= 0 && indicePreguntaActual < juego.getPreguntas().size()) {
            Pregunta p = juego.getPreguntas().get(indicePreguntaActual);
            labelLetraActual.setText("Letra: " + Character.toUpperCase(p.getLetra()));
            labelPregunta.setText("<html><center>" + p.getEnunciado() + "</center></html>");
        } else {
            labelLetraActual.setText("Rosco completado");
            labelPregunta.setText("");
        }
        int pts = juego.calcularPuntuacion(estadoAString());
        labelPuntuacion.setText("Puntuacion: " + pts
            + "  |  Jugador: " + partida.getJugadores()[0].getUsername());
        panelRosco.repaint();
        campoRespuesta.setText("");
        campoRespuesta.requestFocus();
    }

    /**
     * Evalúa la respuesta del campo de texto para la pregunta activa.
     * Marca la letra como correcta ({@code 'C'}) o fallada ({@code 'F'})
     * y avanza a la siguiente pregunta pendiente.
     */
    private void responder() {
        if (indicePreguntaActual < 0) return;
        String respuesta = campoRespuesta.getText().trim();
        if (respuesta.isEmpty()) return;

        Pregunta p = juego.getPreguntas().get(indicePreguntaActual);
        if (p.esCorrecta(respuesta)) {
            estadoLetras[indicePreguntaActual] = 'C';
            JOptionPane.showMessageDialog(this, "¡Correcto!", "Pasapalabra",
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            estadoLetras[indicePreguntaActual] = 'F';
            JOptionPane.showMessageDialog(this,
                "Incorrecto. La respuesta era: " + p.getEnunciado().split(":")[0], "Pasapalabra",
                JOptionPane.ERROR_MESSAGE);
        }

        avanzarAlguiente();
    }

    /**
     * Salta la pregunta activa sin modificar su estado (queda como {@code 'P'})
     * y avanza a la siguiente pregunta pendiente.
     */
    private void pasapalabra() {
        if (indicePreguntaActual < 0) return;
        avanzarAlguiente();
    }

    /**
     * Busca la siguiente pregunta pendiente en sentido circular a partir de la actual
     * y actualiza {@link #indicePreguntaActual}. Si no hay más pendientes, finaliza la partida.
     * Persiste el estado en disco tras cada avance.
     */
    private void avanzarAlguiente() {
        int n        = estadoLetras.length;
        int siguiente = -1;
        for (int i = 1; i <= n; i++) {
            int idx = (indicePreguntaActual + i) % n;
            if (estadoLetras[idx] == 'P') {
                siguiente = idx;
                break;
            }
        }
        indicePreguntaActual = siguiente;

        int pts = juego.calcularPuntuacion(estadoAString());
        partida.setPuntuacion(0, pts);
        guardarEstadoEnPartida();

        if (siguiente == -1) {
            terminarPartida();
        } else {
            actualizarVista();
        }
    }

    /**
     * Convierte el array {@link #estadoLetras} de vuelta a la cadena de estado
     * con formato {@code "A=C;B=P;C=F;..."}.
     *
     * @return cadena de estado del rosco serializada.
     */
    private String estadoAString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < juego.getPreguntas().size(); i++) {
            char letra = Character.toUpperCase(juego.getPreguntas().get(i).getLetra());
            sb.append(letra).append("=").append(estadoLetras[i]);
            if (i < juego.getPreguntas().size() - 1) sb.append(";");
        }
        return sb.toString();
    }

    /**
     * Sincroniza el estado del rosco y el turno actual en la {@link Partida}
     * y persiste los cambios en disco.
     */
    private void guardarEstadoEnPartida() {
        partida.setEstadoGuardado(estadoAString());
        partida.setTurnoActual(indicePreguntaActual < 0 ? 0 : indicePreguntaActual);
        cp.ctrlFiles.actualizarPartida(partida);
    }

    /**
     * Finaliza la partida una vez completado el rosco: actualiza la puntuación,
     * marca la partida como terminada y muestra el resultado al jugador.
     */
    private void terminarPartida() {
        int pts = juego.calcularPuntuacion(estadoAString());
        partida.setPuntuacion(0, pts);
        cp.ctrlJuego.finalizarPartida(partida);
        JOptionPane.showMessageDialog(this,
            "Rosco completado!\nPuntuacion final: " + pts + " puntos", "Fin del juego",
            JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }

    /**
     * Guarda el estado actual del rosco y cierra la ventana sin finalizar la partida.
     * El jugador podrá reanudarla más tarde desde "Continuar partida guardada".
     */
    private void pausar() {
        guardarEstadoEnPartida();
        cp.ctrlJuego.pausarPartida(partida);
        dispose();
    }
}
