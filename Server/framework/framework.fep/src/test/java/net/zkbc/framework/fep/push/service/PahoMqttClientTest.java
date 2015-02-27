package net.zkbc.framework.fep.push.service;

import java.io.IOException;


import net.zkbc.framework.fep.push.service.PahoMqttClient;

import org.junit.Test;


public class PahoMqttClientTest {

	@Test
	public void test() {
		PahoMqttClient client = new PahoMqttClient();
		client.setHost("m2m.eclipse.org");
		client.setPort(1883);
		client.setClientId("TestCase");

		client.connect(null, null, 30 * 1000, null, false, null);
		client.subscribe("msbank/9472bf0296d9baa2", 2);

		try {
			System.in.read();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
