package modelo;

public class Jugador extends Usuario {

    public Jugador(String username, String password) {
        super(username, password);
    }

    @Override
    public String toString() {
        return "Jugador: " + username + " (" + password + ")";
    }

    @Override
    public String toStringInUserFile() {
        return username + ";" + password + ";player;";
    }
}
