package g1;
import java.io.Serializable;

public class Persona implements Serializable {
    private String nome;
    private String cognome;
    private int eta;

    public Persona(String nome, String cognome, int eta) {
        this.nome = nome;
        this.cognome = cognome;
        this.eta = eta;
    }

    @Override
    public String toString() {
        return "Persona [nome=" + nome + ", cognome=" + cognome + ", eta=" + eta + "]";
    }
    
}
