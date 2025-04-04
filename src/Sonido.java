import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;

public class Sonido {
    private Clip clip;


    // Constructor que carga un sonido
    public Sonido(String rutaRecurso) {
        try {
            // Obtener la URL del archivo de sonido
            URL url = getClass().getResource(rutaRecurso);
            if (url == null) {
                throw new RuntimeException("Archivo de sonido no encontrado " + rutaRecurso);
            }

            // Cargar el archivo de sonido en un audioInputStream
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(url);
            clip = AudioSystem.getClip(); // Crea el clip de sonido
            clip.open(audioInputStream); // Abrir el archivo de audio en el Clip
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    // Reproduce el sonido una vez desde el principio
    public void reproducir() {
        if (clip != null) {
            clip.stop(); // Detener si ya está sonando
            clip.setFramePosition(0); // Reiniciar al inicio
            clip.start(); // Comenzar la reproducción
        }
    }

    // Reproducir el sonido en bucle
    public void reproducirEnBucle() {
        if (clip != null) {
            clip.loop(Clip.LOOP_CONTINUOUSLY); // Repetir indefinidamente
        }
    }

    // Detiene el sonido si está en reproducción
    public void detener() {
        if (clip != null) {
            clip.stop();
        }
    }

    // Cierra el clip y libera los recursos de audio
    public void cerrar() {
        try {
            if (clip != null) {
                clip.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Verifica si el sonido se está reproduciendo
    public boolean estaSonando() {
        return clip != null && clip.isRunning();
    }

    public void reproducirYEsperar() {
        if (clip != null) {
            clip.stop();
            clip.setFramePosition(0);
            clip.start();

            // Esperar hasya que termine la reproducicion
            while (clip.isRunning()) {
                try {
                    Thread.sleep(500); // pequeña pasusa para no sobrecargar la cpu
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
