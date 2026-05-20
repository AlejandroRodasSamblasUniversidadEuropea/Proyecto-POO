package vista;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import control.ControladorPrincipal;
import modelo.Partida;
import modelo.Pasapalabra;
import modelo.Pregunta;

/**
 * Ventana del juego Pasapalabra.
 * Muestra el rosco con las letras en circulo.
 * El jugador escribe su respuesta y pulsa Responder o Pasapalabra.
 *
 * El estado del rosco se guarda como: "A=C;B=P;C=F;..." (C=correcto, F=fallado, P=pendiente)
 */
public class VentanaPasapalabra extends VentanaPartida {

    private Partida partida;
    private Pasapalabra juego;
    private ControladorPrincipal cp;

    // Estado del rosco: un array de resultados para cada pregunta
    // 'P' = pendiente, 'C' = correcta, 'F' = fallada
    private char[] estadoLetras;
    private int indicePreguntaActual; // indice en la lista de preguntas

    // Componentes de la interfaz
    private JPanel panelRosco;
    private JLabel labelPregunta;
    private JLabel labelLetraActual;
    private JTextField campoRespuesta;
    private JButton botonResponder;
    private JButton botonPasapalabra;
    private JButton botonPausar;
    private JLabel labelPuntuacion;
    private JLabel labelEstado;

    public VentanaPasapalabra(Partida partida, ControladorPrincipal cp) {
        super("Pasapalabra");
        this.partida = partida;
        this.juego   = (Pasapalabra) partida.getJuego();
        this.cp      = cp;
        cargarEstado();
        crearVista();
        actualizarVista();
    }

    // Carga el estado del rosco desde el string guardado en la partida
    private void cargarEstado() {
        String estado = partida.getEstadoGuardado();
        String[] partes = estado.split(";");
        estadoLetras = new char[partes.length];
        for (int i = 0; i < partes.length; i++) {
            // Cada parte tiene formato "A=P" o "B=C" etc.
            if (partes[i].contains("=")) {
                estadoLetras[i] = partes[i].split("=")[1].charAt(0);
            } else {
                estadoLetras[i] = 'P';
            }
        }
        // Buscamos la primera pregunta pendiente
        indicePreguntaActual = primeraPendiente();
    }

    // Devuelve el indice de la primera pregunta pendiente, o -1 si no hay
    private int primeraPendiente() {
        for (int i = 0; i < estadoLetras.length; i++) {
            if (estadoLetras[i] == 'P') return i;
        }
        return -1;
    }

