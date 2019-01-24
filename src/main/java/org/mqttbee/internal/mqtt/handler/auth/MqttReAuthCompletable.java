/*
 * Copyright 2018 The MQTT Bee project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.mqttbee.internal.mqtt.handler.auth;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.internal.disposables.EmptyDisposable;
import org.jetbrains.annotations.NotNull;
import org.mqttbee.internal.mqtt.MqttClientConfig;
import org.mqttbee.internal.mqtt.MqttClientConnectionConfig;
import org.mqttbee.internal.mqtt.exceptions.MqttClientStateExceptions;
import org.mqttbee.internal.rx.CompletableFlow;

/**
 * @author Silvio Giebl
 */
public class MqttReAuthCompletable extends Completable {

    private final @NotNull MqttClientConfig clientConfig;

    public MqttReAuthCompletable(final @NotNull MqttClientConfig clientConfig) {
        this.clientConfig = clientConfig;
    }

    @Override
    protected void subscribeActual(final @NotNull CompletableObserver s) {
        final MqttClientConnectionConfig clientConnectionConfig = clientConfig.getRawClientConnectionConfig();
        if (clientConnectionConfig == null) {
            EmptyDisposable.error(MqttClientStateExceptions.notConnected(), s);
            return;
        }
        if (clientConnectionConfig.getRawEnhancedAuthMechanism() == null) {
            EmptyDisposable.error(new UnsupportedOperationException(
                    "Reauth is not available if enhanced auth was not used during connect"), s);
            return;
        }
        final Channel channel = clientConnectionConfig.getChannel();
        final ChannelHandler authHandler = channel.pipeline().get(MqttAuthHandler.NAME);
        if (authHandler == null) {
            EmptyDisposable.error(MqttClientStateExceptions.notConnected(), s);
            return;
        }
        if (!(authHandler instanceof MqttReAuthHandler)) {
            EmptyDisposable.error(new UnsupportedOperationException("Auth is still pending"), s);
            return;
        }
        final MqttReAuthHandler reAuthHandler = (MqttReAuthHandler) authHandler;
        final CompletableFlow flow = new CompletableFlow(s);
        s.onSubscribe(flow);
        reAuthHandler.reauth(flow);
    }
}