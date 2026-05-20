package vista;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import control.ControladorPrincipal;
import modelo.Ahorcado;
import modelo.Partida;

/**
 * Ventana del juego Ahorcado.
 * Muestra la palabra oculta con guiones, el dibujo del ahorcado,
 * y un campo para introducir letras.
 */
public class VentanaAhorcado extends VentanaPartida {

    private Partida partida;
    private Ahorcado juego;
    private ControladorPrincipal cp;

    // Estado del juego (cadena: "PALABRA;letrasUsadas;fallos")
    private String estado;

    // Componentes
    private JLabel labelPalabra;
    private JLabel labelFallos;
    private JLabel labelLetrasUsadas;
    private JTextField campoLetra;
    private JButton botonJugar;
    private JButton botonPausar;
    private DibujoAhorcado panelDibujo;

    public VentanaAhorcado(Partida partida, ControladorPrincipal cp) {
        super("Ahorcado");
        this.partida = partida;
        this.juego   = (Ahorcado) partida.getJuego();
        this.cp      = cp;
        this.estado  = partida.getEstadoGuardado();
        crearVista();
        actualizarVista();
    }

    @Override
    public void crearVista() {
        setSize(600, 520);
        setLocation(60, 40);
        setLayout(new BorderLayout(10, 10));

        // Panel superior: palabra y estado
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

        // Panel central: dibujo del ahorcado
        panelDibujo = new DibujoAhorcado();
        panelDibujo.setPreferredSize(new Dimension(200, 220));

        // Panel inferior: campo para letra y botones
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
        // También responde al ENTER en el campo de texto
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

    // Actualiza todos los componentes visuales con el estado actual
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

    // Procesa la letra introducida por el jugador
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

        // Actualizamos puntuacion en la partida
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

    private void pausar() {
        partida.setEstadoGuardado(estado);
        cp.ctrlJuego.pausarPartida(partida);
        dispose();
    }

    // ---- Clase interna: dibuja el ahorcado en funcion de los fallos ----
    private static class DibujoAhorcado extends JPanel {
        private int fallos = 0;

        public void setFallos(int fallos) { this.fallos = fallos; }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(Color.BLACK);
            Graphics2D g2 = (Graphics2D) g;
            g2.setStroke(new BasicStroke(3));

            int ox = 30, oy = getHeight() - 20; // origen base

            // Base
            g.drawLine(ox, oy, ox + 120, oy);
            // Palo vertical
            g.drawLine(ox + 50, oy, ox + 50, oy - 180);
            // Palo horizontal
            g.drawLine(ox + 50, oy - 180, ox + 130, oy - 180);
            // Cuerda
            g.drawLine(ox + 130, oy - 180, ox + 130, oy - 150);

            int cx = ox + 130, cy = oy - 150;

            if (fallos >= 1) { // cabeza
                g.drawOval(cx - 15, cy, 30, 30);
            }
            if (fallos >= 2) { // cuerpo
                g.drawLine(cx, cy + 30, cx, cy + 80);
            }
            if (fallos >= 3) { // brazo izquierdo
                g.drawLine(cx, cy + 45, cx - 20, cy + 65);
            }
            if (fallos >= 4) { // brazo derecho
                g.drawLine(cx, cy + 45, cx + 20, cy + 65);
            }
            if (fallos >= 5) { // pierna izquierda
                g.drawLine(cx, cy + 80, cx - 20, cy + 110);
            }
            if (fallos >= 6) { // pierna derecha
                g.drawLine(cx, cy + 80, cx + 20, cy + 110);
            }
        }
    }
}
