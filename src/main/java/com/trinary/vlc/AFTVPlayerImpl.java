package com.trinary.vlc;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.trinary.util.StringUtil;

import uk.co.caprica.vlcj.binding.internal.libvlc_media_t;
import uk.co.caprica.vlcj.medialist.MediaList;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.DefaultFullScreenStrategy;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.FullScreenStrategy;
import uk.co.caprica.vlcj.player.list.MediaListPlayer;
import uk.co.caprica.vlcj.player.list.MediaListPlayerEventListener;
import uk.co.caprica.vlcj.player.list.MediaListPlayerMode;

public class AFTVPlayerImpl implements KeyListener, AFTVPlayer {
	@Autowired
	@Qualifier("restTriggerService")
	RESTTriggerService restService;
	
	protected String filenameTemplate = "{uuid} - {trackNumber} - {artist} - {trackname}";
	
	protected EmbeddedMediaPlayer mediaPlayer;
	protected MediaListPlayer mediaListPlayer;
	protected MediaList mediaList;
	protected Pattern idParser;
	
	protected Playlist playlist;
	
	protected PrintWriter log;
	
	protected JFrame frame;
	protected JPanel panel;
	protected Canvas canvas;
	
	protected String pid = "";
	
	public static void toggleOSXFullscreen(Window window) {
        try {
            Class<?> util = Class.forName("com.apple.eawt.Application");
            Object o = util.newInstance();

            Class<?> params[] = new Class[] { Window.class };
            Method method = util.getDeclaredMethod("requestToggleFullScreen",
                    params);
            method.invoke(o, window);
        } catch (ClassNotFoundException e1) {

        } catch (Exception e) {

        }
    }

    protected static void enableOSXFullscreen(Window window) {
        try {
            Class<?> util = Class.forName("com.apple.eawt.FullScreenUtilities");

            Class<?> params[] = new Class[] { Window.class, Boolean.TYPE };
            Method method = util.getMethod("setWindowCanFullScreen", params);
            method.invoke(util, window, true);
        } catch (ClassNotFoundException e1) {

        } catch (Exception e) {

        }
    }
    
    public AFTVPlayerImpl() {
    	this(null);
    }
	
