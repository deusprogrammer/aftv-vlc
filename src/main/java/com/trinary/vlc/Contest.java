package com.trinary.vlc;

import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

public interface Contest {
	public String getUuid();
	public String getTitle();
	public String getDescription();
	
	@JsonSerialize(contentAs=ContestEntry.class)
	public List<ContestEntry> getEntries();
}