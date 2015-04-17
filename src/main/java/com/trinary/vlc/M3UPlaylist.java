package com.trinary.vlc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.trinary.util.StringUtil;

public class M3UPlaylist extends Playlist {

	public M3UPlaylist(String filename) {
		super(filename);
	}
	
	public M3UPlaylist(String filename, String titleStringPattern) {
		super(filename, titleStringPattern);
	}

	@Override
	protected void parseFile(String filename) throws IOException {
		Pattern extPidPattern = Pattern.compile("#PID:(.*)");
		Pattern extInfPattern = Pattern.compile("#EXTINF:([0-9]+),(.*)");
		Pattern extM3uPattern = Pattern.compile("#EXTM3U");
		File file = new File(filename);
		
		FileReader fileReader = new FileReader(file);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		
		String line;
		String title ="", url = "", lengthInSeconds = "";
		Boolean validFile = false;
		
		while ((line = bufferedReader.readLine()) != null) {
			Matcher extInfMatcher = extInfPattern.matcher(line);
			Matcher extM3uMatcher = extM3uPattern.matcher(line);
			Matcher extPidMatcher = extPidPattern.matcher(line);
			if (extInfMatcher.matches()) {
				lengthInSeconds = extInfMatcher.group(1);
				title = extInfMatcher.group(2);
				if (titleStringPattern != null) {
					Matcher titleStringMatcher = titleStringPattern.matcher(title);
					if (titleStringMatcher.find()) {
					}
				} else {
				}
			} else if (extM3uMatcher.matches()) {
				validFile = true;
			} else if (extPidMatcher.matches()) {
				pid = extPidMatcher.group(1);
				System.out.println("FOUND PID: " + pid);
			} else {
				url = line;
				int beginIndex = url.lastIndexOf("/") + 1;
				int endIndex   = url.lastIndexOf(".");
				String s = url.substring(beginIndex, endIndex);
				
				Map<String, String> map = StringUtil.extrapolateString("{uuid} - {trackNumber} - {artist} - {trackname}", s);
				entries.add(new PlaylistEntry(map.get("uuid"), map.get("trackname"), map.get("artist"), url, Integer.getInteger(lengthInSeconds)));
			}
		}
		bufferedReader.close();
		
		if (!validFile) {
			throw new IOException("Invalid m3u format");
		}
	}
}