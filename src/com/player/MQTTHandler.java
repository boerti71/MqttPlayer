package com.player;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import io.github.initio.JavaMQTT;

public class MQTTHandler {
	public MQTTHandler() {
		super();
		logger = Logger.getLogger(MQTTHandler.class.getName());
	}

	static JavaMQTT mqtt = null;
	static String sBroker = "tcp://192.168.1.16:1883";
	static String sClientId = "RaspiPlayer";
	static String sTopic = "home/nodes/sensor/raspiplayer";
	static String sStatus = "home/nodes/sensor/raspiplayer/Monitor";
	static String sStatusFeedback = "alive";
	static int iStatusIntervall = 120;
	static int iQoS = 1;
	static String sMessage = "online";
	boolean isConnected = false;
	String sUsername = "";
	String sPassword = "";
	Logger logger;

	public void setsBroker(String sBroker) {
		MQTTHandler.sBroker = sBroker;
	}

	public void setsClientId(String sClientId) {
		MQTTHandler.sClientId = sClientId;
	}

	public void setiQoS(int iQoS) {
		MQTTHandler.iQoS = iQoS;
	}

	public void setiStatusIntervall(int iStatusIntervall) {
		MQTTHandler.iStatusIntervall = iStatusIntervall;
	}

	public void setsUsername(String sUsername) {
		this.sUsername = sUsername;
	}

	public void setsPassword(String sPassword) {
		this.sPassword = sPassword;
	}

	public void setsTopic(String sTopic) {
		MQTTHandler.sTopic = sTopic;
	}

	public void setsStatusTopic(String sStatus) {
		MQTTHandler.sStatus = sStatus;
	}

	public void setsStatusFeedback(String sStatusFeedback) {
		MQTTHandler.sStatusFeedback = sStatusFeedback;
	}

	public void startMqtt() {
		Timer watchdogTimer = new Timer();
		MP3Player playMp3 = new MP3Player();
		WavPlayer playWav = new WavPlayer();
		try {
			mqtt = new JavaMQTT(sBroker, sClientId, null, Runnable::run // Main thread callback executor
			);
			mqtt.setQos(iQoS);
		} catch (MqttException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mqtt.setOnReconnectListener(() -> {
			logger.log(Level.INFO, "Reconnected! 🎉 Resubscribing...");
		});
		mqtt.connect(sUsername, sPassword, new JavaMQTT.ConnectionListener() {
			@Override
			public void onSuccess() {
				mqtt.subscribe(sTopic, (sTopic, payload) -> {
					logger.log(Level.INFO, "Got message: " + payload + " from sTopic: " + sTopic);
					Object obj = JSONValue.parse(payload);
					JSONObject jsonObject = (JSONObject) obj;
					String sAction = (String) jsonObject.get("action");
					String sSong = (String) jsonObject.get("song");
					if (sAction.toLowerCase().equals("play")) {
						if (sSong.indexOf("mp3") > 0) {
							playMp3.play(sSong);
						}
						if (sSong.indexOf("wav") > 0) {
							try {
								playWav.play(sSong);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								logger.log(Level.WARNING, "File not found..." + sSong);
							}
						}
					}
				});
				logger.log(Level.INFO, "MQTT Connected 🎉");
			}

			@Override
			public void onFailure(Throwable e) {
				logger.log(Level.WARNING, "MQTT Bruh, connect failed 💀: " + e.getMessage());
			}
		});

		TimerTask watchdogTask = new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				mqtt.put(sStatus, sStatusFeedback);
				DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
				LocalDateTime now = LocalDateTime.now();
				logger.log(Level.INFO, "Status sent:"+dtf.format(now));
			}
		};
		watchdogTimer.schedule(watchdogTask, 2000, iStatusIntervall * 1000);

	}

}