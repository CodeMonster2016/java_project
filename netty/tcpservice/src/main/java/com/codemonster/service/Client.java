package com.codemonster.service;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.serialization.ClassResolvers;
import org.jboss.netty.handler.codec.serialization.ObjectDecoder;
import org.jboss.netty.handler.codec.serialization.ObjectEncoder;
import org.jboss.netty.handler.ssl.SslContext;
import org.jboss.netty.handler.ssl.util.InsecureTrustManagerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * Created by chshi on 1/4/2017.
 */
public class Client {
    static final boolean SSL = System.getProperty("ssl") != null;
    static final String HOST = System.getProperty("host", "127.0.0.1");
    static final int PORT = Integer.parseInt(System.getProperty("port", "8007"));
    static final int SIZE = Integer.parseInt(System.getProperty("size", "256"));

    public static void main(String[] args) throws Exception {
        // Configure SSL.
        final SslContext sslCtx;
        if (SSL) {
            sslCtx = SslContext.newClientContext(InsecureTrustManagerFactory.INSTANCE);
        } else {
            sslCtx = null;
        }

        // Configure the client.
        ClientBootstrap bootstrap = new ClientBootstrap(
                new NioClientSocketChannelFactory(
                        Executors.newCachedThreadPool(),
                        Executors.newCachedThreadPool()));

        try {
            // Set up the pipeline factory.
            bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
                public ChannelPipeline getPipeline() {
                    ChannelPipeline p = Channels.pipeline(
                            new ObjectEncoder(),
                            new ObjectDecoder(ClassResolvers.cacheDisabled(getClass().getClassLoader())),
                            new ClientHandler(SIZE)
                    );

                    if (sslCtx != null) {
                        p.addFirst("ssl", sslCtx.newHandler(HOST, PORT));
                    }
                    return p;
                }
            });

            // Start the connection attempt.
            ChannelFuture f = bootstrap.connect(new InetSocketAddress(HOST, PORT));

            // Wait until the connection attempt is finished and then the connection is closed.
            f.sync().getChannel().getCloseFuture().sync();
        } finally {
            bootstrap.releaseExternalResources();
        }
    }
}
