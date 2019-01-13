package bgu.spl.net.Message;

import bgu.spl.net.api.bidi.Connections;


public class PmMessage extends Message  {
    private String content;
    private String userName;
    public PmMessage(int opecode, String content, String userName) {
        super(opecode);
        this.content = content;
        this.userName = userName;

    }

    @Override
    public void act() {
        if (dataBase.getClientIdName().containsKey(connectId) &&
            dataBase.getClients().containsKey(dataBase.getClientIdName().get(connectId)) &&
            dataBase.getClients().get(dataBase.getClientIdName().get(connectId)).isConnect() &&
            dataBase.getClients().containsKey(userName)){
                    Message message = new NotifiacationMessage(9, (byte) 0, content, dataBase.getClientIdName().get(connectId));
                    message.setConnectId(dataBase.getClients().get(userName).getCilentId());
                    message.setConnections(connections);
                    synchronized (dataBase.getClients().get(userName)) {
                        if (dataBase.getClients().get(userName).isConnect()) {
                            message.act();
                        } else {
                            dataBase.getClients().get(userName).setPostsToRead((NotifiacationMessage) message);
                        }
                    }
            dataBase.getClients().get(dataBase.getClientIdName().get(connectId)).setClientPost(content);
        }else {
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
