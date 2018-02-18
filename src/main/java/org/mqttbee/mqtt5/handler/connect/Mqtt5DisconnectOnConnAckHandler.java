package org.mqttbee.mqtt5.handler.connect;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.mqttbee.annotations.NotNull;
import org.mqttbee.api.mqtt5.exception.Mqtt5MessageException;
import org.mqttbee.api.mqtt5.message.disconnect.Mqtt5DisconnectReasonCode;
import org.mqttbee.mqtt5.handler.disconnect.Mqtt5DisconnectUtil;
import org.mqttbee.mqtt5.message.connect.connack.Mqtt5ConnAckImpl;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Sends a DISCONNECT message if a CONNACK message is received. This handler is added after the first CONNACK
 * message is received, so it disconnects on further CONNACK messages.
 *
 * @author Silvio Giebl
 */
@ChannelHandler.Sharable
@Singleton
public class Mqtt5DisconnectOnConnAckHandler extends ChannelInboundHandlerAdapter {

    public static final String NAME = "disconnect.on.connack";

    @Inject
    Mqtt5DisconnectOnConnAckHandler() {
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) {
        if (msg instanceof Mqtt5ConnAckImpl) {
            readConnAck(ctx, (Mqtt5ConnAckImpl) msg);
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    private void readConnAck(@NotNull final ChannelHandlerContext ctx, @NotNull final Mqtt5ConnAckImpl connAck) {
        Mqtt5DisconnectUtil.disconnect(ctx.channel(), Mqtt5DisconnectReasonCode.PROTOCOL_ERROR,
                new Mqtt5MessageException(connAck, "Must not receive second CONNACK"));
    }

}