    @Override
    public void crearVista() {
        setSize(680, 580);
        setLocation(50, 30);
        setLayout(new BorderLayout(10, 10));

        // ---- PANEL SUPERIOR: letra actual y pregunta ----
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        labelLetraActual = new JLabel("Letra: ?", JLabel.CENTER);
        labelLetraActual.setFont(new Font("Arial", Font.BOLD, 28));
        labelLetraActual.setForeground(Color.BLUE);

        labelPregunta = new JLabel("Pregunta", JLabel.CENTER);
        labelPregunta.setFont(new Font("Arial", Font.PLAIN, 14));

        panelSuperior.add(labelLetraActual, BorderLayout.NORTH);
        panelSuperior.add(labelPregunta, BorderLayout.CENTER);

        // ---- PANEL CENTRAL: rosco dibujado ----
        panelRosco = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                dibujarRosco(g);
            }
        };
        panelRosco.setPreferredSize(new Dimension(300, 300));

        // ---- PANEL INFERIOR: respuesta y botones ----
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
        labelEstado = new JLabel("", JLabel.CENTER);
        labelEstado.setFont(new Font("Arial", Font.BOLD, 13));

        panelInferior.add(campoRespuesta);
        panelInferior.add(panelBotones);
        panelInferior.add(labelPuntuacion);

        add(panelSuperior, BorderLayout.NORTH);
        add(panelRosco,    BorderLayout.CENTER);
        add(panelInferior, BorderLayout.SOUTH);
    }

    // Dibuja el rosco con circulos coloreados por estado
    private void dibujarRosco(Graphics g) {
        int n = estadoLetras.length;
        if (n == 0) return;

        int cx = panelRosco.getWidth() / 2;
        int cy = panelRosco.getHeight() / 2;
        int radio = Math.min(cx, cy) - 30;
        int radioCirculo = 18;

        g.setFont(new Font("Arial", Font.BOLD, 12));

        for (int i = 0; i < n; i++) {
            double angulo = 2 * Math.PI * i / n - Math.PI / 2;
            int x = (int) (cx + radio * Math.cos(angulo));
            int y = (int) (cy + radio * Math.sin(angulo));

            char letra = juego.getPreguntas().get(i).getLetra();
            char estado = estadoLetras[i];

            // Color segun estado
            if (i == indicePreguntaActual) {
                g.setColor(Color.BLUE);
            } else if (estado == 'C') {
                g.setColor(new Color(0, 180, 0)); // verde
            } else if (estado == 'F') {
                g.setColor(Color.RED);
            } else {
                g.setColor(Color.LIGHT_GRAY);
            }

            g.fillOval(x - radioCirculo, y - radioCirculo, radioCirculo * 2, radioCirculo * 2);
            g.setColor(Color.BLACK);
            g.drawOval(x - radioCirculo, y - radioCirculo, radioCirculo * 2, radioCirculo * 2);

            // Dibujamos la letra centrada
            FontMetrics fm = g.getFontMetrics();
            String letraStr = String.valueOf(Character.toUpperCase(letra));
            g.setColor(Color.WHITE);
            g.drawString(letraStr, x - fm.stringWidth(letraStr) / 2, y + fm.getAscent() / 2 - 2);
        }
    }

    // Actualiza los labels con la pregunta actual y la puntuacion
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

    // El jugador responde a la pregunta actual
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

    // El jugador pasa la pregunta actual
    private void pasapalabra() {
        if (indicePreguntaActual < 0) return;
        // La letra queda como 'P' (pendiente), no suma ni resta
        avanzarAlguiente();
    }

    // Avanza a la siguiente pregunta pendiente
    private void avanzarAlguiente() {
        // Buscamos la siguiente pendiente a partir de la actual
        int n = estadoLetras.length;
        int siguiente = -1;
        for (int i = 1; i <= n; i++) {
            int idx = (indicePreguntaActual + i) % n;
            if (estadoLetras[idx] == 'P') {
                siguiente = idx;
                break;
            }
        }
        indicePreguntaActual = siguiente;

        // Actualizamos puntuacion en la partida
        int pts = juego.calcularPuntuacion(estadoAString());
        partida.setPuntuacion(0, pts);
        guardarEstadoEnPartida();

        if (siguiente == -1) {
            // Rosco completo
            terminarPartida();
        } else {
            actualizarVista();
        }
    }

    // Convierte el array de estados de vuelta a string para guardar
    private String estadoAString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < juego.getPreguntas().size(); i++) {
            char letra = Character.toUpperCase(juego.getPreguntas().get(i).getLetra());
            sb.append(letra).append("=").append(estadoLetras[i]);
            if (i < juego.getPreguntas().size() - 1) sb.append(";");
        }
        return sb.toString();
    }

    private void guardarEstadoEnPartida() {
        partida.setEstadoGuardado(estadoAString());
        partida.setTurnoActual(indicePreguntaActual < 0 ? 0 : indicePreguntaActual);
        cp.ctrlFiles.actualizarPartida(partida);
    }

    private void terminarPartida() {
        int pts = juego.calcularPuntuacion(estadoAString());
        partida.setPuntuacion(0, pts);
        cp.ctrlJuego.finalizarPartida(partida);
        JOptionPane.showMessageDialog(this,
            "Rosco completado!\nPuntuacion final: " + pts + " puntos", "Fin del juego",
            JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }

    private void pausar() {
        guardarEstadoEnPartida();
        cp.ctrlJuego.pausarPartida(partida);
        dispose();
    }
}
