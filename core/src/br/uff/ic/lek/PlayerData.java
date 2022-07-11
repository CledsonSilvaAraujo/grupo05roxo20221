package br.uff.ic.lek;

import com.badlogic.gdx.math.MathUtils;

import java.sql.Timestamp;


public class PlayerData {
    public String username;
    public States gameState;
    public String cmd;
    public String party;
    public String registrationTime;
    public String updateTime;

    public enum States {
        WAITING,
        READY,
        PLAYING
    };

    public PlayerData() { }
}
