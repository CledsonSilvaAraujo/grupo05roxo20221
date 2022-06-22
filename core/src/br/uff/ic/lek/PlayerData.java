package br.uff.ic.lek;

import static com.badlogic.gdx.math.MathUtils.random;

import com.badlogic.gdx.math.MathUtils;

import java.sql.Timestamp;

public class PlayerData {
    String authUID;
    String writerUID;
    States gameState;
    String chat;
    String cmd;
    String email;
    String playerNickName;
    String lastUpdateTime;
    String registrationTime;
    String stateAndLastTime;
    int runningTimes;
    String avatarType;
    Object cmdObj;

    public String getAvatarType() {
        return avatarType;
    }

    public void setAvatarType(String avatarType) {
        this.avatarType = avatarType;
    }

    public String getStateAndLastTime() {
        return stateAndLastTime;
    }

    public void setStateAndLastTime(String stateAndLastTime) {
        this.stateAndLastTime = stateAndLastTime;
    }

    public int getRandomNumber() {
        return randomNumber;
    }

    public void setRandomNumber(int randomNumber) {
        this.randomNumber = randomNumber;
    }

    int randomNumber;

    private static PlayerData pd=null;
    private PlayerData(){ // singleton
    }
    public static PlayerData myPlayerData(){
        if (PlayerData.pd == null){
            MathUtils.random.setSeed(System.currentTimeMillis());
            PlayerData.pd = new PlayerData();
        }
        PlayerData.pd.randomNumber = MathUtils.random(1, Integer.MAX_VALUE);
        return PlayerData.pd; // singleton = sempre a mesma instancia
    }

    public int getRunningTimes() {
        return runningTimes;
    }

    public void setRunningTimes(int runningTimes) {
        this.runningTimes = runningTimes;
    }

    Timestamp timestamp;
    public enum States {
        WAITING,
        READYTOPLAY,
        LETSPLAY,
        PLAYING
    };

    public String getWriterUID() {
        return writerUID;
    }

    public void setWriterUID(String writerUID) {
        this.writerUID = writerUID;
    }

    public States getGameState() {
        return gameState;
    }

    public void setGameState(States gameState) {
        this.gameState = gameState;
    }

    public String getRegistrationTime() {
        return registrationTime;
    }

    public void setRegistrationTime(String registrationTime) {
        this.registrationTime = registrationTime;
    }

    public String getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAuthUID() {
        return authUID;
    }

    public void setAuthUID(String authUID) {
        this.authUID = authUID;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getChat() {
        return chat;
    }

    public void setChat(String chat) {
        this.chat = chat;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }
    public void setCmdObj(Object cmd) {
        this.cmdObj  = cmd;
    }

    public void setPlayerNickName(String playerNickName) {
        this.playerNickName = playerNickName;
    }

    public String getPlayerNickName() {
        return playerNickName;
    }
}
