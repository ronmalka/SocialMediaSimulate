package bgu.spl.net.Message;

import bgu.spl.net.api.bidi.Connections;

public class RegisterMessage extends Message {
    private String clientName;
    private String password;
    public RegisterMessage( int opecode, String clientName, String password ){
        super(opecode);
        this.clientName = clientName;
        this.password = password;
    }

    @Override
    public void act() {
        if (!dataBase.getClients().containsKey(clientName) && !dataBase.getClientIdName().containsKey(connectId)) {
            dataBase.addUser(this.connectId, this.clientName, this.password);
            dataBase.getClientIdName().put(connectId,clientName);
        }else {
            this.status = false;
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
        connections.send(connectId, returnMessage);

    }
}
