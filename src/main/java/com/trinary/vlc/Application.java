package com.trinary.vlc;

import javax.swing.SwingUtilities;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;

import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;

public class Application {
	protected final static String LIB_LOCATION="/Applications/VLC.app/Contents/MacOS/lib";

	public static void main(String[] args) {
		NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), LIB_LOCATION);
		Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);
		
		final Player player = new Player("aftv-200X69.png");
		player.addVideo("test1.mp4");
		player.addVideo("test2.mp4");
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				player.play();
			}
		});
	}
}
