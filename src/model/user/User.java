package model.user;

import java.io.Serializable;
import java.time.LocalDate;
import model.stats.PlayerStats;

public class User implements Serializable{
    private String username;
    private String password;
    private LocalDate registrationDate;
    private PlayerStats stats;

    public User(String username, String password, String passwordAgain){
        this.username=username;
        if(password.equals(passwordAgain)) this.password=password;
        this.registrationDate=LocalDate.now();
        stats = new PlayerStats();
    }

    //Se devuelve en username.
    public String getUsername(){return username;}
    //Se devuelve las stats del usuario.
    public PlayerStats getStats(){return stats;}
    //Se devuelve la fecha de registro.
    public LocalDate getRegistrationDate(){return registrationDate;}
    //Devuelve la contraseña.
    public String getPassword(){return password;}

    //Se cambia la contraseña solo si ingresa correctamente la contraseña original.
    public void setPassword(String oldPassword, String newPassword){
        if(this.password.equals(oldPassword))
            this.password=newPassword;
    }

    //Se compara la contraseña ingresada con la contraseña almacenada y se devuelve un boolean.
    public boolean checkPassword(String password){return this.password.equals(password);}
    
    //Se verifica si el usuario es admin.
    public boolean isAdmin(){return false;}

    @Override
    public String toString(){
        return "User[" + (username != null ? "username: " + username : "")+ "," + "Stats: " + stats + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        User other = (User) obj;
        if(username==null) {
            if(other.username!=null)
                return false;
        }else if (!username.equals(other.username))
                return false;
        return true;
    }

    @Override
    public int hashCode() {
        return username != null ? username.hashCode() : 0;
    }
}
