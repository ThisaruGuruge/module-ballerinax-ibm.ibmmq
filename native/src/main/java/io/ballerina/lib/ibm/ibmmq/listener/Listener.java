/*
 * Copyright (c) 2025, WSO2 LLC. (http://www.wso2.org).
 *
 *  WSO2 LLC. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied. See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package io.ballerina.lib.ibm.ibmmq.listener;

import com.ibm.mq.jms.MQConnection;
import com.ibm.mq.jms.MQConnectionFactory;
import com.ibm.mq.jms.MQSession;
import com.ibm.msg.client.wmq.WMQConstants;
import io.ballerina.lib.ibm.ibmmq.Constants;
import io.ballerina.runtime.api.Environment;
import io.ballerina.runtime.api.utils.StringUtils;
import io.ballerina.runtime.api.values.BMap;
import io.ballerina.runtime.api.values.BObject;
import io.ballerina.runtime.api.values.BString;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Topic;
import javax.net.ssl.SSLSocketFactory;

import static io.ballerina.lib.ibm.ibmmq.CommonUtils.createError;
import static io.ballerina.lib.ibm.ibmmq.Constants.HOST;
import static io.ballerina.lib.ibm.ibmmq.Constants.IBMMQ_ERROR;
import static io.ballerina.lib.ibm.ibmmq.Constants.PORT;
import static io.ballerina.lib.ibm.ibmmq.Constants.SECURE_SOCKET;
import static io.ballerina.lib.ibm.ibmmq.Constants.SSL_CIPHER_SUITE;
import static io.ballerina.lib.ibm.ibmmq.Constants.USER_ID;
import static io.ballerina.lib.ibm.ibmmq.QueueManager.getSecureSocketFactory;
import static io.ballerina.lib.ibm.ibmmq.QueueManager.getSslProtocol;

/**
 * Native class for the Ballerina IBM MQ Listener.
 *
 * @since 1.3.0
 */
public final class Listener {
    static final String NATIVE_CONNECTION = "JMSConnection";
    static final String NATIVE_CONSUMER = "JMSConsumer";
    static final String NATIVE_SESSION = "JMSSession";
    static final String NATIVE_SERVICE_LIST = "ServiceList";
    static final String NATIVE_SERVICE = "NativeService";

    static final BString QUEUE_NAME = StringUtils.fromString("queueName");
    static final BString TOPIC_NAME = StringUtils.fromString("topicName");
    static final BString DURABLE = StringUtils.fromString("durable");

    private Listener() {
    }

    public static Object initListener(BObject listener, BMap<BString, Object> configs) {
        try {
            MQConnectionFactory connectionFactory = new MQConnectionFactory();
            connectionFactory.setHostName(configs.getStringValue(HOST).getValue());
            connectionFactory.setPort(configs.getIntValue(PORT).intValue());
            connectionFactory.setQueueManager(configs.getStringValue(Constants.QUEUE_MANAGER_NAME).getValue());
            connectionFactory.setChannel(configs.getStringValue(Constants.CHANNEL).getValue());
            connectionFactory.setTransportType(WMQConstants.WMQ_CM_CLIENT);
            if (configs.containsKey(SSL_CIPHER_SUITE)) {
                connectionFactory.setSSLCipherSuite(configs.getStringValue(SSL_CIPHER_SUITE).getValue());
            }

            BMap<BString, Object> secureSocket = (BMap<BString, Object>) configs.getMapValue(SECURE_SOCKET);
            if (secureSocket != null) {
                String sslProtocol = getSslProtocol(configs);
                SSLSocketFactory sslSocketFactory = getSecureSocketFactory(sslProtocol, secureSocket);
                connectionFactory.setSSLSocketFactory(sslSocketFactory);
            }
            String user = configs.getStringValue(USER_ID).getValue();
            String pass = configs.getStringValue(Constants.PASSWORD).getValue();
            MQConnection connection = (MQConnection) connectionFactory.createConnection(user, pass);
            connection.setClientID("ballerina_ibmmq_" + UUID.randomUUID());
            listener.addNativeData(NATIVE_CONNECTION, connection);
            listener.addNativeData(NATIVE_SERVICE_LIST, new ArrayList<BObject>());
        } catch (Exception e) {
            return createError(IBMMQ_ERROR, "Failed to initialize listener", e);
        }
        return null;
    }

