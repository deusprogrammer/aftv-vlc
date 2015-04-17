package com.trinary.vlc;

import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(as=ContestEntry.class)
public class PlaylistEntry implements ContestEntry {
	protected String id;
	protected String title;
	protected String artist;
	protected String url;
	protected Integer lengthInSeconds;
	
	protected String description = "A Video";
	
	public PlaylistEntry(String id, String title, String artist, String url, Integer lengthInSeconds) {
		super();
		this.id = id;
		this.artist = artist;
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

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUuid() {
		return id;
	}

	public String getDescription() {
		return description;
	}
}