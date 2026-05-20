package modelo;

/**
 * Representa a un administrador del sistema.
 * <p>
 * Los administradores tienen acceso exclusivo al panel de administración,
 * donde pueden consultar rankings por juego y la lista completa de usuarios
 * con sus últimas partidas. No pueden iniciar partidas.
 * </p>
 */
public class Admin extends Usuario {

    /**
     * Construye un administrador con el nombre de usuario y contraseña indicados.
     *
     * @param username nombre de usuario único.
     * @param password contraseña del administrador.
     */
    public Admin(String username, String password) {
        super(username, password);
    }

    /**
     * Devuelve una representación legible del administrador.
     *
     * @return cadena con el prefijo "Admin:" seguido del username y password.
     */
    @Override
    public String toString() {
        return "Admin: " + username + " (" + password + ")";
    }

    /**
     * Serializa el administrador al formato de línea del fichero {@code usuarios.txt}.
     * <p>Formato: {@code username;password;admin;}</p>
     *
     * @return cadena con los datos del administrador listos para guardar en disco.
     */
    @Override
    public String toStringInUserFile() {
        return username + ";" + password + ";admin;";
    }
}