    public static Object attach(Environment environment, BObject listener, BObject service, Object name) {
        IbmmqService nativeService = new IbmmqService(service);
        nativeService.validate();
        MQConnection connection = (MQConnection) listener.getNativeData(NATIVE_CONNECTION);
        if (connection == null) {
            return createError(IBMMQ_ERROR, "JMS connection is not initialized");
        }
        try {
            MQSession session = (MQSession) connection.createSession();
            MessageConsumer consumer;
            if (nativeService.isTopic()) {
                consumer = getTopicConsumer(session, nativeService);
            } else {
                consumer = getQueueConsumer(session, nativeService.getConfig());
            }
            service.addNativeData(NATIVE_SERVICE, nativeService);
            service.addNativeData(NATIVE_CONSUMER, consumer);
            service.addNativeData(NATIVE_SESSION, session);
            consumer.setMessageListener(new BallerinaIbmmqListener(environment, service, nativeService));

            @SuppressWarnings("unchecked")
            List<BObject> serviceList = (List<BObject>) listener.getNativeData(NATIVE_SERVICE_LIST);
            if (serviceList != null) {
                serviceList.add(service);
            }
        } catch (Exception e) {
            return createError(IBMMQ_ERROR, "Failed to attach the service to listener", e);
        }
        return null;
    }

    public static Object start(BObject listener) {
        try {
            MQConnection connection = (MQConnection) listener.getNativeData(NATIVE_CONNECTION);
            if (connection == null) {
                return createError(IBMMQ_ERROR, "JMS connection is not initialized");
            }
            connection.start();
        } catch (JMSException e) {
            return createError(IBMMQ_ERROR, "Failed to start the listener", e);
        }
        return null;
    }

    public static Object detach(BObject listener, BObject service) {
        try {
            MessageConsumer consumer = (MessageConsumer) service.getNativeData(NATIVE_CONSUMER);
            MQSession session = (MQSession) service.getNativeData(NATIVE_SESSION);
            IbmmqService nativeService = (IbmmqService) service.getNativeData(NATIVE_SERVICE);
            if (consumer != null) {
                if (nativeService.getSubscriptionName() != null) {
                    session.unsubscribe(nativeService.getSubscriptionName());
                }
                consumer.close();
            }
            if (session != null) {
                session.close();
            }
        } catch (JMSException e) {
            return createError(IBMMQ_ERROR, "Failed to detach the service from listener", e);
        }
        return null;
    }

    public static Object gracefulStop(BObject listener) {
        try {
            @SuppressWarnings("unchecked")
            List<BObject> serviceList = (List<BObject>) listener.getNativeData(NATIVE_SERVICE_LIST);
            if (serviceList != null) {
                for (BObject service : serviceList) {
                    detach(listener, service);
                }
            }
            MQConnection connection = (MQConnection) listener.getNativeData(NATIVE_CONNECTION);
            if (connection != null) {
                connection.close();
            }
        } catch (JMSException e) {
            return createError(IBMMQ_ERROR, "Failed to gracefully stop the listener", e);
        }
        return null;
    }

    public static Object immediateStop(BObject listener) {
        try {
            @SuppressWarnings("unchecked")
            List<BObject> serviceList = (List<BObject>) listener.getNativeData(NATIVE_SERVICE_LIST);
            if (serviceList != null) {
                for (BObject service : serviceList) {
                    detach(listener, service);
                }
            }
            MQConnection connection = (MQConnection) listener.getNativeData(NATIVE_CONNECTION);
            if (connection != null) {
                connection.close();
            }
        } catch (JMSException e) {
            return createError(IBMMQ_ERROR, "Failed to immediately stop the listener", e);
        }
        return null;
    }

    private static MessageConsumer getQueueConsumer(MQSession session, BMap<BString, Object> config)
            throws JMSException {
        String queueName = config.getStringValue(QUEUE_NAME).getValue();
        Destination destination = session.createQueue(queueName);
        return session.createConsumer(destination);
    }

    private static MessageConsumer getTopicConsumer(MQSession session, IbmmqService nativeService)
            throws JMSException {
        String topicName = nativeService.getConfig().getStringValue(TOPIC_NAME).getValue();
        boolean durable = nativeService.getConfig().getBooleanValue(DURABLE);
        String subscriptionName = nativeService.getSubscriptionName();
        Topic topic = session.createTopic(topicName);
        if (durable) {
            return session.createDurableConsumer(topic, subscriptionName);
        } else {
            return session.createConsumer(topic);
        }
    }
}
