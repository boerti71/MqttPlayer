package com.player;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

	public static void main(String[] args) {

		MQTTHandler mqh = new MQTTHandler();
		Logger logger = Logger.getLogger(MQTTHandler.class.getName());
		if (args.length > 0) {
			logger.log(Level.INFO,"Importing ini...");
			Properties prop = new Properties();
			try {
				prop.load(new FileInputStream(args[0]));
				mqh.setsBroker(prop.getProperty("broker"));
				mqh.setsClientId(prop.getProperty("clientid"));
				mqh.setsTopic(prop.getProperty("topic"));
				mqh.setsStatusTopic(prop.getProperty("statustopic"));
				mqh.setsStatusFeedback(prop.getProperty("statusfeedback"));
				mqh.setiStatusIntervall(Integer.parseInt(prop.getProperty("statusintervall")));
				mqh.setiQoS(Integer.parseInt(prop.getProperty("qos")));
				mqh.setsUsername(prop.getProperty("username"));
				mqh.setsPassword(prop.getProperty("password"));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				logger.log(Level.WARNING, ".. failed, file not found");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.log(Level.WARNING, ".. failed");
			}
		}
		mqh.startMqtt();
	}
}
