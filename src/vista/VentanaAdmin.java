package vista;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import control.ControladorPrincipal;

/**
 * Panel de administración de la plataforma.
 * <p>
 * Solo accesible para usuarios de tipo {@link modelo.Admin}. Permite consultar:
 * </p>
 * <ul>
 *   <li>Ranking de Pasapalabra — partidas finalizadas ordenadas por puntuación.</li>
 *   <li>Ranking de Ahorcado — ídem para el juego del Ahorcado.</li>
 *   <li>Lista de usuarios — todos los usuarios con sus últimas 5 partidas.</li>
 * </ul>
 *
 * <p>La información se muestra en un área de texto monoespaciada que se actualiza
 * al pulsar cada botón. Al abrirse, muestra por defecto la lista de usuarios.</p>
 */
public class VentanaAdmin extends JInternalFrame {

    /** Controlador principal del que se obtienen los datos a mostrar. */
    private ControladorPrincipal cp;

    /** Área de texto donde se muestra la información generada por el administrador. */
    private JTextArea areaTexto;

    /**
     * Construye el panel de administración.
     *
     * @param cp controlador principal con acceso al {@link control.ControladorAdmin}.
     */
    public VentanaAdmin(ControladorPrincipal cp) {
        super("Panel de Administrador", true, true, true, true);
        this.cp = cp;
        crearVista();
    }

    /**
     * Construye y añade todos los componentes visuales: título, área de texto con scroll
     * y tres botones para seleccionar la información a mostrar.
     * Al abrir la ventana, se carga automáticamente la lista de usuarios.
     */
    private void crearVista() {
        setSize(600, 480);
        setLocation(80, 50);
        setLayout(new BorderLayout(10, 10));

        JLabel titulo = new JLabel("Panel de Administrador", JLabel.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 16));
        titulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        add(titulo, BorderLayout.NORTH);

        areaTexto = new JTextArea();
        areaTexto.setFont(new Font("Courier New", Font.PLAIN, 12));
        areaTexto.setEditable(false);
        areaTexto.setMargin(new Insets(5, 5, 5, 5));
        add(new JScrollPane(areaTexto), BorderLayout.CENTER);

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

        areaTexto.setText(cp.ctrlAdmin.getInfoTodosLosUsuarios());
        setVisible(true);
    }
}
