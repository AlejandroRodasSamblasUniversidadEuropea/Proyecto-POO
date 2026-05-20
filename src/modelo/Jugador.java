package modelo;

/**
 * Representa a un jugador registrado en la plataforma.
 * <p>
 * Los jugadores pueden iniciar y continuar partidas, así como consultar
 * sus estadísticas personales. Es la subclase de {@link Usuario} con acceso
 * completo a la funcionalidad de juego.
 * </p>
 */
public class Jugador extends Usuario {

    /**
     * Construye un jugador con el nombre de usuario y contraseña indicados.
     *
     * @param username nombre de usuario único.
     * @param password contraseña del jugador.
     */
    public Jugador(String username, String password) {
        super(username, password);
    }

    /**
     * Devuelve una representación legible del jugador.
     *
     * @return cadena con el prefijo "Jugador:" seguido del username y password.
     */
    @Override
    public String toString() {
        return "Jugador: " + username + " (" + password + ")";
    }

    /**
     * Serializa el jugador al formato de línea del fichero {@code usuarios.txt}.
     * <p>Formato: {@code username;password;player;}</p>
     *
     * @return cadena con los datos del jugador listos para guardar en disco.
     */
    @Override
    public String toStringInUserFile() {
        return username + ";" + password + ";player;";
    }
}
