package model.user;

import java.io.IOException;
import java.io.Serializable;
import model.stats.PlayerStats;

public class AdminUser extends User implements Serializable{

    public AdminUser(String username, String password, String passwordAgain){
        super(username, password, passwordAgain);
    }

    public void getAllUsersStats(){}

    public void getGameRanking(){}

    @Override
    public boolean isAdmin(){return true;}
    
}
