package com.experimental.httproxy;

import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.cert.Certificate;

import javax.net.ssl.HttpsURLConnection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 
 * @author jmalyik
 *
 */
@ContextConfiguration(classes = {ClientConfiguration.class})//, ProxyApplication.class})
@RunWith(SpringJUnit4ClassRunner.class)
//@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@ConfigurationProperties("application-test.properties")
public class ClientTest {

	private Logger logger = LoggerFactory.getLogger(ClientTest.class);

	@Value("${server.ssl.trust-store}")
	private String trustStore;
	@Value("${server.ssl.trust-store-password}")
	private String trustStorePass;

	@Value("${server.ssl.key-store}")
	private String keyStore;
	@Value("${server.ssl.key-store-password}")
	private String keyStorePassword;

	@Test
	public void testConnection() {
		System.setProperty("javax.net.debug", "ssl");
		System.setProperty("javax.net.ssl.trustStore", "src/main/resources/client.jks");
		System.setProperty("javax.net.ssl.trustStorePassword", "changeit");
		System.setProperty("javax.net.ssl.keyStore", "src/main/resources/server.jks");
		System.setProperty("javax.net.ssl.keyStorePassword", "changeit");

		javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(new javax.net.ssl.HostnameVerifier() {

			@Override
			public boolean verify(String hostname, javax.net.ssl.SSLSession sslSession) {
				logger.debug("HOSTNAME: {}", hostname);
				if (hostname.equals("localhost")) {
					return true;
				}
				return false;
			}
		});
		HttpsURLConnection con = null;
		try {
			URL url = new URL("https://localhost");
			con = (HttpsURLConnection) url.openConnection();

			// dumpl all cert info
			print_https_cert(con);

			// dump all the content
			print_content(con);

		} catch (IOException e) {
			logger.error("error", e);
			fail(e.getMessage());
		} finally {
			if (con != null) {
				con.disconnect();
			}
		}
	}

	private void print_https_cert(HttpsURLConnection con) {
		if (con != null) {
			try {

				logger.debug("Response Code : {}", con.getResponseCode());
				logger.debug("Cipher Suite : {}", con.getCipherSuite());

				Certificate[] certs = con.getServerCertificates();
				for (Certificate cert : certs) {
					logger.debug("\nCert Type: {}", cert.getType());
					logger.debug("Cert Hash Code: {}", cert.hashCode());
					logger.debug("Cert Public Key Algorithm: {}", cert.getPublicKey().getAlgorithm());
					logger.debug("Cert Public Key Format: {}", cert.getPublicKey().getFormat());
					logger.debug("\n");
				}

			} catch (IOException e) {
				logger.error("error", e);
				fail(e.getMessage());
			}
		}
	}

	private void print_content(HttpsURLConnection con) {
		if (con != null) {
			BufferedReader br = null;
			try {

				logger.debug("****** Content of the URL ********");
				br = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String input;
				while ((input = br.readLine()) != null) {
					logger.debug(input);
				}
			} catch (IOException e) {
				logger.error("error", e);
				fail(e.getMessage());
			} finally {
				if (br != null) {
					try {
						br.close();
					} catch (IOException e) {
						logger.error("error on closing BufferedReader", e);
					}
				}
			}
		}
	}
}
