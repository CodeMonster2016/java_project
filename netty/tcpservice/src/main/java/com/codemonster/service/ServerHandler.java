package com.codemonster.service;

import org.jboss.netty.channel.*;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by chshi on 1/4/2017.
 */
public class ServerHandler extends SimpleChannelUpstreamHandler {
    private final AtomicLong transferredMessages = new AtomicLong();

    public long getTransferredMessages() {
        return transferredMessages.get();
    }

    @Override
    public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
        if (e instanceof ChannelStateEvent && ((ChannelStateEvent) e).getState() != ChannelState.INTEREST_OPS) {
            System.err.println("server handler" + e);
        }
        super.handleUpstream(ctx, e);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
        // Echo back the received object to the client.
        transferredMessages.incrementAndGet();
        //e.getChannel().write(e.getMessage());
        System.out.println("server channel received #" + transferredMessages.toString() + ":" + e.getMessage());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
        e.getCause().printStackTrace();
        e.getChannel().close();
    }
}
