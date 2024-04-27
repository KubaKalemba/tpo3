package zad1;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class LanguageServer implements Runnable {

    private final String languageCode;
    private Map<String, String> dictionary;
    private final int PORT;

    public LanguageServer(String languageCode, int port) {
        System.out.println("Creating lang server on port " + port + "...");
        this.languageCode = languageCode;
        this.dictionary = new HashMap<>();
        this.PORT = port;
        initDictionary(languageCode);
    }

    public void run() {
        System.out.println("[lang]: Starting language server on port " + PORT + "...");
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("[lang]: Started!");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        while (true) {

            Socket mainServerSocket = null;
            try {
                mainServerSocket = serverSocket.accept();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            System.out.println("[lang]: Main server connected");

            ObjectInputStream in;
            ObjectOutputStream out;
            try {
                in = new ObjectInputStream(mainServerSocket.getInputStream());
                out = new ObjectOutputStream(mainServerSocket.getOutputStream());
            } catch (IOException e) {
                System.out.println("Error creating streams!");
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

            String translation = getTranslation(wordToTranslate);

            try {
                out.writeObject(translation);
                out.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void initDictionary(String languageCode) {
        Dictionaries.init();
        if(Dictionaries.dictionaries.containsKey(languageCode))
            dictionary = Dictionaries.dictionaries.get(languageCode);
        else
            dictionary = new HashMap<>();
    }

    public String getTranslation(String wordToTranslate) {
        if(dictionary.containsKey(wordToTranslate)) {
            return dictionary.get(wordToTranslate);
        }
        return "Nie ma takiego słowa w słowniku :(";
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public int getPORT() {
        return PORT;
    }
}
