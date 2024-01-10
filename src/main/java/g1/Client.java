package g1;
import java.io.*;
import java.net.*;

public class Client {
    private static final String ipServer = "localhost"; // creo una variabile contenente l'ip a cui il clinet si colegherà 
    private static final int  portaServer = 42069; // creo una variabile per assegnare una porta al server

    public static void main(String[] args) {
        try (Socket socket = new Socket(ipServer,  portaServer);

            // istanzo bufferReader e printWriter per la ricezione e l'invio dei messaggi
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in))) {
            //client chiede il nome che mandera al server
            System.out.println("Inserisci il tuo nome:");
            String clientName = consoleReader.readLine();
            writer.println(clientName);
            //legge la risposta del server
            String serverResponse = reader.readLine();
            System.out.println(serverResponse);

            if (serverResponse.equals("messaggio non inviato")) {
                return;
            }
            //arrow function che crea un thread e legge l'imput dalla console  per mandarlo al writer
            Thread inputThread = new Thread(() -> {
                try {
                    String inputLine;
                    while ((inputLine = consoleReader.readLine()) != null) {
                        writer.println(inputLine);
                        if (inputLine.equalsIgnoreCase("esciDalServer")) {
                            break;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            //arrow function che crea un thread x la lettura del bufferReader
            Thread outputThread = new Thread(() -> {
                try {
                    String serverMessage;
                    while ((serverMessage = reader.readLine()) != null) {
                        System.out.println(serverMessage);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            // avvio dei thread
            inputThread.start();
            outputThread.start();
            // join dei thread
            inputThread.join();
            outputThread.join();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
