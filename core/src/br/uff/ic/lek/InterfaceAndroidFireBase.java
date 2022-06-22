package br.uff.ic.lek;

import br.uff.ic.lek.actors.Avatar;

public interface InterfaceAndroidFireBase {
    void setLibGDXScreen(InterfaceLibGDX libGDXScreen);
    void writePlayerData(Avatar player);
    void waitForMyMessages();
    void waitForPlayers();
    void finishAndRemoveTask();
}
