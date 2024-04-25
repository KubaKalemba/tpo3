package zad1;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MainServer {

    private static final int PORT = 8080;
    protected static final List<LanguageServer> languageServers = new ArrayList<>();
    private static int currPort = 8081;
    private static Socket clientSocket;

    private ObjectInputStream clientIn;
    private ObjectOutputStream clientOut;

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("[main]: Starting main server on port " + PORT + "...");

        LanguageServer engServer = new LanguageServer("EN", currPort++);
        addServer(engServer);

        while (true) {
            clientSocket = serverSocket.accept();
            System.out.println("[main]: New client connected");
            System.out.flush();

            ObjectInputStream in;
            ObjectOutputStream out;
            try {
                in = new ObjectInputStream(clientSocket.getInputStream());
                out = new ObjectOutputStream(clientSocket.getOutputStream());
            } catch (IOException e) {
                System.out.println("Error creating input/output streams");
                return;
            }

            // Handle client request
            handleRequest(in,out);
        }
    }

    public static void handleRequest(ObjectInputStream clientIn, ObjectOutputStream clientOut) {
        try {
            Request request = (Request) clientIn.readObject();
            String wordToTranslate = request.getWordToTranslate();
            String targetLanguageCode = request.getTargetLanguageCode();
            System.out.println("[main]: WORD: " + wordToTranslate);
            System.out.println("[main]: LANG: " + targetLanguageCode);

            // Find the corresponding language server
            LanguageServer languageServer = findLanguageServer(targetLanguageCode);
            System.out.println(languageServer.getLanguageCode());

            //language server opens and listens on its port
            //languageServer.run();
            Thread langServerThread = new Thread(languageServer);
            langServerThread.start();

            //request is send to the language server
            String translation;
            try (Socket socket = new Socket("localhost", languageServer.getPORT());
                 ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                 ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

                out.writeObject(request);
                out.flush();

                translation = (String) in.readObject();

                //JOptionPane.showMessageDialog(frame, translation);

            } catch (IOException | ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
            //language server returns the translation

            //translation is passed back to client GUI

            // Send request to language server


            // Send response to client
            clientOut.writeObject(translation);
            clientOut.flush();
        } catch (IOException e) {
            System.out.println("Error handling request");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                clientIn.close();
                clientOut.close();
                clientSocket.close();
            } catch (IOException e) {
                System.out.println("Error closing streams");
            }
        }
    }

    public static void addServer(LanguageServer languageServer) {
        languageServers.add(languageServer);
    }

    public static int getCurrPort() {
        return currPort;
    }

    public static void incrementCurrPort() {
        currPort++;
    }

    public static LanguageServer findLanguageServer(String languageCode) {
        for(LanguageServer languageServer : languageServers) {
            if(languageServer.getLanguageCode().equals(languageCode)) {
                return languageServer;
            }
        }
        int currPort = MainServer.getCurrPort();
        MainServer.incrementCurrPort();
        LanguageServer languageServer = new LanguageServer(languageCode, currPort);
        MainServer.addServer(languageServer);
        return languageServer;
    }


}
