package com.experimental.httpproxy;

import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * 
 * Rest Template using http proxy
 * http://stackoverflow.com/questions/3687670/using-resttemplate-how-to-send-the-request-to-a-proxy-first-so-i-can-use-my-jun
 * 
 * TWO WAY SSL example
 * https://github.com/viniciusccarvalho/boot-two-way-ssl-example
 * 
 * 
 * This one is the combined version :)
 * 
 * @author jmalyik
 *
 */
@RestController
@SpringBootApplication
public class ProxyApplication {

	private Logger logger = LoggerFactory.getLogger(ProxyApplication.class);

	@Autowired
	private RestTemplate restTemplate;

	@Value("${target_host}")
	private String proxyhost;

	@Value("${target_port}")
	private int proxyport;

	@RequestMapping(path="/**", method=RequestMethod.POST)
	@ResponseBody
	public String mirrorRestPost(@RequestBody String body, HttpMethod method, HttpServletRequest request,
			HttpServletResponse response) throws URISyntaxException
	{
		return doMirror(body, method, request);
	}

	@RequestMapping(path = "/**", method = RequestMethod.PUT)
	@ResponseBody
	public String mirrorRestPUT(@RequestBody String body, HttpMethod method, HttpServletRequest request,
			HttpServletResponse response) throws URISyntaxException {
		return doMirror(body, method, request);
	}

	@RequestMapping(path = "/**", method = RequestMethod.GET)
	@ResponseBody
	public String mirrorRestGET(HttpMethod method, HttpServletRequest request, HttpServletResponse response)
			throws URISyntaxException {
		return doMirror(null, method, request);
	}

	private String doMirror(String body, HttpMethod method, HttpServletRequest request) throws URISyntaxException {
		if (body != null) {
			logger.trace("Request body: {}", body);
		}
		logger.trace("Method: {}", method);
		URI uri = new URI("http", null, proxyhost, proxyport, request.getRequestURI(), request.getQueryString(), null);
		ResponseEntity<String> responseEntity = restTemplate.exchange(uri, method, getHttpEntity(body), String.class);
		return responseEntity.getBody();
	}

	private HttpEntity<String> getHttpEntity(String body) {
		return body != null ? new HttpEntity<>(body) : null;
	}

	public static void main(String[] args) {
		SpringApplication.run(ProxyApplication.class, args);
	}
}
