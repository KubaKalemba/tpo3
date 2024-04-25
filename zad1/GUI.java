package zad1;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class GUI {
    private final JFrame frame;
    private final JTextField wordField;
    private final JTextField languageCodeField;

    public GUI() {
        frame = new JFrame("Kuba's Translator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 200);

        // Create labels
        JLabel wordLabel = new JLabel("Word to Translate:");
        JLabel languageCodeLabel = new JLabel("Target Language Code:");

        wordField = new JTextField(20); // Set preferred width
        languageCodeField = new JTextField(20); // Set preferred width
        JButton translateButton = new JButton("Translate");

        translateButton.addActionListener(e -> {
            String wordToTranslate = wordField.getText();
            String targetLanguageCode = languageCodeField.getText();

            //sending request to the main server
            try (Socket socket = new Socket("localhost", 8080);
                 ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                 ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

                Request request = new Request(wordToTranslate, targetLanguageCode);
                out.writeObject(request);
                out.flush();

                String translation = (String) in.readObject();

                JOptionPane.showMessageDialog(frame, translation);

            } catch (IOException | ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        });

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(wordLabel);
        panel.add(wordField);
        panel.add(languageCodeLabel);
        panel.add(languageCodeField);
        panel.add(translateButton);

        frame.getContentPane().add(panel);
        frame.setVisible(true);
    }
}
