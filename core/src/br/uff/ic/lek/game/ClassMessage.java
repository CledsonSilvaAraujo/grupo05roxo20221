package br.uff.ic.lek.game;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;


public class ClassMessage {
    private String cmd;
    private float px;
    private float py;
    private String uID;

    private int cardNumber;

    public ClassMessage(String cmd,float px, float py, int cardNumber) {
        this.cmd  = cmd ;
        this.px = px;
        this.py = py;
        this.cardNumber = cardNumber;
    }

    public ClassMessage() { }

    public static String encodeCurrentPos(ClassMessage obj){
        Json jsonParser = new Json();
        jsonParser.addClassTag("ClassMesage", ClassMessage.class);
        String myJSON = jsonParser.toJson(obj);
        System.out.println("dentro encode: "+myJSON);
        return myJSON;
    }

    public static Object decodeCurrentPos(String json){
        Json jsonParser = new Json();

        jsonParser.setTypeName("class");
        jsonParser.setUsePrototypes(false);
        jsonParser.setIgnoreUnknownFields(true);
        jsonParser.setOutputType(JsonWriter.OutputType.json);
        jsonParser.addClassTag("ClassMessage", ClassMessage.class);

        Object obj = jsonParser.fromJson(ClassMessage.class, json);

        return obj;
    }

    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }

    public int getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(int cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCmd() {
        return cmd ;
    }

    public void setCmd(String cmd) {
        this.cmd  = cmd ;
    }

    public float getPx() {
        return px;
    }
    public void setPx(float px) {
        this.px = px;
    }
    public float getPy() {
        return py;
    }
    public void setPy(float py) {
        this.py = py;
    }
}
