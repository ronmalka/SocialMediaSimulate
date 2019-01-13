package bgu.spl.net.Message;

import bgu.spl.net.api.bidi.Connections;

import java.util.LinkedList;


public class FollowMessage extends Message {
    private boolean follow;
    private byte byteFollow;
    private int followSucces;
    private String followname;
    private LinkedList<String> followList;
    public FollowMessage(int opecode, boolean follow, LinkedList<String> followList) {
        super(opecode);
        this.follow = follow;
        this.followList = followList;
        followSucces = 0 ;
        followname = "";
    }

    @Override
    public void act() {
        if (dataBase.getClientIdName().containsKey(connectId) &&
            dataBase.getClients().get(dataBase.getClientIdName().get(connectId)).isConnect()) {
            if (follow) {
                byteFollow = 0;
                for (int i = 0; i < followList.size(); i++) {
                    if (dataBase.getClients().containsKey(followList.get(i)) &&
                        !dataBase.getClients().get(dataBase.getClientIdName().get(connectId)).getFollowList().contains(followList.get(i))) {
                        dataBase.getClients().get(dataBase.getClientIdName().get(connectId)).getFollowList().add(followList.get(i));
                        dataBase.getClients().get(followList.get(i)).getFollowingList().add(dataBase.getClientIdName().get(connectId));
                        followSucces++;
                        followname = followname + followList.get(i) + '\0';
                    }
                }
            }else{
                byteFollow = 1;
                for (int i = 0; i < followList.size(); i++) {
                    if (dataBase.getClients().containsKey(followList.get(i)) &&
                        dataBase.getClients().get(dataBase.getClientIdName().get(connectId)).getFollowList().contains(followList.get(i))) {
                        dataBase.getClients().get(dataBase.getClientIdName().get(connectId)).getFollowList().remove(followList.get(i));
                        dataBase.getClients().get(followList.get(i)).getFollowingList().remove(dataBase.getClientIdName().get(connectId));
                        followSucces++;
                        followname = followname + followList.get(i) + '\0';
                    }
                }
            }
            if (followSucces == 0){
                status = false;
            }
        }else{status = false;}
        Complete();
    }

    @Override
    public void Complete() {
        if (status){
            returnMessage = createACKMessage(opecode)  + intToBytes(followSucces) +  followname;
        }else{
            returnMessage = createERORMessage(opecode);
        }
        this.connections.send(connectId,returnMessage);
    }
}

