package zad1;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainServer {

    private static final int PORT = 8080;
    protected static final List<LanguageServer> languageServers = new ArrayList<>();
    private static int currPort = 8081;
    private static Socket clientSocket;
    private static final Map<LanguageServer, Thread> threadMap = new HashMap<>();

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
                System.out.println("Error creating streams!");
                return;
            }
            handleRequest(in,out);
        }
    }

    public static void handleRequest(ObjectInputStream clientIn, ObjectOutputStream clientOut) {
        try {
            Request request = (Request) clientIn.readObject();
            System.out.println("[main]: WORD: " + request.getWordToTranslate());
            System.out.println("[main]: LANG: " + request.getTargetLanguageCode());

            LanguageServer languageServer = findLanguageServer(request.getTargetLanguageCode());
            if(!threadMap.containsKey(languageServer)) {
                Thread langServerThread = new Thread(languageServer);
                langServerThread.start();
                threadMap.put(languageServer, langServerThread);
            } else if(!threadMap.get(languageServer).isAlive()) {
                Thread langServerThread = new Thread(languageServer);
                langServerThread.start();
            }

            String translation;
            System.out.println("Main server connecting to lang server on port " + languageServer.getPORT() + "...");
            try (Socket socket = new Socket("localhost", languageServer.getPORT());
                 ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                 ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

                out.writeObject(request);
                out.flush();

                translation = (String) in.readObject();

            } catch (IOException | ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
            clientOut.writeObject(translation);
            clientOut.flush();
        } catch (IOException e) {
            System.out.println("Error! " + e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                clientIn.close();
                clientOut.close();
                clientSocket.close();
            } catch (IOException e) {
                System.out.println("Error! " + e);
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
