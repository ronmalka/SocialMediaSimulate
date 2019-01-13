package bgu.spl.net.srv.bidi;

import bgu.spl.net.impl.BGSServer.BidiMessagingProtocolImpl;
import bgu.spl.net.impl.BGSServer.ConnectionsImpl;
import bgu.spl.net.api.bidi.MessageEncoderDecoder;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

public class BlockingConnectionHandler<T> implements Runnable, ConnectionHandler<T> {

    private final BidiMessagingProtocolImpl<T> protocol;
    private final MessageEncoderDecoder<T> encdec;
    private final Socket sock;
    private BufferedInputStream in;
    private BufferedOutputStream out;
    private volatile boolean connected = true;
    private int connectionId;

    public BlockingConnectionHandler(Socket sock, MessageEncoderDecoder<T> reader, BidiMessagingProtocolImpl<T> protocol, int Id) {
        this.sock = sock;
        this.encdec = reader;
        this.protocol = protocol;
        connectionId = Id;
        ((ConnectionsImpl)protocol.getConnections()).addConnection(connectionId, this);
    }

    @Override
    public void run() {
        try (Socket sock = this.sock) { //just for automatic closing
            int read;

            in = new BufferedInputStream(sock.getInputStream());
            out = new BufferedOutputStream(sock.getOutputStream());

            while (!protocol.shouldTerminate() && connected && (read = in.read()) >= 0) {
                T nextMessage = encdec.decodeNextByte((byte) read);
                if (nextMessage != null) {
                    protocol.process(nextMessage);
                }
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public void close() throws IOException {
        connected = false;
        sock.close();
    }

    @Override
    public void send(T msg) {
        try {
            out.write(((String)msg).getBytes());
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
