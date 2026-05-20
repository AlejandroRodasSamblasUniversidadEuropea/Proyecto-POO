package modelo;

/**
 * Clase abstracta que representa a cualquier usuario del sistema.
 * <p>
 * Define los atributos y comportamientos comunes a todos los tipos de usuario
 * (jugador y administrador). No puede instanciarse directamente.
 * </p>
 *
 * <p>Subclases concretas:</p>
 * <ul>
 *   <li>{@link Jugador} — puede iniciar y jugar partidas.</li>
 *   <li>{@link Admin} — accede al panel de administración y rankings.</li>
 * </ul>
 */
public abstract class Usuario {

    /** Nombre de usuario único en el sistema. */
    protected String username;

    /** Contraseña del usuario (texto plano). */
    protected String password;

    /**
     * Construye un nuevo usuario con las credenciales indicadas.
     *
     * @param username nombre de usuario único.
     * @param password contraseña del usuario.
     */
    public Usuario(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * Devuelve el nombre de usuario.
     *
     * @return el nombre de usuario.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Devuelve la contraseña del usuario.
     *
     * @return la contraseña.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Comprueba si la contraseña proporcionada coincide con la del usuario.
     *
     * @param password contraseña a verificar.
     * @return {@code true} si la contraseña es correcta; {@code false} en caso contrario.
     */
    public boolean comprobarPassword(String password) {
        return this.password.equals(password);
    }

    /**
     * Devuelve una representación legible del usuario.
     *
     * @return cadena con información del usuario.
     */
    public abstract String toString();

    /**
     * Serializa el usuario al formato de línea del fichero {@code usuarios.txt}.
     * <p>Formato: {@code username;password;tipo;}</p>
     *
     * @return cadena con los datos del usuario listos para guardar en disco.
     */
    public abstract String toStringInUserFile();
}
