package br.uff.ic.lek;


public interface InterfaceLibGDX {
    void enqueueMessage(String querySource, String registrationTime, String authUID, String cmd, String lastUpdateTime);
    void parseCmd(String authUID, String cmd);
    String MY_PLAYER_DATA = "MY_PLAYER_DATA";
    String ALL_PLAYERS_DATA = "ALL_PLAYERS_DATA";
}
