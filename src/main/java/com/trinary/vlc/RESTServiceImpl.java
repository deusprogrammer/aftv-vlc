package com.trinary.vlc;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.multipart.FormDataMultiPart;
import com.sun.jersey.multipart.file.FileDataBodyPart;
import com.trinary.aftv.commons.EventDTO;
import com.trinary.util.StringUtil;

public class RESTServiceImpl implements RESTService {
//	protected String protocol;
//	protected String hostname;
//	protected String port;
//	protected String publishUriTemplate;
//	protected String createUriTemplate;
	
	protected String protocol           = "http";
	protected String hostname           = "localhost";
	protected String port               = "8080";
	protected String publishUriTemplate = "/aftv-backend/v1/contest/{contestId}/publish";
	protected String createUriTemplate  = "/aftv-backend/v1/contest/";
	
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
 
    	// Create the contest
		ClientResponse response = webResource.type("application/json")
		   .post(ClientResponse.class, jsonString);
		if (response.getStatus() < 200 || response.getStatus() > 299) {
			throw new RuntimeException("Failed : HTTP error code : "
			     + response.getStatus());
		}
	}
	
	@Override
	public void uploadThumbnail(Contest contest, ContestEntry entry, String thumbnailPath) throws IOException {
		Map<String, Object> values = new HashMap<String, Object>();
		values.put("uuid", contest.getUuid());
		values.put("entryId", entry.getUuid());
		String uri = StringUtil.interpolate("/aftv-backend/v1/contest/{uuid}/entry/{entryId}/thumbnail", values);
		String url = String.format("%s://%s:%s%s", protocol, hostname, port, uri);
		
		System.out.println("URL: " + url);
		
		Client client = Client.create();
		WebResource webResource = client
				.resource(url);
		
		File file = new File(thumbnailPath);
        
        try {
			FormDataMultiPart part = new FormDataMultiPart();
			part.bodyPart(new FileDataBodyPart("file", file, MediaType.APPLICATION_OCTET_STREAM_TYPE));
	        ClientResponse response = webResource.type(MediaType.MULTIPART_FORM_DATA_TYPE)
	        		.post(ClientResponse.class, part);
	        if (response.getStatus() < 200 || response.getStatus() > 299) {
				throw new RuntimeException("Failed : HTTP error code : "
				     + response.getStatus());
			}
        } catch (Exception e) {
        	throw new RuntimeException("Failed to add thumbnail", e);
        }
	}

	public void sendTrigger(PlayerEvent playerEvent) {
		Contest contest = playerEvent.getPlaylist();
		ContestEntry entry = playerEvent.getEntry();
		String trigger = playerEvent.getType().toString();
		
		Client client = Client.create();
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("contestId", contest.getUuid());
		
		String uri = StringUtil.interpolate(publishUriTemplate, parameters);
		String url = String.format("%s://%s:%s%s", protocol, hostname, port, uri);
		
		System.out.println("URL: " + url);
		
		WebResource webResource = client
		   .resource(url);
		
		EventDTO event = new EventDTO();
		event.setContestId(contest.getUuid());
		if (entry != null) {
			event.setEntryId(entry.getUuid());
		}
		event.setEventType(trigger);
		
		ObjectMapper mapper = new ObjectMapper();
		String jsonString = null;
		try {
			jsonString = mapper.typedWriter(EventDTO.class).writeValueAsString(event);
		} catch (JsonGenerationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (JsonMappingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
 
		try {
			ClientResponse response = webResource.type("application/json")
			   .post(ClientResponse.class, jsonString);
			
			System.out.println("RESPONSE: " + response.getStatus());
	 
			if (response.getStatus() < 200 || response.getStatus() > 300) {
				throw new RuntimeException("Failed : HTTP error code : "
				     + response.getStatus());
			}
		} catch (Exception e) {
			throw new RuntimeException("Trigger post failed!", e);
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