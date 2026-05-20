package modelo;

public abstract class Usuario {
    protected String username;
    protected String password;

    public Usuario(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean comprobarPassword(String password) {
        return this.password.equals(password);
    }

    public abstract String toString();

    // Formato para guardar en usuarios.txt: username;password;tipo;
    public abstract String toStringInUserFile();
}
