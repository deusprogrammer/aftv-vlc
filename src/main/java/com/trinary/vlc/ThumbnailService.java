package com.trinary.vlc;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class ThumbnailService {
	
	public String generateThumbnail(Playlist playlist, PlaylistEntry entry) throws IOException, InterruptedException {
		String filepath = entry.getUrl();
		String imageDirectory = ".playlist-cache/" + playlist.getUuid() + "/";
		String imagePath = imageDirectory + entry.getUuid() + ".png";
		
		File directory = new File(imageDirectory);
		if (!directory.mkdirs() && !directory.exists()) {
			throw new IOException("Unable to create directory: " + imageDirectory);
		}
		
		File imageFile = new File(imagePath);
		if (imageFile.exists()) {
			return imagePath;
		}
		
		ProcessBuilder pb = new ProcessBuilder(
				"ffmpeg", 
				"-i", filepath, 
				"-ss", "00:00:10.000",
				"-vframes", "1",
				"-v", "quiet",
				imagePath);
		Process p = pb.start();
		
		BufferedReader errors = new BufferedReader(new InputStreamReader(p.getErrorStream()));
		BufferedReader stdin  = new BufferedReader(new InputStreamReader(p.getInputStream()));
		
		String line = errors.readLine();
		while (line != null) {
			System.out.println(line);
			line = errors.readLine();
		}
		
		line = stdin.readLine();
		while (line != null) {
			System.out.println(line);
			line = stdin.readLine();
		}
		
		p.waitFor();
		
		return imagePath;
	}
}