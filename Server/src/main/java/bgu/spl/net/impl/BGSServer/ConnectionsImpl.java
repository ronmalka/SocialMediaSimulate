package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.srv.bidi.ConnectionHandler;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionsImpl<T> implements Connections<T> {
    private ConcurrentHashMap<Integer, ConnectionHandler<T>> connections;
    public ConnectionsImpl(){
        connections = new ConcurrentHashMap<>();
    }
    @Override
    public boolean send(int connectionId, T msg) {
        if(connections.containsKey(connectionId)){
            connections.get(connectionId).send(msg);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void broadcast(T msg) {
        Iterator it = connections.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            ((ConnectionHandler<T>)pair.getValue()).send(msg);
        }
    }

    @Override
    public void disconnect(int connectionId) {
        connections.remove(connectionId);
    }
    public void addConnection(int countId , ConnectionHandler<T> handler){
        connections.put(countId,handler);
    }
    public void removeConnection(int countId){
        connections.remove(countId);
    }
}
