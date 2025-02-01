package Slot;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class TragaperrasArcadeGUI extends JFrame {

    private double saldo = 100.0;
    private double apuesta = 1.0;
    private final double[] valoresApuesta = {0.10, 0.20, 0.50, 1.00, 2.00, 5.00, 10.00};

    private JLabel saldoLabel;
    private JLabel apuestaLabel;
    private JTextArea outputArea;
    private JLabel[][] slots = new JLabel[3][3];

    // Rutas de las imágenes
    private String[] imagenes = {
            "img/cshard.png",
            "img/html.png",
            "img/java.png",
            "img/php.png",
            "img/ruby.png"
    };
    // Multiplicadores de premio según el símbolo (se puede usar aleatoriamente)
    private int[] premios = {2, 5, 10, 20, 50};

    private Random random = new Random();

    // Botones como atributos para habilitarlos/deshabilitarlos
    private JButton button1, button2, button3, button4, button5;

    public TragaperrasArcadeGUI() {
        setTitle("Tragaperras Arcade");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 500);
        setLayout(new BorderLayout());

        // 1. Panel de slots (3x3)
        JPanel slotPanel = new JPanel(new GridLayout(3, 3));
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                slots[i][j] = new JLabel();
                slots[i][j].setHorizontalAlignment(JLabel.CENTER);
                slots[i][j].setVerticalAlignment(JLabel.CENTER);
                // Imagen inicial (aleatoria o fija)
                slots[i][j].setIcon(new ImageIcon(imagenes[random.nextInt(imagenes.length)]));
                slotPanel.add(slots[i][j]);
            }
        }
        add(slotPanel, BorderLayout.CENTER);

        // 2. Panel de estado (saldo y apuesta)
        saldoLabel = new JLabel("Saldo: " + String.format("%.2f€", saldo));
        apuestaLabel = new JLabel("Apuesta: " + String.format("%.2f€", apuesta));
        JPanel statusPanel = new JPanel(new GridLayout(1, 2));
        statusPanel.add(saldoLabel);
        statusPanel.add(apuestaLabel);
        add(statusPanel, BorderLayout.NORTH);

        // 3. Área de salida de texto
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setText("Bienvenido a la tragaperras. Pulsa 5 para iniciar.");
        add(new JScrollPane(outputArea), BorderLayout.EAST);

        // 4. Panel de botones
        JPanel buttonPanel = new JPanel(new GridLayout(1, 5));

        button1 = new JButton("1");
        button1.addActionListener(e -> mostrarMensajePrueba());
        buttonPanel.add(button1);

        button2 = new JButton("2");
        button2.addActionListener(e -> disminuirApuesta());
        buttonPanel.add(button2);

        button3 = new JButton("3");
        button3.addActionListener(e -> aumentarApuesta());
        buttonPanel.add(button3);

        button4 = new JButton("4");
        button4.addActionListener(e -> girarTragaperras());
        buttonPanel.add(button4);

        button5 = new JButton("5");
        button5.addActionListener(e -> iniciarJuego());
        buttonPanel.add(button5);

        add(buttonPanel, BorderLayout.SOUTH);

        // 5. KeyListener para capturar teclas
        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) { }

            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyChar()) {
                    case '1': mostrarMensajePrueba(); break;
                    case '2': disminuirApuesta(); break;
                    case '3': aumentarApuesta(); break;
                    case '4': girarTragaperras(); break;
                    case '5': iniciarJuego(); break;
                    default:
                        outputArea.append("\nTecla desconocida: " + e.getKeyChar());
                }
            }

            @Override
            public void keyReleased(KeyEvent e) { }
        });

        // Asegura que la ventana recupere el foco para capturar teclas
        addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                requestFocusInWindow();
            }
        });

        setVisible(true);
    }

    /**
     * Lógica para girar la tragaperras con animación.
     */
    private void girarTragaperras() {
        if (saldo >= apuesta) {
            saldo -= apuesta;
            saldoLabel.setText("Saldo: " + String.format("%.2f€", saldo));
            outputArea.append("\nGirando con apuesta de " + String.format("%.2f€", apuesta) + "€");

            // Desactivar botones para que el usuario no interactúe mientras “gira”
            setBotonesHabilitados(false);

            // Número de “pasos” para la animación (cada paso se refresca la pantalla)
            final int pasosAnimacion = 15;
            // Tiempo entre cada paso (en milisegundos)
            final int delay = 100;

            // Creamos un Timer para actualizar las imágenes en cada paso
            Timer timer = new Timer(delay, null);
            final int[] contador = {0};  // Para contar el número de refrescos

            timer.addActionListener(e -> {
                // En cada tic del timer, refrescamos las imágenes
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        slots[i][j].setIcon(new ImageIcon(imagenes[random.nextInt(imagenes.length)]));
                    }
                }

                contador[0]++;
                if (contador[0] >= pasosAnimacion) {
                    // Se acabó la animación
                    timer.stop();

                    // Establecer la combinación final
                    for (int i = 0; i < 3; i++) {
                        for (int j = 0; j < 3; j++) {
                            slots[i][j].setIcon(new ImageIcon(imagenes[random.nextInt(imagenes.length)]));
                        }
                    }

                    // Comprobamos si hay premio (3 en línea horizontal)
                    boolean gano = false;
                    for (int i = 0; i < 3; i++) {
                        String icono1 = slots[i][0].getIcon().toString();
                        String icono2 = slots[i][1].getIcon().toString();
                        String icono3 = slots[i][2].getIcon().toString();
                        if (icono1.equals(icono2) && icono2.equals(icono3)) {
                            double premio = apuesta * premios[random.nextInt(premios.length)];
                            saldo += premio;
                            saldoLabel.setText("Saldo: " + String.format("%.2f€", saldo));
                            outputArea.append("\n¡Ganaste " + String.format("%.2f€", premio) + "!");
                            gano = true;
                            break;
                        }
                    }

                    if (!gano) {
                        outputArea.append("\nNo ganaste esta vez.");
                    }

                    // Volvemos a activar los botones
                    setBotonesHabilitados(true);
                }
            });

            // Iniciamos la animación
            timer.start();

        } else {
            outputArea.append("\nNo tienes saldo suficiente para girar.");
        }
    }

    /**
     * Activar o desactivar todos los botones para que el usuario
     * no haga clic mientras el juego “gira”.
     */
    private void setBotonesHabilitados(boolean habilitado) {
        button1.setEnabled(habilitado);
        button2.setEnabled(habilitado);
        button3.setEnabled(habilitado);
        button4.setEnabled(habilitado);
        button5.setEnabled(habilitado);
    }

    // -----------------------
    // Funcionalidades extra
    // -----------------------
    private void mostrarMensajePrueba() {
        outputArea.append("\nTecla '1' detectada: Mensaje de prueba.");
    }

    private void iniciarJuego() {
        outputArea.setText("¡Juego iniciado!\nSaldo inicial: " + String.format("%.2f€", saldo));
    }

    private void aumentarApuesta() {
        for (int i = 0; i < valoresApuesta.length - 1; i++) {
            if (apuesta == valoresApuesta[i]) {
                apuesta = valoresApuesta[i + 1];
                actualizarApuesta();
                return;
            }
        }
    }

    private void disminuirApuesta() {
        for (int i = valoresApuesta.length - 1; i > 0; i--) {
            if (apuesta == valoresApuesta[i]) {
                apuesta = valoresApuesta[i - 1];
                actualizarApuesta();
                return;
            }
        }
    }

    private void actualizarApuesta() {
        apuestaLabel.setText("Apuesta: " + String.format("%.2f€", apuesta));
        outputArea.append("\nApuesta actualizada: " + String.format("%.2f€", apuesta) + "€");
    }

    // -----------------------
    // Método main
    // -----------------------
    public static void main(String[] args) {
        SwingUtilities.invokeLater(TragaperrasArcadeGUI::new);
    }
}
