package bgu.spl.net.srv.bidi;

import bgu.spl.net.impl.BGSServer.BidiMessagingProtocolImpl;
import bgu.spl.net.impl.BGSServer.ConnectionsImpl;
import bgu.spl.net.api.bidi.MessageEncoderDecoder;
import bgu.spl.net.api.bidi.Connections;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Supplier;

public abstract class BaseServer<T> implements Server<T> {

    private final int port;
    private final Supplier<BidiMessagingProtocolImpl<T>> protocolFactory;
    private final Supplier<MessageEncoderDecoder<T>> encdecFactory;
    private ServerSocket sock;
    int IdCount;
    Connections<T> connections;

    public BaseServer(
            int port,
            Supplier<BidiMessagingProtocolImpl<T>> protocolFactory,
            Supplier<MessageEncoderDecoder<T>> encdecFactory) {

        this.port = port;
        this.protocolFactory = protocolFactory;
        this.encdecFactory = encdecFactory;
		this.sock = null;
		this.IdCount = 1;
		this.connections = new ConnectionsImpl<>();
    }

    @Override
    public void serve() {

        try (ServerSocket serverSock = new ServerSocket(port)) {
			System.out.println("Server started");

            this.sock = serverSock; //just to be able to close

            while (!Thread.currentThread().isInterrupted()) {

                Socket clientSock = serverSock.accept();

                BidiMessagingProtocolImpl protocol = protocolFactory.get();
                protocol.start(IdCount,connections);
                BlockingConnectionHandler<T> handler = new BlockingConnectionHandler<T>(
                        clientSock,
                        encdecFactory.get(),
                        protocol,
                        IdCount
                );
                execute(handler);
                IdCount++;
            }
        } catch (IOException ex) {
        }

        System.out.println("server closed!!!");
    }

    @Override
    public void close() throws IOException {
		if (sock != null)
			sock.close();
    }

    protected abstract void execute(BlockingConnectionHandler<T>  handler);

}
