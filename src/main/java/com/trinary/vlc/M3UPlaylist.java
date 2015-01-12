package com.trinary.vlc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class M3UPlaylist extends Playlist {

	public M3UPlaylist(String filename) {
		super(filename);
	}
	
	public M3UPlaylist(String filename, String titleStringPattern) {
		super(filename, titleStringPattern);
	}

	@Override
	protected void parseFile(String filename) throws IOException {
		Pattern extInfPattern = Pattern.compile("#EXTINF:([0-9]+),(.*)");
		Pattern extM3uPattern = Pattern.compile("#EXTM3U");
		File file = new File(filename);
		
		FileReader fileReader = new FileReader(file);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		
		String line;
		String title ="", url = "", lengthInSeconds = "";
		String id = "";
		Boolean validFile = false;
		
		while ((line = bufferedReader.readLine()) != null) {
			Matcher extInfMatcher = extInfPattern.matcher(line);
			Matcher extM3uMatcher = extM3uPattern.matcher(line);
			if (extInfMatcher.matches()) {
				lengthInSeconds = extInfMatcher.group(1);
				title = extInfMatcher.group(2);
				if (titleStringPattern != null) {
					Matcher titleStringMatcher = titleStringPattern.matcher(title);
					if (titleStringMatcher.find()) {
						id = titleStringMatcher.group(1);
					}
				} else {
					id = title;
				}
			} else if (extM3uMatcher.matches()) {
				validFile = true;
			} else {
				url = line;
				entries.add(new PlaylistEntry(id, title, url, Integer.getInteger(lengthInSeconds)));
			}
		}
		bufferedReader.close();
		
		if (!validFile) {
			
			throw new IOException("Invalid m3u format");
		}
	}
}