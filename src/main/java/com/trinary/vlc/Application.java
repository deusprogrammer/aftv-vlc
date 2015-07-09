package com.trinary.vlc;

import javax.swing.SwingUtilities;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;

import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;

public class Application {
	protected final static RESTService restService = new RESTServiceImpl();
	
	protected final static String LIB_MAC = "/Applications/VLC.app/Contents/MacOS/lib";
	protected final static String LIB_WIN = "C:\\Program Files\\VLC\\lib";

	public static void main(String[] args) throws Exception {
		if (OS.isMac()) {
			NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), LIB_MAC);
		} else if (OS.isWindows()) {
			throw new Exception("Windows is not currently supported");
		} else if (OS.isUnix()) {
			throw new Exception("Linux/Unix is not currently supported");
		}
		Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);
		
		final Playlist playlist = new M3UPlaylist("AMV Playlist.m3u", "([0-9A-Z]+).*");
				
    	// Create the contest on the back end.
    	restService.createContest((Contest)playlist);
    	
    	// Generate local thumbnails and attempt to publish.
    	ThumbnailService thumbService = new ThumbnailService();
    	for (PlaylistEntry entry : playlist.getPlaylistEntries()) {
    		String thumbnailPath = thumbService.generateThumbnail(playlist, entry);
    		restService.uploadThumbnail(playlist, entry, thumbnailPath);
    	}
    	
    	// Create player
    	final AFTVPlayer player = new AFTVPlayerImpl() {
			@Override
			public void handleEvent(PlayerEvent event) {
				restService.sendTrigger(event);
			}
    	};
    	
    	// Add playlist to player
		player.addPlaylist(playlist);
		
		// Start player
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				player.play();
			}
		});
		
		// Start heartbeat
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				while (true) {					
					// Send heartbeat event
					if (player.isPlaying()) {
						PlayerEvent event = new PlayerEvent();
						event.setPlaylist(playlist);
						event.setEntry(null);
						event.setType(PlayerEventType.HEARTBEAT);
						restService.sendTrigger(event);
						
						// Sleep for 10 seconds
						try {
							Thread.sleep(10000);
						} catch (InterruptedException e) {
							continue;
						}
					} else {
						// Sleep for 1 second
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							continue;
						}
					}
				}
			}
		});
	}
}