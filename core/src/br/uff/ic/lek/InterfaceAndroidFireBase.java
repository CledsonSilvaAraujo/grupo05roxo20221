package br.uff.ic.lek;

import br.uff.ic.lek.actors.Avatar;

public interface InterfaceAndroidFireBase {
    String getDeviceId();
    void setLibGDXScreen(InterfaceLibGDX libGDXScreen);
    void writePlayerData(Avatar player);
    void writePartyData(Avatar player);
    void waitForMyMessages();
    void waitForPlayers(String party);
    void finishAndRemoveTask();
    void signIn(String email,String username);
}
