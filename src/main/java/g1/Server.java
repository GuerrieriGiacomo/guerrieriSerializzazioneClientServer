package g1;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private static final int porta = 42069;
    private static Set<String> clientNames = new HashSet<>();
    private static Map<String, PrintWriter> clients = new HashMap<>();
    private static XStream xStream = new XStream(new DomDriver());

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(porta)) {
            System.out.println("Server in attesa di connessioni...");

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Nuova connessione: " + socket);

                Thread thread = new Thread(new ClientHandler(socket));
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler implements Runnable {
        private String clientName;
        private Socket socket;
        private BufferedReader reader;
        private PrintWriter writer;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer = new PrintWriter(socket.getOutputStream(), true);

                // Ricevi oggetto Persona serializzato come stringa XML
                String xmlString = reader.readLine();
                Persona persona = (Persona) xStream.fromXML(xmlString);

                System.out.println("Oggetto ricevuto dal client: " + persona);

                // Esegui ulteriori operazioni con l'oggetto se necessario

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
