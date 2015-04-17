package com.trinary.vlc;

import javax.swing.SwingUtilities;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;

import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;

public class Application {
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
		
		final AFTVPlayer player = AFTVPlayerFactory.getPlayer();
		player.addPlaylist(new M3UPlaylist("AMV Playlist.m3u", "([0-9A-Z]+).*"));
		
		/*
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				player.play();
			}
		});
		*/
	}
}