    public AFTVPlayerImpl(String logoFile) {
    	try {
			log = new PrintWriter("out.log", "UTF-8");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
    	
        frame = new JFrame("AFTV Player");
        
        MediaPlayerFactory mediaPlayerFactory = new MediaPlayerFactory();

        canvas = new Canvas(); 
        canvas.setBackground(Color.black);
        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(canvas, BorderLayout.CENTER);
        
        frame.add(panel, BorderLayout.CENTER);
        
        FullScreenStrategy fullScreenStrategy = new DefaultFullScreenStrategy(
                frame) {
            @Override
            public void enterFullScreenMode() {
            	if (OS.isMac()) {
            		toggleOSXFullscreen(frame);
            	}
            }
        };
        
        frame.addKeyListener(this);
        
        mediaPlayer = mediaPlayerFactory.newEmbeddedMediaPlayer(fullScreenStrategy);
        mediaPlayer.setVideoSurface(mediaPlayerFactory.newVideoSurface(canvas));
        if (logoFile != null) {
        	String[] standardMediaOptions = {"video-filter=logo", "logo-file=" + logoFile, "logo-opacity=100"};
        	mediaPlayer.setStandardMediaOptions(standardMediaOptions);
        }
        mediaListPlayer = mediaPlayerFactory.newMediaListPlayer();
        mediaListPlayer.setMediaPlayer(mediaPlayer);
        mediaList = mediaPlayerFactory.newMediaList();
        mediaListPlayer.setMediaList(mediaList);
        frame.setLocation(100, 100);
        frame.setSize(1050, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        if (OS.isMac()) {
        	enableOSXFullscreen(frame);
        }
        
        frame.setVisible(true);
        
        mediaListPlayer.addMediaListPlayerEventListener(new MediaListPlayerEventListener() {

			public void played(MediaListPlayer mediaListPlayer) {}

			public void nextItem(MediaListPlayer mediaListPlayer,
					libvlc_media_t item, String itemMrl) {
				int beginIndex = itemMrl.lastIndexOf("/") + 1;
				int endIndex   = itemMrl.lastIndexOf(".");
				String filename = itemMrl.substring(beginIndex, endIndex);
				
				Map<String, String> map = StringUtil.extrapolateString(filenameTemplate, filename);
				for (ContestEntry entry : playlist.getEntries()) {
					if (entry.getUuid().equals(map.get("uuid"))) {
						restService.sendTrigger(playlist, entry, "next");
						return;
					}
				}
			}

			public void stopped(MediaListPlayer mediaListPlayer) {}

			public void mediaMetaChanged(MediaListPlayer mediaListPlayer,
					int metaType) {}

			public void mediaSubItemAdded(MediaListPlayer mediaListPlayer,
					libvlc_media_t subItem) {}

			public void mediaDurationChanged(MediaListPlayer mediaListPlayer,
					long newDuration) {}

			public void mediaParsedChanged(MediaListPlayer mediaListPlayer,
					int newStatus) {}

			public void mediaFreed(MediaListPlayer mediaListPlayer) {}

			public void mediaStateChanged(MediaListPlayer mediaListPlayer,
					int newState) {}
        	
        });
    }
    
	public void setFilenameTemplate(String filenameTemplate) {
		this.filenameTemplate = filenameTemplate;
	}
    
    /* (non-Javadoc)
	 * @see com.trinary.vlc.AFTVPlayer#setLoop(boolean)
	 */
    public void setLoop(boolean loop) {
    	MediaListPlayerMode mode;
    	if (loop) {
    		mode = MediaListPlayerMode.LOOP;
    	} else {
    		mode = MediaListPlayerMode.DEFAULT;
    	}
    	
    	mediaListPlayer.setMode(mode);
    }
    
    /* (non-Javadoc)
	 * @see com.trinary.vlc.AFTVPlayer#play()
	 */
    public void play() {
    	mediaListPlayer.play();
    }
    
    /* (non-Javadoc)
	 * @see com.trinary.vlc.AFTVPlayer#addVideo(java.lang.String)
	 */
    public void addVideo(String file) {
    	mediaList.addMedia(file);
    }
    
    /* (non-Javadoc)
	 * @see com.trinary.vlc.AFTVPlayer#setPlaylist(com.trinary.vlc.Playlist)
	 */
    public void setPlaylist(Playlist playlist) {
    	this.mediaList.clear();
    	addPlaylist(playlist);
    }
    
    /* (non-Javadoc)
	 * @see com.trinary.vlc.AFTVPlayer#addPlaylist(com.trinary.vlc.Playlist)
	 */
    public void addPlaylist(Playlist playlist) {
    	if (this.mediaList.size() == 0) {
    		this.pid = playlist.getPid();
    	}
    	
    	restService.createContest((Contest)playlist);
    	
    	for (String filename : playlist.getFileNames()) {
    		addVideo(filename);
    	}
    	this.playlist = playlist;
    }

	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void keyReleased(KeyEvent e) {
		int volume = mediaPlayer.getVolume();
		boolean isPlaying = mediaListPlayer.isPlaying();
		switch(e.getKeyCode()) {
		case KeyEvent.VK_W:
			mediaPlayer.setVolume(volume + 10);
			break;
		case KeyEvent.VK_S:
			mediaPlayer.setVolume(volume - 10);
			break;
		case KeyEvent.VK_A:
			mediaListPlayer.playPrevious();
			break;
		case KeyEvent.VK_D:
			mediaListPlayer.playNext();
			break;
		case KeyEvent.VK_P:
			if (isPlaying) {
				mediaListPlayer.pause();
			} else {
				mediaListPlayer.play();
			}
		}
	}
}