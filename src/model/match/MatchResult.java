package model.match;

import java.io.Serializable;
import java.time.LocalDateTime;

public class MatchResult implements Serializable{
    private String gameType;
    private int score;
    private LocalDateTime date;
    private boolean won;
    private UUID matchId;
}
