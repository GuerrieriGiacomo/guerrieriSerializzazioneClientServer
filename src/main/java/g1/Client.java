package g1;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import java.io.*;
import java.net.Socket;

public class Client {
    private static XStream xStream = new XStream(new DomDriver());

    public static void main(String[] args) {
        String serverAddress = "localhost"; // Inserisci l'indirizzo IP o il nome host del server
        int serverPort = 42069;

        try (Socket socket = new Socket(serverAddress, serverPort);
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream())) {

            // Crea un oggetto Persona
            Persona persona = new Persona("Mario", "Rossi", 30);

            // Serializza l'oggetto Persona in una stringa XML
            String xmlString = xStream.toXML(persona);

            // Invia la stringa XML al server
            objectOutputStream.writeObject(xmlString);

            System.out.println("Oggetto inviato al server: " + persona);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
