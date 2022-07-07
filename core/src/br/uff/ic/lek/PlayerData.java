package br.uff.ic.lek;

import com.badlogic.gdx.math.MathUtils;

import java.sql.Timestamp;


public class PlayerData {
    public String nickName;
    public States gameState;
    public String cmd;
    public String registrationTime;
    public String updateTime;

    public enum States {
        WAITING,
        READYTOPLAY,
        PLAYING
    };

    public PlayerData() { }
}
