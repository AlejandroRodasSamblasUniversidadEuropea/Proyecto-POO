package vista;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import control.ControladorPrincipal;

/**
 * Panel del administrador.
 * Permite ver el ranking por juego y la lista completa de usuarios con sus partidas.
 */
public class VentanaAdmin extends JInternalFrame {

    private ControladorPrincipal cp;
    private JTextArea areaTexto;

    public VentanaAdmin(ControladorPrincipal cp) {
        super("Panel de Administrador", true, true, true, true);
        this.cp = cp;
        crearVista();
    }

    private void crearVista() {
        setSize(600, 480);
        setLocation(80, 50);
        setLayout(new BorderLayout(10, 10));

        JLabel titulo = new JLabel("Panel de Administrador", JLabel.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 16));
        titulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        add(titulo, BorderLayout.NORTH);

        // Area de texto para mostrar la informacion
        areaTexto = new JTextArea();
        areaTexto.setFont(new Font("Courier New", Font.PLAIN, 12));
        areaTexto.setEditable(false);
        areaTexto.setMargin(new Insets(5, 5, 5, 5));
        add(new JScrollPane(areaTexto), BorderLayout.CENTER);

        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout());

        JButton btnRankingPasapalabra = new JButton("Ranking Pasapalabra");
        JButton btnRankingAhorcado    = new JButton("Ranking Ahorcado");
        JButton btnListaUsuarios      = new JButton("Lista de usuarios");

        btnRankingPasapalabra.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                areaTexto.setText(cp.ctrlAdmin.getRankingDeJuego("Pasapalabra"));
            }
        });
        btnRankingAhorcado.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                areaTexto.setText(cp.ctrlAdmin.getRankingDeJuego("Ahorcado"));
            }
        });
        btnListaUsuarios.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                areaTexto.setText(cp.ctrlAdmin.getInfoTodosLosUsuarios());
            }
        });

        panelBotones.add(btnRankingPasapalabra);
        panelBotones.add(btnRankingAhorcado);
        panelBotones.add(btnListaUsuarios);
        add(panelBotones, BorderLayout.SOUTH);

        // Al abrir, mostramos la lista de usuarios por defecto
        areaTexto.setText(cp.ctrlAdmin.getInfoTodosLosUsuarios());
        setVisible(true);
    }
}
