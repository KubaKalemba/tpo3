package zad1;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class LanguageServer {

    private String languageCode;
    private Map<String, String> dictionary;
    private int PORT;

    public LanguageServer(String languageCode, int port) {
        this.languageCode = languageCode;
        this.dictionary = new HashMap<>();
        this.PORT = port;
        // Initialize dictionary with translations
    }

    public void run() throws IOException, ClassNotFoundException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Starting language server on port " + PORT + "...");

        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("New client connected");

            //read word from client
            ObjectInputStream in;
            ObjectOutputStream out;
            try {
                in = new ObjectInputStream(clientSocket.getInputStream());
                out = new ObjectOutputStream(clientSocket.getOutputStream());
            } catch (IOException e) {
                System.out.println("Error creating input/output streams");
                return;
            }

            Request request = (Request) in.readObject();
            String wordToTranslate = request.getWordToTranslate();
            String targetLanguageCode = request.getTargetLanguageCode();
            System.out.println("WORD: " +wordToTranslate);
            System.out.println("LANG: " + targetLanguageCode);
            //translate word

            //write translation to the stream


            // Handle client request
            //RequestHandler requestHandler = new RequestHandler(clientSocket);
            //requestHandler.handleRequest();
        }
    }

    public void sendRequest(String wordToTranslate, Socket clientSocket) {
        try {
            // Send request to client
            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
            out.writeObject(wordToTranslate);
            out.flush();
        } catch (IOException e) {
            System.out.println("Error sending request");
        }
    }

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
