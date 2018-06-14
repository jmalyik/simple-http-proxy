package com.experimental.httpproxy;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * 
 * @author jmalyik
 *
 */
@Configuration
public class ProxyConfiguration {

	@Value("${proxy.use}")
	private boolean useProxy;

	@Value("${proxy.useAuthenticated}")
	private boolean useAuthenticatedProxy;

	@Value("${server.port}")
	private int serverPort;

	@Value("${proxy.host}")
	private String proxyHost;

	@Value("${proxy.port}")
	private int proxyPort;

	@Value("${proxy.user}")
	private String proxyUser;

	@Value("${proxy.pass}")
	private String proxyPass;

	/**
	 * creates the proper @see {@link RestTemplate}
	 * 
	 * @return
	 */
	@Bean
	public RestTemplate restTemplate() {
		if(useProxy){
			SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
			Proxy proxy= new Proxy(Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
			requestFactory.setProxy(proxy);
			return  new RestTemplate(requestFactory);
		}else if(useAuthenticatedProxy){
			CredentialsProvider credsProvider = new BasicCredentialsProvider();
			credsProvider.setCredentials( 
					new AuthScope(proxyHost, proxyPort),
					new UsernamePasswordCredentials(proxyUser, proxyPass)
					);
			HttpHost proxy = new HttpHost(proxyHost, proxyPort);
			HttpClientBuilder clientBuilder = HttpClientBuilder.create();
			clientBuilder.setProxy(proxy).setDefaultCredentialsProvider(credsProvider).disableCookieManagement();
			HttpClient httpClient = clientBuilder.build();
			HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
			factory.setHttpClient(httpClient);
			return  new RestTemplate(factory);
		}else{
			return new RestTemplate();
		}
	}
}
