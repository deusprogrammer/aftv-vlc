package com.trinary.vlc;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.trinary.aftv.commons.Event;
import com.trinary.util.StringUtil;

public class RESTTriggerServiceImpl implements RESTTriggerService {
	protected String protocol;
	protected String hostname;
	protected String port;
	protected String publishUriTemplate;
	protected String createUriTemplate;
	
	public void createContest(Contest contest) {
		Client client = Client.create();
		
		String url = String.format("%s://%s:%s%s", protocol, hostname, port, createUriTemplate);
		
		System.out.println("URL: " + url);
		 
		WebResource webResource = client
		   .resource(url);
		
		// Send playlist info to AFTV-Backend
    	ObjectMapper mapper = new ObjectMapper();
    	String jsonString = null;
    	try {
			jsonString = mapper.typedWriter(Contest.class).writeValueAsString(contest);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	System.out.println("JSON: " + jsonString);
 
		ClientResponse response = webResource.type("application/json")
		   .post(ClientResponse.class, jsonString);
 
		if (response.getStatus() < 200 || response.getStatus() > 299) {
			throw new RuntimeException("Failed : HTTP error code : "
			     + response.getStatus());
		}
	}

	public void sendTrigger(Contest contest, ContestEntry entry, String trigger) {
		Client client = Client.create();
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("contestId", contest.getUuid());
		
		String uri = StringUtil.interpolate(publishUriTemplate, parameters);
		String url = String.format("%s://%s:%s%s", protocol, hostname, port, uri);
		
		System.out.println("URL: " + url);
		
		WebResource webResource = client
		   .resource(url);
		
		Event event = new Event();
		event.setContestId(contest.getUuid());
		event.setEntryId(entry.getUuid());
		event.setEventType(trigger);
 
		ClientResponse response = webResource.type("application/json")
		   .post(ClientResponse.class, event);
 
		if (response.getStatus() != 201) {
			throw new RuntimeException("Failed : HTTP error code : "
			     + response.getStatus());
		}
	}
	
	/**
	 * @return the hostname
	 */
	public String getHostname() {
		return hostname;
	}
	
	/**
	 * @param hostname the hostname to set
	 */
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	
	/**
	 * @return the port
	 */
	public String getPort() {
		return port;
	}
	
	/**
	 * @param port the port to set
	 */
	public void setPort(String port) {
		this.port = port;
	}

	/**
	 * @return the publishUriTemplate
	 */
	public String getPublishUriTemplate() {
		return publishUriTemplate;
	}

	/**
	 * @param publishUriTemplate the publishUriTemplate to set
	 */
	public void setPublishUriTemplate(String publishUriTemplate) {
		this.publishUriTemplate = publishUriTemplate;
	}

	/**
	 * @return the createUriTemplate
	 */
	public String getCreateUriTemplate() {
		return createUriTemplate;
	}

	/**
	 * @param createUriTemplate the createUriTemplate to set
	 */
	public void setCreateUriTemplate(String createUriTemplate) {
		this.createUriTemplate = createUriTemplate;
	}

	/**
	 * @return the protocol
	 */
	public String getProtocol() {
		return protocol;
	}

	/**
	 * @param protocol the protocol to set
	 */
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
}