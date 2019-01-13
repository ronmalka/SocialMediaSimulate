package bgu.spl.net.Message;

public class NotifiacationMessage extends Message {
    private byte type;
    private String content;
    private String PostingUserName;
    public NotifiacationMessage(int opecode, byte type, String content, String PostingUserName) {
        super(opecode);
        this.type = type;
        this.content = content;
        this.PostingUserName = PostingUserName;
    }

    @Override
    public void act() {
        byte[] typeArr = new byte[1];
        typeArr[0] = type;
        String typeToSend = new String(typeArr, 0, 1);
        returnMessage = intToBytes(9) + typeToSend + PostingUserName + '\0' + content + '\0';
        connections.send(connectId, returnMessage);
    }

    @Override
    public void Complete() {

    }
}
