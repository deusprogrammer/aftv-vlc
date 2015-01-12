package com.trinary.vlc;

public class PlaylistEntry {
	protected String id;
	protected String title;
	protected String url;
	protected Integer lengthInSeconds;
	
	public PlaylistEntry(String id, String title, String url, Integer lengthInSeconds) {
		super();
		this.id = id;
		this.title = title;
		this.url = url;
		this.lengthInSeconds = lengthInSeconds;
	}
	
	public String getName() {
		return title;
	}
	
	public void setName(String name) {
		this.title = name;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}

	public Integer getLengthInSeconds() {
		return lengthInSeconds;
	}

	public void setLengthInSeconds(Integer lengthInSeconds) {
		this.lengthInSeconds = lengthInSeconds;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}