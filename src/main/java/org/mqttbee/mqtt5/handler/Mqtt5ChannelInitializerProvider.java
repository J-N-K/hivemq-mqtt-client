package org.mqttbee.mqtt5.handler;

import io.reactivex.SingleEmitter;
import org.mqttbee.annotations.NotNull;
import org.mqttbee.api.mqtt5.message.connect.connack.Mqtt5ConnAck;
import org.mqttbee.mqtt5.Mqtt5ClientDataImpl;
import org.mqttbee.mqtt5.message.connect.Mqtt5ConnectImpl;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Provider for the channel initializer.
 *
 * @author Silvio Giebl
 */
@Singleton
public class Mqtt5ChannelInitializerProvider {

    @Inject
    Mqtt5ChannelInitializerProvider() {
    }

    /**
     * Returns the appropriate channel initializer for the given data.
     *
     * @param connect        the CONNECT message.
     * @param connAckEmitter the emitter for the CONNACK message.
     * @param clientData     the data of the client.
     * @return the appropriate channel initializer.
     */
    public Mqtt5ChannelInitializer get(
            @NotNull final Mqtt5ConnectImpl connect, @NotNull final SingleEmitter<Mqtt5ConnAck> connAckEmitter,
            @NotNull final Mqtt5ClientDataImpl clientData) {

        if (clientData.usesSSL()) {
            throw new UnsupportedOperationException(); // TODO
        } else {
            return new Mqtt5ChannelInitializer(connect, connAckEmitter, clientData);
        }
    }

}
