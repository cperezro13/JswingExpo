package jswingexp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class JswingExpo extends JFrame {

    public JswingExpo() {
        setTitle("Juego Clicker");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // Evita que se cierre directamente
        setLocationRelativeTo(null);

        // Crear una instancia del panel del juego
        GamePanel gamePanel = new GamePanel();
        add(gamePanel);

        // Agregar WindowListener para manejar eventos de la ventana
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Mostrar un cuadro de diálogo de confirmación
                int option = JOptionPane.showConfirmDialog(
                        JswingExpo.this,
                        "¿Estás seguro de que quieres salir?",
                        "Confirmar salida",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

                // Si el usuario confirma, guardar el estado y cerrar la ventana
                if (option == JOptionPane.YES_OPTION) {
                    gamePanel.saveGameState();
                    System.out.println("Estado del juego guardado. Cerrando la ventana...");
                    dispose(); // Cierra la ventana
                }
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JswingExpo game = new JswingExpo();
            game.setVisible(true);
        });
    }
}

class GamePanel extends JPanel implements MouseListener {

    private int clickCount = 0;
    private JLabel clickLabel;
    private JLabel instructionLabel; // Etiqueta para mostrar "Haz clic aquí"
    private JButton clickButton;
    private static final String STATE_FILE = "clicker_state.txt";

    // Rutas de las imágenes (rutas relativas dentro del proyecto)
    private final String[] imagePaths = {
        "images/ga530d1ck9pd1.png", // Ruta de la primera imagen
        "images/frame_05_delay-0.12s.png", // Ruta de la segunda imagen
    };
    private int currentImageIndex = 0; // Índice de la imagen actual

    public GamePanel() {
        setLayout(new BorderLayout());

        // Cargar el estado del juego al iniciar
        loadGameState();

        // Etiqueta para mostrar el contador de clics
        clickLabel = new JLabel("Clics: " + clickCount, SwingConstants.CENTER);
        clickLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(clickLabel, BorderLayout.NORTH);

        // Etiqueta para mostrar "Haz clic aquí" cuando el mouse entre en el botón
        instructionLabel = new JLabel("", SwingConstants.CENTER);
        instructionLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        add(instructionLabel, BorderLayout.CENTER);

        // Botón para hacer clic (sin texto inicial)
        clickButton = new JButton();
        clickButton.setPreferredSize(new Dimension(100, 100)); // Tamaño fijo del botón
        clickButton.addMouseListener(this);

        // Cargar la primera imagen en el botón
        updateButtonImage();
        add(clickButton, BorderLayout.SOUTH);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // Incrementar el contador de clics y actualizar la etiqueta
        clickCount++;
        clickLabel.setText("Clics: " + clickCount);

        // Cambiar la imagen del botón
        currentImageIndex = (currentImageIndex + 1) % imagePaths.length; // Alternar entre imágenes
        updateButtonImage();
    }

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {
        // Cuando el mouse entra en el botón, mostrar "Haz clic aquí" en la etiqueta
        if (e.getSource() == clickButton) {
            instructionLabel.setText("¡Haz clic aquí!");
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // Cuando el mouse sale del botón, borrar el texto de la etiqueta
        if (e.getSource() == clickButton) {
            instructionLabel.setText("");
        }
    }

    // Método para actualizar la imagen del botón
    private void updateButtonImage() {
        try {
            // Cargar la imagen actual
            ImageIcon icon = new ImageIcon(getClass().getResource(imagePaths[currentImageIndex]));
            if (icon.getImageLoadStatus() != MediaTracker.COMPLETE) {
                throw new IOException("No se pudo cargar la imagen: " + imagePaths[currentImageIndex]);
            }

            // Escalar la imagen al tamaño del botón
            int width = clickButton.getWidth() > 0 ? clickButton.getWidth() : 100; // Tamaño predeterminado si no está definido
            int height = clickButton.getHeight() > 0 ? clickButton.getHeight() : 100;
            Image scaledImage = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);

            // Asignar la imagen escalada al botón
            clickButton.setIcon(new ImageIcon(scaledImage));
        } catch (Exception e) {
            System.err.println("Error al cargar la imagen: " + imagePaths[currentImageIndex]);
            e.printStackTrace();
        }
    }

    // Método para guardar el estado del juego en un archivo
    public void saveGameState() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(STATE_FILE))) {
            writer.write(String.valueOf(clickCount));
            System.out.println("Estado del juego guardado: " + clickCount);
        } catch (IOException e) {
            System.err.println("Error al guardar el estado del juego: " + e.getMessage());
        }
    }

    // Método para cargar el estado del juego desde un archivo
    private void loadGameState() {
        File file = new File(STATE_FILE);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(STATE_FILE))) {
                String line = reader.readLine();
                if (line != null) {
                    clickCount = Integer.parseInt(line);
                    System.out.println("Estado del juego cargado: " + clickCount);
                }
            } catch (IOException e) {
                System.err.println("Error al cargar el estado del juego: " + e.getMessage());
            }
        } else {
            System.out.println("No se encontró un estado previo. Comenzando desde 0.");
        }
    }
}