package de.uol.swp.server.communication.netty;

import com.google.inject.Inject;
import de.uol.swp.common.message.request.RequestMessage;
import de.uol.swp.server.communication.ServerHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.SocketException;

/**
 * This handler is called from netty when communications occur e.g. a new connection
 * is established or data is received
 *
 * @see io.netty.channel.ChannelInboundHandler
 */

@Sharable
public
class NettyServerHandler extends SimpleChannelInboundHandler<RequestMessage> {

    private static final Logger LOG = LogManager.getLogger(NettyServerHandler.class);
    private final ServerHandler delegate;

    /**
     * Constructor
     *
     * @param delegate handler who handles all communication
     * @see de.uol.swp.server.communication.ServerHandler
     */
    @Inject
    public NettyServerHandler(ServerHandler delegate) {
        this.delegate = delegate;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        delegate.newClientConnected(new NettyMessageContext(ctx));
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, RequestMessage msg) {
        msg.setMessageContext(new NettyMessageContext(ctx));
        delegate.process(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (cause instanceof SocketException && "Connection reset".equals(cause.getMessage())) {
            LOG.info("Client hat die Verbindung getrennt: {}", ctx.channel().remoteAddress());
        } else {
            LOG.error("Unerwarteter Fehler: ", cause);
        }
        ctx.close();
    }

}
