package bgu.spl.net.Message;

import bgu.spl.net.api.bidi.Connections;

public class LoginMessage extends Message {
    String userName;
    String password;
    public LoginMessage(int opecode,String userName, String password) {
        super(opecode);
        this.password = password;
        this.userName = userName;
    }

    @Override
    public void act() {
        if(dataBase.getClients().containsKey(userName)) {
            synchronized (dataBase.getClients().get(userName)) {
                if (dataBase.getClients().get(userName).getPassword().equals(password) && !dataBase.getClients().get(userName).isConnect()) {
                    dataBase.getClients().get(userName).Connect();
                    if (dataBase.getClients().get(userName).getCilentId() != connectId) {
                            dataBase.getClientIdName().remove(dataBase.getClients().get(userName).getCilentId());
                            dataBase.getClients().get(userName).setCilentId(connectId);
                            dataBase.getClientIdName().put(connectId, userName);
                    }
                    while (dataBase.getClients().get(userName).getCountPosts() < dataBase.getClients().get(userName).getPostsToRead().size()) {
                        NotifiacationMessage message = dataBase.getClients().get(userName).getPostsToRead().get(dataBase.getClients().get(userName).getCountPosts());
                        message.setConnectId(connectId);
                        message.act();
                        dataBase.getClients().get(userName).increaseCountPosts();
                    }
                } else {
                    status = false;
                }
            }
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
