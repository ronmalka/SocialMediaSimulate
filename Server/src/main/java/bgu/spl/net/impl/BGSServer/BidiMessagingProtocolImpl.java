package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.DataBase;
import bgu.spl.net.Message.Message;
import bgu.spl.net.api.bidi.BidiMessagingProtocol;
import bgu.spl.net.api.bidi.Connections;

public class BidiMessagingProtocolImpl<T> implements BidiMessagingProtocol<T> {
    private Connections connections;
    private int connectiontId;
    private boolean shouldTerminate;
    private DataBase dataBase;
    public BidiMessagingProtocolImpl(){
        connectiontId=-1;
        connections=null;
        shouldTerminate=false;
        dataBase = DataBase.getInstance();
    }
    @Override
    public void start(int connectionId, Connections connections) {
        this.connections=connections;
        this.connectiontId=connectionId;

    }

    @Override
    public void process(Object message) {
        Message castedMessage = (Message)message;
        castedMessage.setConnectId(connectiontId);
        castedMessage.setConnections(connections);
        castedMessage.act();
    }

    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }

    public Connections getConnections() {
        return connections;
    }
}
