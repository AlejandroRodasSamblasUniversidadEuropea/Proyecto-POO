package modelo;

public class Admin extends Usuario {

    public Admin(String username, String password) {
        super(username, password);
    }

    @Override
    public String toString() {
        return "Admin: " + username + " (" + password + ")";
    }

    @Override
    public String toStringInUserFile() {
        return username + ";" + password + ";admin;";
    }
}
