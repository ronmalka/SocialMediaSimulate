package bgu.spl.net.Message;

public class LogoutMessage extends Message {
    public LogoutMessage(int opecode) {
        super(opecode);
    }

    @Override
    public void act() {
        String clientName = dataBase.getClientIdName().get(connectId);
        if (clientName != null && dataBase.getClients().containsKey(clientName)) {
            synchronized (dataBase.getClients().get(clientName)) {
                if (dataBase.getClients().get(clientName).isConnect()) {
                    Complete();
                    connections.disconnect(connectId);
                    dataBase.getClients().get(clientName).disConnect();
                } else {
                    status = false;
                    Complete();
                }
            }
        }else{
            status = false;
            Complete();
        }




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
