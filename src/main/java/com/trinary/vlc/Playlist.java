package com.trinary.vlc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.codehaus.jackson.annotate.JsonIgnore;

public abstract class Playlist implements Contest {
	protected Pattern titleStringPattern;
	protected List<PlaylistEntry> entries = new ArrayList<PlaylistEntry>();
	protected int index = 0;
	protected String pid = "";
	
	protected String title = "Video Contest";
	protected String description = "A Video Contest";
	
	public Playlist(String filename) {
		try {
			parseFile(filename);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Playlist(String filename, String titleStringPattern) {
		this.titleStringPattern = Pattern.compile(titleStringPattern);
		try {
			parseFile(filename);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public List<PlaylistEntry> getPlaylistEntries() {
		return entries;
	}

	public void setPlaylistEntries(List<PlaylistEntry> entries) {
		this.entries = entries;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	@JsonIgnore
	public PlaylistEntry getPrevious() {
		if (!hasItem(index-1)) {
			return null;
		}
		
		return entries.get(--index);
	}
	
	@JsonIgnore
	public PlaylistEntry getNext() {
		if (!hasItem(index+1)) {
			return null;
		}
		
		return entries.get(++index);
	}
	
	public PlaylistEntry getItem(int index) {
		if (!hasItem(index)) {
			return null;
		}
		
		return entries.get(index);
	}
	
	@JsonIgnore
	public List<String> getFileNames() {
		List<String> filenames = new ArrayList<String>();
		
		for (PlaylistEntry entry : entries) {
			filenames.add(entry.getUrl());
		}
		
		return filenames;
	}
	
	public boolean hasItem(int index) {
		return entries.size() > index;
	}
	
	protected abstract void parseFile(String filename) throws IOException;

	public String getUuid() {
		return pid;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public List<ContestEntry> getEntries() {
		return new ArrayList<ContestEntry>(this.entries);
	}
}