package com.codemonster.service;

import org.jboss.netty.channel.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by chshi on 1/4/2017.
 */
public class ClientHandler extends SimpleChannelUpstreamHandler {
    private final List<Integer> firstMessage;
    private final AtomicLong transferredMessages = new AtomicLong();

    /**
     * Creates a client-side handler.
     */

    public ClientHandler(int firstMessageSize) {
        if (firstMessageSize <= 0) {
            throw new IllegalArgumentException("firstMessageSize: " + firstMessageSize);
        }
        firstMessage = new ArrayList<Integer>(firstMessageSize);
        for (int i = 0; i < firstMessageSize; i++) {
            firstMessage.add(i);
        }
    }


    public long getTransferredMessages() {
        return transferredMessages.get();
    }


    @Override
    public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
        if (e instanceof ChannelStateEvent &&
                ((ChannelStateEvent) e).getState() != ChannelState.INTEREST_OPS) {
            System.err.println("client handler" + e);
        }
        super.handleUpstream(ctx, e);
    }


    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
        // Send the first message if this handler is a client-side handler.
        e.getChannel().write(firstMessage);
        System.out.println("client channel connected" + firstMessage.toString());
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
        // Echo back the received object to the server.
        transferredMessages.incrementAndGet();
        e.getChannel().write(e.getMessage());
        System.out.println("client channel received #" + transferredMessages.toString() + ":" + e.getMessage());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
        e.getCause().printStackTrace();
        e.getChannel().close();
    }
}
