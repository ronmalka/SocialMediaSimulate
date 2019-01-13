package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.Message.Message;
import bgu.spl.net.api.bidi.MessageEncoderDecoder;
import bgu.spl.net.srv.bidi.Server;

import java.util.function.Supplier;

public class TPCMain {
    public static void main(String[] args){
        int port = Integer.parseInt(args[0]);
        Supplier<BidiMessagingProtocolImpl<Message>> protocolFactory = new Supplier<BidiMessagingProtocolImpl<Message>>() {
            @Override
            public BidiMessagingProtocolImpl<Message> get() {
                return new BidiMessagingProtocolImpl<>();
            }
        };
        Supplier<MessageEncoderDecoder<Message>> decoderSupplier = new Supplier<MessageEncoderDecoder<Message>>() {
            @Override
            public MessageEncoderDecoder<Message> get() {
                return new LineMessageEncoderDecoder();
            }
        };

        Server<Message> serverTPC = Server.threadPerClient(port,protocolFactory,decoderSupplier);
        serverTPC.serve();
    }
}
