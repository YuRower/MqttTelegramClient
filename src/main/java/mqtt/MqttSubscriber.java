package mqtt;

import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqttSubscriber implements MqttCallback {
	private final static Logger LOGGER = Logger.getLogger(MqttSubscriber.class);

	static final String TOPIC = "myCourseWorkTopic/iot";
	static final String BROKER = "tcp://iot.eclipse.org:1883";
	private String clientId;
	private String messageReply;
	private MqttClient client;

	public MqttSubscriber(String clientId) {
		this.clientId = clientId;
	}

	public MqttSubscriber() {
	}

	public void subscribe() throws MqttException {
		client = new MqttClient(BROKER, clientId);
		client.connect();
		client.setCallback(this);
		client.subscribe(TOPIC);
		LOGGER.info(clientId + " subscribe topic " + TOPIC);
	}

	public void connectionLost(Throwable cause) {
		LOGGER.info(clientId + " lost connection \n " + "Connection lost on instance \"" + clientId + "\" with cause \""
				+ cause.getMessage() + "\" Reason code " + ((MqttException) cause).getReasonCode() + "\" Cause \""
				+ ((MqttException) cause).getCause() + "\"");
	}

	public void messageArrived(String topic, MqttMessage message) throws Exception {
		LOGGER.info(clientId + " get a new message " + message);
		LOGGER.info("Message arrived: \"" + message.toString() + "\" on topic \"" + topic
				+ "\" for instance \"" + clientId + "\"");
		messageReply="Message arrived: \"" + message.toString() + "\" on topic \"" + topic
		+ "\" for instance \"" + clientId + "\"";
		
	}

	public void deliveryComplete(IMqttDeliveryToken token) {
		LOGGER.info(clientId + " message delivered");
		LOGGER.info("Delivery token \"" + token.hashCode() + "\" received by instance \"" + clientId + "\"");
	}

	public String getMessage() {
		return messageReply;
	}

}