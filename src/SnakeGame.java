import DB.BaseDeDatos;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.Timer;

public class SnakeGame extends JPanel implements ActionListener, KeyListener {

    // Baldosas del tablero
    private static class Baldosa {
        int x, y;

        Baldosa(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }


    // Dimensiones del tablero
    int tableroAncho;
    int tableroLargo;
    int baldosaTamaño = 25; // Tamaño de cada casilla en el tablero

    // Imágenes
    private BufferedImage imgFondo;
    private BufferedImage imgSerpienteCabeza;

    // Serpiente
    Baldosa serpienteCabeza; // Cabeza de la serpiente
    ArrayList<Baldosa> serpienteCuerpo; // Cuerpo de la serpiente

    // Comida
    Baldosa comida;
    Random random; // Generador aleatorio para el posicionamiento de la comida

    // Lógica del juego
    Timer juegoTiempo;// Temporizador para actualizar el juego
    int velocidadX; // Dirección en X
    int velocidadY; // Dirección en Y
    boolean gameOver = false; // Indica si el juego termina

    // Fondo
    private Image fondo;

    // Dificultad
    private int nivel = 1;
    private int puntuacion = 0;
    private ArrayList<Baldosa> obstaculos;

    // Panel de pausa
    private JPanel panelPausa;
    private boolean juegoPausado = false;


    // Constructor del juego
    SnakeGame(int tableroAncho, int tableroLargo, int baldosaTamaño) {
        this.tableroAncho = tableroAncho;
        this.tableroLargo = tableroLargo;

        // Configuración del panel
        setPreferredSize(new Dimension(this.tableroAncho, this.tableroLargo));
        // nuevo fondo:
        fondo = new ImageIcon("src/resources/imagenes/grass.png").getImage();
        //setBackground(Color.BLACK); // Color del fondo
        addKeyListener(this); // Agrega detector de teclado
        setFocusable(true); // Permite que el panel reciva eventos de teclado



        // Inicializar la serpiente
        serpienteCabeza = new Baldosa(5, 5);
        serpienteCuerpo = new ArrayList<Baldosa>();

        // Inicializar la comida
        comida = new Baldosa(10, 10);
        random = new Random();
        posicionComida(); // Genera la comida en una posición aleatoria

        obstaculos = new ArrayList<>();

        // Inicializar la dirección de la serpiente
        velocidadX = 0;
        velocidadY = 0;

        // Inicializar el temporizador
        juegoTiempo = new Timer(150, this);
        juegoTiempo.start();

        baseDeDatos = new BaseDeDatos();
    }

    // Método para recibir el panel de pausa
    public void setPanelPausa(JPanel panelPausa) {
        this.panelPausa = panelPausa;
    }


    // Método para reunadar el juego
    public void reanudarJuego() {
        panelPausa.setVisible(false);
        juegoTiempo.start();
        juegoPausado = false;
        this.requestFocusInWindow();
    }

    // Método que dibuja los componentes en la pantalla
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Dibujar imagen de fondo
        g.drawImage(fondo, 0, 0, getWidth(), getHeight(), this);

        dibujar(g);
    }

    // Método para dibujar la cuadricula, la comida y la serpiente
    public void dibujar(Graphics g) {

        // Dibujar la comida
        g.setColor(Color.RED);
        g.fillRect(comida.x * baldosaTamaño, comida.y * baldosaTamaño, baldosaTamaño, baldosaTamaño);

        // Dibujar la cabeza de la serpiente
        g.setColor(Color.yellow);
        g.fillRect(serpienteCabeza.x * baldosaTamaño, serpienteCabeza.y * baldosaTamaño, baldosaTamaño, baldosaTamaño);

        // Dibujar el cuerpo de la serpiente
        for (Baldosa serpienteParte : serpienteCuerpo) {
            g.fillRect(serpienteParte.x * baldosaTamaño, serpienteParte.y * baldosaTamaño, baldosaTamaño, baldosaTamaño);
        }

        g.setColor(Color.WHITE);
        // previene null pointer exception
        if (obstaculos == null) {
            obstaculos = new ArrayList<>();
        }
        for (Baldosa obstaculo : obstaculos) {
            g.fillRect(obstaculo.x * baldosaTamaño, obstaculo.y * baldosaTamaño, baldosaTamaño, baldosaTamaño);
        }

        g.setFont(new Font("Monospaced", Font.BOLD, 24));
        g.setColor(Color.WHITE);
        g.drawString("Puntuación: " + puntuacion, baldosaTamaño - 16, baldosaTamaño);
        g.drawString("Nivel: " + nivel, baldosaTamaño - 16, baldosaTamaño + 30);

        if (gameOver) {
            g.setFont(new Font("Monospaced", Font.BOLD, 40));
            g.setColor(Color.RED);
            g.drawString("GAME OVER", tableroAncho / 3, tableroLargo / 2);
        }

    }

    // Genera una posicion aleatoria para la comida dentro de los límites del tablero
    public void posicionComida() {
        comida.x = random.nextInt(tableroAncho / baldosaTamaño);
        comida.y = random.nextInt(tableroLargo / baldosaTamaño);
    }

