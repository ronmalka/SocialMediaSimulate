package bgu.spl.net;

import java.util.concurrent.ConcurrentHashMap;

public class DataBase {
    private ConcurrentHashMap<String, User> Clients;
    private ConcurrentHashMap<Integer, String> ClientIdName;
    private User user;
    private static DataBase ourInstance = new DataBase();

    public static DataBase getInstance() {
        return ourInstance;
    }

    private DataBase() {
        Clients = new ConcurrentHashMap<>();
        ClientIdName = new ConcurrentHashMap();
    }

    public void addUser(int clientId, String clientName,String password){
        user = new User(clientId, clientName, password);
        Clients.put(clientName,user);
    }

    public ConcurrentHashMap<String, User> getClients() {
        return Clients;
    }

    public ConcurrentHashMap<Integer, String> getClientIdName() {
        return ClientIdName;
    }
}
