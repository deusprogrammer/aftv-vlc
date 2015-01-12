package com.trinary.vlc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public abstract class Playlist {
	protected Pattern titleStringPattern;
	protected List<PlaylistEntry> entries = new ArrayList<PlaylistEntry>();
	protected int index = 0;
	
	public Playlist(String filename) {
		try {
			parseFile(filename);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Playlist(String filename, String titleStringPattern) {
		this(filename);
		this.titleStringPattern = Pattern.compile(titleStringPattern);
	}
	
	public PlaylistEntry getPrevious() {
		if (!hasItem(index-1)) {
			return null;
		}
		
		return entries.get(--index);
	}
	
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
}