    public void actualizarNivel() {
        if (puntuacion >= nivel * 10) {
            nivel++;
            juegoTiempo.setDelay(Math.max(50, 150 - (nivel * 10)));
            if (nivel != 1) {
                obstaculos.add(new Baldosa(random.nextInt(tableroAncho / baldosaTamaño), random.nextInt(tableroLargo / baldosaTamaño)));
            }
        }
    }

    // Verifica si dos bloques chocan o están en la misma posición
    public boolean colision(Baldosa baldosa1, Baldosa baldosa2) {
        // Compara las coordenadas xy de ambas casillas
        return baldosa1.x == baldosa2.x && baldosa1.y == baldosa2.y;
    }

    public void generarObstaculos() {
        obstaculos.clear();
        for (int i = 0; i < 5; i++) {
            obstaculos.add(new Baldosa(random.nextInt(tableroAncho / baldosaTamaño), random.nextInt(tableroLargo / baldosaTamaño)));
        }
    }

    // Movimiento de la serpiente
    public void movimiento() {
        // Comprueba si la serpiente come la comida
        if (colision(serpienteCabeza, comida)) {
            serpienteCuerpo.add(new Baldosa(comida.x, comida.y)); // Agrega una nueva parte al cuerpo
            posicionComida(); // Genera una nueva comida
            puntuacion++;
            actualizarNivel();
        }

        // Mueve el cuerpo de la serpiente
        for (int i = serpienteCuerpo.size() - 1; i > 0; i--) {
            serpienteCuerpo.get(i).x = serpienteCuerpo.get(i - 1).x;
            serpienteCuerpo.get(i).y = serpienteCuerpo.get(i - 1).y;
        }

        if (!serpienteCuerpo.isEmpty()) {
            serpienteCuerpo.get(0).x = serpienteCabeza.x;
            serpienteCuerpo.get(0).y = serpienteCabeza.y;
        }

        // Mueve la cabeza de la serpiente en la dirección deseada
        serpienteCabeza.x += velocidadX;
        serpienteCabeza.y += velocidadY;

        // Verifica la colisión con su propio cuerpo
        for (Baldosa serpienteParte : serpienteCuerpo) {
            if (colision(serpienteCabeza, serpienteParte)) {
                gameOver = true;
            }
        }

        // colision con obstaculos
        for (Baldosa obstaculo : obstaculos) {
            if (colision(serpienteCabeza, obstaculo)) {
                gameOver = true;
            }
        }

        // Verifica colisión con los bordes del tablero
        if (serpienteCabeza.x * baldosaTamaño < 0 || serpienteCabeza.x * baldosaTamaño >= tableroAncho ||
            serpienteCabeza.y * baldosaTamaño < 0 || serpienteCabeza.y * baldosaTamaño >= tableroLargo) {
            gameOver = true;
        }


    }





    // Método que se ejecuta en cada iteración del temporizador
    @Override
    public void actionPerformed(ActionEvent e) {
        if (!juegoPausado) {
            movimiento(); // Mueve la serpiente
            repaint(); // Redibuja la pantalla
            if (gameOver) {
                juegoTiempo.stop(); // Detiene el juego si es "Game over"
                gameOver(puntuacion);
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    // Maneja las teclas para mover la serpiente
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE && panelPausa != null) {
            if (panelPausa.isVisible()) {
                reanudarJuego();
            } else {
                juegoTiempo.stop();
                panelPausa.setVisible(true);
                panelPausa.repaint();
                panelPausa.revalidate();
                SwingUtilities.updateComponentTreeUI(panelPausa.getParent());
                juegoPausado = true;
            }
        }

        if (!gameOver && !juegoPausado) {
            if (e.getKeyCode() == KeyEvent.VK_DOWN && velocidadY != -1) {
                velocidadX = 0;
                velocidadY = 1;
            } else if (e.getKeyCode() == KeyEvent.VK_UP && velocidadY != 1) {
                velocidadX = 0;
                velocidadY = -1;
            } else if (e.getKeyCode() == KeyEvent.VK_LEFT && velocidadX != 1) {
                velocidadX = -1;
                velocidadY= 0;
            } else if (e.getKeyCode() == KeyEvent.VK_RIGHT && velocidadX != -1){
                velocidadX = 1;
                velocidadY = 0;
            }
        }


    }

    @Override
    public void keyReleased(KeyEvent e) {

    }


    // BBDD
    private BaseDeDatos baseDeDatos;

    public void gameOver(int score) {
        // ventana ingresar nombre
        String nombreJugador = JOptionPane.showInputDialog(null, "Ingresa tu nombre: ", "Game Over", JOptionPane.INFORMATION_MESSAGE);

        // Si el jugador no ingresa nombre, se asigna uno por defecto
        if (nombreJugador == null || nombreJugador.trim().isEmpty()) {
            nombreJugador = "Jugador";
        }

        // Guardar la puntuacion junto con el nombre
        baseDeDatos.guardarPuntuacion(nombreJugador, score);

        // mostrar el ranking
        mostrarRanking();
    }

    private void mostrarRanking() {
        String ranking = baseDeDatos.obtenerRanking();
        JOptionPane.showMessageDialog(null, "Ranking de los 5 mejores jugadores:\n\n" + ranking, "Ranking", JOptionPane.INFORMATION_MESSAGE);
    }


}
