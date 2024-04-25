package zad1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class GUI {
    private JFrame frame;
    private JTextField wordField;
    private JTextField languageCodeField;
    private JButton translateButton;

    public GUI() {
        frame = new JFrame("Kuba's Translator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500,500);
        wordField = new JTextField();
        languageCodeField = new JTextField();
        translateButton = new JButton("Translate");

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

        frame.getContentPane().add(wordField, BorderLayout.NORTH);
        frame.getContentPane().add(languageCodeField, BorderLayout.CENTER);
        frame.getContentPane().add(translateButton, BorderLayout.SOUTH);

        frame.setVisible(true);
    }
}
