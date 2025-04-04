import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main {
    public static void main(String[] args) {

        // Tamaño del tablero
        int baldosaTamaño = 25;
        int tableroAncho = 800;
        int tableroLargo = 800;

        // Ajustar tamaño del tablero
        tableroAncho = (tableroAncho / baldosaTamaño) * baldosaTamaño;
        tableroLargo = (tableroLargo / baldosaTamaño) * baldosaTamaño;

        // Ventana del juego
        JFrame frame = new JFrame("Snake Game"); // Crea ventana con titulo "Snake Game"
        //frame.setVisible(true); // La ventana es visible
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Cierra el programa al cerrar la ventana
        frame.setResizable(false); // Evita que la ventana se pueda cambiar de tamaño



        // Panel del juego
        SnakeGame snakeGame = new SnakeGame(tableroAncho, tableroLargo, baldosaTamaño);
        frame.add(snakeGame); // Agrega el panel del juego al JFrame
        frame.pack();
        frame.setLocationRelativeTo(null);

        // Panel para los botones superpuesto
        JLayeredPane jLayeredPane = new JLayeredPane();
        frame.setContentPane(jLayeredPane);
        snakeGame.setBounds(0, 0, tableroAncho, tableroLargo);
        jLayeredPane.add(snakeGame, JLayeredPane.DEFAULT_LAYER);

        // Panel de pausa ventana flotante
        JPanel panelPausa = new JPanel();
        panelPausa.setLayout(new GridLayout(2, 1, 10, 10));
        panelPausa.setBackground(new Color(0, 0, 0 ,  220));
        panelPausa.setBounds((tableroAncho - 300) / 2, (tableroLargo - 150) / 2, 300, 150);
        panelPausa.setBorder(BorderFactory.createLineBorder(Color.GREEN, 4));
        panelPausa.setVisible(false);
        jLayeredPane.add(panelPausa, JLayeredPane.PALETTE_LAYER);

        // Estilo de los botones
        Font fuente = new Font("Monospaced", Font.BOLD, 20);

        JButton btnContinuar = new JButton("▶ CONTINUAR");
        btnContinuar.setFont(fuente);
        btnContinuar.setForeground(Color.GREEN);
        btnContinuar.setBackground(Color.BLACK);
        btnContinuar.setFocusPainted(false);

        JButton btnSalir = new JButton("✖ SALIR");
        btnSalir.setFont(fuente);
        btnSalir.setForeground(Color.RED);
        btnSalir.setBackground(Color.BLACK);
        btnSalir.setFocusPainted(false);

        panelPausa.add(btnContinuar);
        panelPausa.add(btnSalir);

        // Botón de continuar
        btnContinuar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                snakeGame.reanudarJuego();
                panelPausa.setVisible(false);
                snakeGame.requestFocusInWindow();
            }
        });

        // Botón salir
        btnSalir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });


        // Panel de pausa
        snakeGame.setPanelPausa(panelPausa);

        // Hacer visible la ventana solo después de agregar componentes
        frame.setVisible(true);
        snakeGame.requestFocusInWindow(); // Establece el foco en el juego para detectar entradas de teclado

        // Sonido
        // Crear un objeto Sonido con la ruta del archivo
        Sonido sonido = new Sonido("resources/sonidos/newbattle.wav");

        // Reproducir el sonido una vez
        sonido.reproducir();

        // Esperar 2 segundos antes de iniciar el bucle
        try {
            Thread.sleep(2000); // Pausar el programa por 2 segundos
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Iniciar la reproducción en bucle
        sonido.reproducirEnBucle();


    }
}
