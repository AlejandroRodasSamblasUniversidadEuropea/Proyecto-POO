package modelo;

public abstract class Juego {
    protected String nombre;

    public Juego(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    // Cada juego concreto decide como se juega y que hay que hacer para iniciar una partida
    public abstract Partida iniciarPartida(Jugador[] jugadores);
}
