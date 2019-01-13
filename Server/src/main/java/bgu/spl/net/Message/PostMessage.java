package bgu.spl.net.Message;

import bgu.spl.net.api.bidi.Connections;

import java.util.LinkedList;

public class PostMessage extends Message {
    private String content;
    private LinkedList<String> userNames;
    public PostMessage(int opecode, String content, LinkedList<String> userNames) {
        super(opecode);
        this.content = content;
        this.userNames = userNames;

    }

    @Override
    public void act() {
        if (dataBase.getClientIdName().containsKey(connectId) &&
            dataBase.getClients().containsKey(dataBase.getClientIdName().get(connectId)) &&
            dataBase.getClients().get(dataBase.getClientIdName().get(connectId)).isConnect()){
            for (String name : userNames){
                if (!dataBase.getClients().get(dataBase.getClientIdName().get(connectId)).getFollowingList().contains(name)){
                  Message message = new NotifiacationMessage(9, (byte) 1, content, dataBase.getClientIdName().get(connectId));
                  message.setConnectId(dataBase.getClients().get(name).getCilentId());
                  message.setConnections(connections);
                   synchronized (dataBase.getClients().get(name)) {
                        if (dataBase.getClients().get(name).isConnect()) {
                            message.act();
                        } else {
                            dataBase.getClients().get(name).setPostsToRead((NotifiacationMessage) message);
                        }
                    }
                }
            }
            for (String name : dataBase.getClients().get(dataBase.getClientIdName().get(connectId)).getFollowingList()){
                Message message = new NotifiacationMessage(9, (byte) 1, content, dataBase.getClientIdName().get(connectId));
                message.setConnectId(dataBase.getClients().get(name).getCilentId());
                message.setConnections(connections);
                synchronized (dataBase.getClients().get(name)) {
                    if (dataBase.getClients().get(name).isConnect()) {
                        message.act();
                    } else {
                        dataBase.getClients().get(name).setPostsToRead((NotifiacationMessage) message);
                    }
                }

            }
            dataBase.getClients().get(dataBase.getClientIdName().get(connectId)).setClientPost(content);
        }else{
            status = false;
        }
        Complete();
    }

    @Override
    public void Complete() {
        if (status){
            returnMessage = createACKMessage(opecode);
        }else{
            returnMessage = createERORMessage(opecode);
        }
        this.connections.send(connectId,returnMessage);
    }
}
