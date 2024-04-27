package zad1;

import javax.swing.*;

public class GUI {
    private final JFrame frame;
    private final JTextField wordField;
    private final JTextField languageCodeField;

    public GUI() {
        frame = new JFrame("Kuba's Translator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 200);

        JLabel wordLabel = new JLabel("Word to Translate:");
        JLabel languageCodeLabel = new JLabel("Target Language Code:");
        wordField = new JTextField(20);
        languageCodeField = new JTextField(20);
        JButton translateButton = new JButton("Translate!");

        translateButton.addActionListener(e -> {
            String wordToTranslate = wordField.getText();
            String targetLanguageCode = languageCodeField.getText();
            String translation = new Client(8070).translate(wordToTranslate, targetLanguageCode);
            JOptionPane.showMessageDialog(frame, translation);

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
