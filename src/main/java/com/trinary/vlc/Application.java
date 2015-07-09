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
		
		Playlist playlist = new M3UPlaylist("AMV Playlist.m3u", "([0-9A-Z]+).*");
				
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
	}
}