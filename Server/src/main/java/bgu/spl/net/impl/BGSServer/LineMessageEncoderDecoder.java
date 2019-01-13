package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.Message.*;
import bgu.spl.net.api.bidi.MessageEncoderDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedList;

public class LineMessageEncoderDecoder implements MessageEncoderDecoder<Message> {

    private short opeCode = 0;
    private byte[] bytes = new byte[1 << 10]; //start with 1k
    private int len = 0;
    private int zeroCount = 0;
    private int excpectedZeroes = 0;
    private int byteCount = 0;
    private LinkedList<String> arguments = new LinkedList<>();
    private boolean follow;

    @Override
    public Message decodeNextByte(byte nextByte) {
        //notice tha the top 128 ascii characters have the same representation as their utf-8 counterparts
        //this allow us to do the following comparison
        if(byteCount == 0){
            opeCode = (short)((nextByte & 0xff) << 8);
        }
        if(byteCount == 1){
            opeCode += (short)((nextByte & 0xff));
            if(opeCode == 3 || opeCode ==7) return popMessage();
        }
        if(byteCount == 2){
            if(opeCode == 1 || opeCode == 2 || opeCode == 6) excpectedZeroes = 2;
            if(opeCode == 3 || opeCode == 7 || opeCode == 8 || opeCode == 5) excpectedZeroes = 1;
            if(opeCode == 4 && nextByte == 1) follow = true;
            else if(opeCode == 4 && nextByte == 0) follow = false;
        }
        if (byteCount == 3 && opeCode == 4){
            excpectedZeroes += (nextByte & 0xff) << 8;
        }
        if (byteCount == 4 && opeCode == 4){
            excpectedZeroes += (nextByte & 0xff);
        }
        if (nextByte == 0 && byteCount >= 2 && !(opeCode == 4 && byteCount < 5)) {
            pushArgument();
            zeroCount++;
            if(zeroCount == excpectedZeroes){
                return popMessage();
            }
        }
        if(nextByte != 0 && byteCount >= 2 && !(opeCode == 4 && byteCount < 5)){
            pushByte(nextByte);
        }
        byteCount++;
        return null; //not a line yet
    }

    @Override
    public byte[] encode(Message message) {
        return (message + "\n").getBytes(); //uses utf8 by default
    }

    private void pushByte(byte nextByte) {
        if (len >= bytes.length) {
            bytes = Arrays.copyOf(bytes, len * 2);
        }
        bytes[len++] = nextByte;
    }

    private Message popMessage() {
        //notice that we explicitly requesting that the string will be decoded from UTF-8
        //this is not actually required as it is the default encoding in java.
        //MESSAGE pasring!
        Message output = null;
        LinkedList<String> users = new LinkedList<>();
        if(opeCode == 5){
            String contetCopy = arguments.get(0) + "";
            while(contetCopy.indexOf('@') >= 0){
                contetCopy = contetCopy.substring(contetCopy.indexOf('@')+1);
                if(contetCopy.indexOf(' ') >0)
                    users.add(contetCopy.substring(0, contetCopy.indexOf(' ')));
                else
                    users.add(contetCopy);
            }
        }
        switch (opeCode){
            case 1: output = new RegisterMessage(opeCode, arguments.get(0), arguments.get(1));
                    break;
            case 2: output = new LoginMessage(opeCode, arguments.get(0), arguments.get(1));
                break;
            case 3: output = new LogoutMessage(opeCode);
                break;
            case 4: output = new FollowMessage(opeCode,follow, new LinkedList<>(arguments));
                break;
            case 5: output = new PostMessage(opeCode, arguments.get(0), users);
                break;
            case 6: output = new PmMessage(opeCode, arguments.get(1), arguments.get(0));
                break;
            case 7: output = new UserListMessage(opeCode);
                break;
            case 8: output = new StatMessage(opeCode, arguments.get(0));
                break;

        }
        byteCount = 0;
        arguments.clear();
        excpectedZeroes = 0;
        zeroCount = 0;
        opeCode = 0;
        return output;
    }

    private void pushArgument(){
        arguments.add(new String(bytes, 0, len, StandardCharsets.UTF_8));
        len = 0;
        bytes = new byte[1 << 10];
    }
}
