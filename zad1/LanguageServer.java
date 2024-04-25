package zad1;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class LanguageServer implements Runnable{

    private String languageCode;
    private Map<String, String> dictionary;
    private int PORT;

    public LanguageServer(String languageCode, int port) {
        this.languageCode = languageCode;
        this.dictionary = new HashMap<>();
        this.PORT = port;
        // Initialize dictionary with translations
    }

    public void run() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("[lang]: Starting language server on port " + PORT + "...");

        while (true) {
            Socket clientSocket = null;
            try {
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            System.out.println("[lang]: New client connected");

            ObjectInputStream in;
            ObjectOutputStream out;
            try {
                in = new ObjectInputStream(clientSocket.getInputStream());
                out = new ObjectOutputStream(clientSocket.getOutputStream());
            } catch (IOException e) {
                System.out.println("Error creating input/output streams");
                return;
            }

            Request request = null;
            try {
                request = (Request) in.readObject();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            String wordToTranslate = request.getWordToTranslate();
            String targetLanguageCode = request.getTargetLanguageCode();
            System.out.println("[lang]: WORD: " + wordToTranslate);
            System.out.println("[lang]: LANG: " + targetLanguageCode);

            //translate word
            String translation = getTranslation(wordToTranslate);

            //write translation to the stream
            try {
                out.writeObject("oj");
                out.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    //TODO
    public String getTranslation(String wordToTranslate) {
        // Get translation from dictionary
        return dictionary.get(wordToTranslate);
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public int getPORT() {
        return PORT;
    }
}
