package org.openhab.binding.homie.internal;

import static org.openhab.binding.homie.HomieBindingConstants.*;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttClientPersistence;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.eclipse.smarthome.core.thing.Thing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MqttConnection {
    private static Logger logger = LoggerFactory.getLogger(MqttConnection.class);

    private static MqttConnection instance = null;

    private MqttClient client;
    private MqttClientPersistence persistence = new MemoryPersistence();

    public MqttConnection() throws MqttException {
        logger.debug("Homie MQTT Connection start");
        client = new MqttClient(BROKER_URL, MQTT_CLIENTID, new MemoryPersistence());
        client.connect();
        logger.debug("Homie MQTT Connection connected");
    }

    public void subscribe(Thing thing, IMqttMessageListener messageListener) {
        String topic = String.format("%s/%s/#", BASETOPIC, thing.getUID());
        try {
            client.subscribe(topic, messageListener);
        } catch (MqttException e) {
            logger.error("Failed to subscribe to topic " + topic, e);
        }
    }

    public static MqttConnection getInstance() {
        if (instance == null) {
            try {
                instance = new MqttConnection();
            } catch (MqttException e) {
                logger.error("Failed to connect to broker", e);
            }
        }
        return instance;
    }

    public void listenForDeviceIds(IMqttMessageListener messageListener) {
        String topic = String.format("%s/#", BASETOPIC);
        logger.debug("Listening for devices on topic " + topic);
        try {
            client.subscribe(topic, messageListener);
        } catch (MqttException e) {
            logger.error("Failed to subscribe to topic " + topic, e);
        }
    }

}
