package bgu.spl.net.Message;

import bgu.spl.net.api.bidi.Connections;

import java.util.Iterator;
import java.util.Map;

public class UserListMessage extends Message {
    private String userList;
    public UserListMessage(int opecode) {
        super(opecode);
        userList = "";
    }

    @Override
    public void act() {
        if (dataBase.getClientIdName().containsKey(connectId) &&
            dataBase.getClients().get(dataBase.getClientIdName().get(connectId)).isConnect()) {
            Iterator it = dataBase.getClientIdName().entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                userList = userList + pair.getValue() + '\0';
            }
        } else {
            status = false;
        }

        Complete();
    }
    @Override
    public void Complete(){
        if (status){
            returnMessage = createACKMessage(opecode) + intToBytes(dataBase.getClientIdName().size()) + userList;
        }else{
            returnMessage = createERORMessage(opecode);
        }
        this.connections.send(connectId,returnMessage);
    }

    }

