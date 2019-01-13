package bgu.spl.net.Message;

import bgu.spl.net.api.bidi.Connections;

public class StatMessage extends Message {
    private String userName;
    private int numPosts;
    private int numFollowers;
    private int numFollowing;
    public StatMessage(int opecode, String userName) {
        super(opecode);
        this.userName = userName;
        this.numFollowers = 0 ;
        this.numFollowing = 0 ;
        this.numPosts = 0;
    }

    @Override
    public void act() {
        if (dataBase.getClientIdName().containsKey(connectId) &&
           dataBase.getClients().containsKey(dataBase.getClientIdName().get(connectId)) &&
           dataBase.getClients().get(dataBase.getClientIdName().get(connectId)).isConnect()&&
           dataBase.getClients().containsKey(userName)){
            numPosts = dataBase.getClients().get(userName).getClientPost().size();
            numFollowing = dataBase.getClients().get(userName).getFollowingList().size();
            numFollowers = dataBase.getClients().get(userName).getFollowList().size();
        }else{
            status = false;
        }
        Complete();
    }

    @Override
    public void Complete() {
        if (status) {
            returnMessage = createACKMessage(opecode) + intToBytes(numPosts) + intToBytes(numFollowers) + intToBytes(numFollowing);
        }else{
            returnMessage = createERORMessage(opecode);
        }
        this.connections.send(connectId,returnMessage);
    }
}
