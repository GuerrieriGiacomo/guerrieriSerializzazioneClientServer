package g1;
import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private static final int porta = 42069; // creo una variabile per assegnare una porta al server
    private static Set<String> clientNames = new HashSet<>(); //array per memorizzare solo i nomi UNIVOCI dei client connessi
    private static Map<String, PrintWriter> clients = new HashMap<>(); // array per memorizzare i nomi dei client e i loro messaggi

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(porta)) { // try per accettare le nuove connessioni, all'arrivo del client il server gli assegna un thread e
            System.out.println("Server in attesa di connessioni...");// esegue il metodo run in clientHandler

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
        // creo delle variabili che conterranno le informazioni del client quando si connette
        private String clientName;
        private Socket socket;
        private BufferedReader reader;
        private PrintWriter writer;

        public ClientHandler(Socket socket) { // costruttore della classe che assegna il socjet
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                // istanzo bufferReader e printWriter per la ricezione e l'invio dei messaggi
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer = new PrintWriter(socket.getOutputStream(), true);

                clientName = reader.readLine();
                if (clientName != null && !clientName.isEmpty() && !clientNames.contains(clientName)) { //controllo che il client inserisca un nome nuovo e non una stringa vuota
                    clientNames.add(clientName); // aggiunge il nome del client all'array
                    clients.put(clientName, writer); // aggiunge il nome e l'outputstream del client all'array di client

                    System.out.println("Connessione effettuata: " + clientName);
                    broadcastMessage("Server", clientName + " si è unito alla chat.");

                    String clientMessage;
                    while (true) { // dopo la connessione del client, il server si mette in attesa dei messaggi del client
                        clientMessage = reader.readLine();
                        if (clientMessage == null || clientMessage.equalsIgnoreCase("esciDalServer")) {
                            break; // se il messaggio è "escidalServer" anche scritto in CAPS esce dal while-treu
                        }

                        if(clientNames.size() > 1){
                            if (clientMessage.startsWith("private")) { // se nel messaggio la prima parola è private, manda il messaggio in
                                handlePrivateMessage(clientMessage);
                            } else {
                                broadcastMessage(clientName, clientMessage);
                            }
                        }else{
                            writer.println("ERRORE: messaggio non inviato (solo un client connesso)");// informa il client che c'è solo un client connesso
                        }
                    }
                } else {
                    writer.println("ERRORE: messaggio non inviato (nome del client gia' in uso)"); //informa il client che il nome del client è gia in uso
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally { //esegue il codice anche se si presenta un'eccezione
                if (clientName != null) {
                    clientNames.remove(clientName);
                    clients.remove(clientName);
                    broadcastMessage("Server", clientName + " ha lasciato la chat.");
                }

                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void handlePrivateMessage(String message) { //metodo per gestire l'invio a un solo client
            String[] parts = message.split(" ", 3);
            if (parts.length == 3) {
                String targetClient = parts[1];
                String privateMessage = parts[2];
                PrintWriter targetWriter = clients.get(targetClient);

                if (targetWriter != null) {
                    targetWriter.println("[Messaggio privato da " + clientName + "]: " + privateMessage);
                } else {
                    writer.println("Il client " + targetClient + " non esiste o non è online.");
                }
            } else {
                writer.println("Formato del messaggio privato non valido.");
            }
        }

        private void broadcastMessage(String sender, String message) { // metodo per mandare il messaggio in broadcast
            for (PrintWriter clientWriter : clients.values()) {
                clientWriter.println("[" + sender + "]: " + message);
            }
        }
    }
}
