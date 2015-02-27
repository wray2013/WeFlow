package net.zkbc.framework.fep.push.service;

import java.util.Properties;

import net.zkbc.framework.fep.push.protocol.MessagingMessage;

import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttDefaultFilePersistence;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;


class PahoMqttClient implements Pusher, MqttCallback {
	private static final Logger LOG = LoggerFactory
			.getLogger(PahoMqttClient.class);

	@Value("${mqtt.host:m2m.eclipse.org}")
	private String host;
	@Value("${mqtt.port:1883}")
	private int port;
	@Value("${mqtt.clientId:mqtt_server}")
	private String clientId;
	private boolean cleanSession;
	private String persistence;

	private MqttClient myClient;
	private MqttConnectOptions connectOptions;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public boolean isCleanSession() {
		return cleanSession;
	}

	public void setCleanSession(boolean cleanSession) {
		this.cleanSession = cleanSession;
	}

	public String getPersistence() {
		return persistence;
	}

	public void setPersistence(String persistence) {
		this.persistence = persistence;
	}

	@Override
	public void connect(String username, String password,
			int keepAliveInterval, MessagingMessage willMessage,
			boolean useSSL, Properties sslProperties) {

		if (isConnected()) {
			return;
		}

		String passwordToTrace = ((password == null) || (password.trim()
				.length() == 0)) ? "" : "XXXXXXXXX";

		LOG.debug("connect({},\"{}\",\"{}\",{},\"{}\",{}", cleanSession,
				username, passwordToTrace, keepAliveInterval, willMessage,
				useSSL);

		String serverURI = (useSSL ? "ssl:" : "tcp:") + "//" + host + ":"
				+ port;

		LOG.debug("Connecting \"{}\" as \"{}\"", serverURI, clientId);

		String dir = persistence;
		if (dir == null) {
			dir = System.getProperty("user.home");
		}
		try {
			myClient = new MqttClient(serverURI, clientId,
					new MqttDefaultFilePersistence(dir));
			myClient.setCallback(this);

			connectOptions = new MqttConnectOptions();
			connectOptions.setKeepAliveInterval(keepAliveInterval);
			connectOptions.setCleanSession(cleanSession);
			if ((username != null) && (username.trim().length() != 0)) {
				connectOptions.setUserName(username);
			}
			if ((password != null) && (password.trim().length() != 0)) {
				connectOptions.setPassword(password.toCharArray());
			}
			if (willMessage != null) {
				connectOptions.setWill(
						myClient.getTopic(willMessage.getDestinationName()),
						willMessage.getMessagePayload(), willMessage.getQos(),
						willMessage.isRetained());
			}
			connectOptions.setSSLProperties(sslProperties);
			doConnect();
		} catch (MqttException e) {
			LOG.debug(e.getMessage(), e);
		}

	}

	@Override
	public void disconnect() {
		LOG.debug("disconnect()");

		try {
			myClient.disconnect();
			myClient.setCallback(null);
		} catch (MqttException e) {
			// ignore this
		}
	}

	@Override
	public void push(MessagingMessage msg) {
		publish(msg);
	}

	@Override
	public void connectionLost(Throwable why) {
		LOG.debug("connectionLost({})", why.getMessage());
		doConnect();
	}

	@Override
	public void deliveryComplete(MqttDeliveryToken messageToken) {
		LOG.debug("deliveryComplete({})", messageToken);
	}

	@Override
	public void messageArrived(MqttTopic topic, MqttMessage msg)
			throws Exception {
		LOG.debug("messageArrived({}, {})", topic.getName(), msg);
	}

	public void publish(MessagingMessage msg) {
		LOG.debug("publish({})", msg);

		if (isConnected()) {
			try {
				myClient.getTopic(msg.getDestinationName())
						.publish(msg.getMessagePayload(), msg.getQos(),
								msg.isRetained());
			} catch (MqttException e) {
				throw new RuntimeException(e.getMessage(), e);
			}
		}
	}

	public void subscribe(String topicFilter, int qos) {
		LOG.debug("subscribe(\"{}\", {})", topicFilter, qos);

		if (isConnected()) {
			try {
				myClient.subscribe(topicFilter, qos);
			} catch (MqttException e) {
				throw new RuntimeException(e.getMessage(), e);
			}
		} else {
			LOG.error("not connected");
		}

	}

	public void unsubscribe(String topicFilter) {
		LOG.debug("unsubscribe(\"{}\")", topicFilter);

		if (isConnected()) {
			try {
				myClient.unsubscribe(topicFilter);
			} catch (MqttException e) {
				throw new RuntimeException(e.getMessage(), e);
			}

		} else {
			LOG.error("not connected");
		}

	}

	private boolean isConnected() {
		return (myClient != null) && (myClient.isConnected());
	}

	private void doConnect() {
		try {
			myClient.connect(connectOptions);
		} catch (MqttException e) {
			if (e.getReasonCode() == MqttException.REASON_CODE_CLIENT_ALREADY_CONNECTED) {
			} else {
				throw new RuntimeException(e.getMessage(), e);
			}
		}
	}

	@Override
	public boolean support(MessagingMessage msg) {
		String destinationType = msg.getDestinationType();

		return "apad".equalsIgnoreCase(destinationType)
				|| "aphone".equalsIgnoreCase(destinationType);
	}

}
