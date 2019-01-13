package bgu.spl.net.Message;

import bgu.spl.net.DataBase;
import bgu.spl.net.api.bidi.Connections;

public abstract class Message{
    protected String returnMessage;
    protected int opecode;
    protected int connectId;
    protected DataBase dataBase;
    protected int countPosts;
    protected boolean status;
    protected Connections connections;


    public Message(int opecode) {
        this.opecode = opecode;
        returnMessage = null;
        dataBase = DataBase.getInstance();
        countPosts=0;
        status=true;
    }

    public void setConnectId(int connectId) {
        this.connectId = connectId;
    }

    public void setConnections(Connections connections) {
        this.connections = connections;
    }

    String intToBytes(int opecode){
        byte[] bytes = new byte[2];
        bytes[0] = (byte)((opecode >> 8) & 0xFF);
        bytes[1] = (byte)(opecode & 0xFF);
        String output = new String(bytes, 0, bytes.length);
        return output;
    }

    String createACKMessage(int messageOpecode){
        String output = intToBytes(10) + intToBytes(messageOpecode);
        return output;
    }
    String createERORMessage(int messageOpecode){
        String output = intToBytes(11) + intToBytes(messageOpecode);
        return output;
    }


    public abstract void act();
    public abstract void Complete();

}
