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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import uk.co.caprica.vlcj.binding.internal.libvlc_media_t;
import uk.co.caprica.vlcj.medialist.MediaList;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.DefaultFullScreenStrategy;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.FullScreenStrategy;
import uk.co.caprica.vlcj.player.list.MediaListPlayer;
import uk.co.caprica.vlcj.player.list.MediaListPlayerEventListener;
import uk.co.caprica.vlcj.player.list.MediaListPlayerMode;

public class Player implements KeyListener {
	protected EmbeddedMediaPlayer mediaPlayer;
	protected MediaListPlayer mediaListPlayer;
	protected MediaList playlist;
	protected Pattern idParser;
	
	protected PrintWriter log;
	
	protected JFrame frame;
	protected JPanel panel;
	protected Canvas canvas;
	
	protected String pid = "0";
	
	public void sendEvent(String event, String id) {
		Client client = Client.create();
		 
		WebResource webResource = client
		   .resource("http://localhost:8080/AFTSurvey/event/trigger");
		
		if (idParser != null) {
			Matcher idParserMatcher = idParser.matcher(id);
			
			if (idParserMatcher.find()) {
				id = idParserMatcher.group(1);
			}
		}
		
		log.println("ID: " + id);
 
		String input = String.format("{\"event\":\"%s\",\"vid\":\"%s\",\"pid\":\"%s\"}", event, id, pid);
 
		ClientResponse response = webResource.type("application/json")
		   .post(ClientResponse.class, input);
 
		if (response.getStatus() != 201) {
			throw new RuntimeException("Failed : HTTP error code : "
			     + response.getStatus());
		}
	}
	
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

    public static void enableOSXFullscreen(Window window) {
        try {
            Class<?> util = Class.forName("com.apple.eawt.FullScreenUtilities");

            Class<?> params[] = new Class[] { Window.class, Boolean.TYPE };
            Method method = util.getMethod("setWindowCanFullScreen", params);
            method.invoke(util, window, true);
        } catch (ClassNotFoundException e1) {

        } catch (Exception e) {

        }
    }
    
    public Player() {
    	this(null);
    }
	
    public Player(String logoFile) {
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
        playlist = mediaPlayerFactory.newMediaList();
        mediaListPlayer.setMediaList(playlist);
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
				String id = itemMrl.substring(beginIndex, endIndex);
				
				log.println("NEXT ITEM: " + id);
				log.flush();
				
				sendEvent("change", id);
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
    
    public void setLoop(boolean loop) {
    	MediaListPlayerMode mode;
    	if (loop) {
    		mode = MediaListPlayerMode.LOOP;
    	} else {
    		mode = MediaListPlayerMode.DEFAULT;
    	}
    	
    	mediaListPlayer.setMode(mode);
    }
    
    public void play() {
    	mediaListPlayer.play();
    }
    
    public void addVideo(String file) {
    	playlist.addMedia(file);
    }
    
    public void addPlaylist(Playlist playlist) {
    	for (String filename : playlist.getFileNames()) {
    		addVideo(filename);
    	}
    }
    
    public void setLogo(String file, int x, int y) {
    	mediaPlayer.setLogoFile(file);
    	mediaPlayer.setLogoLocation(x, y);
    	mediaPlayer.setLogoOpacity(255);
    }
    
	public void setIdParserPattern(String pattern) {
		idParser = Pattern.compile(pattern);
	}
	
	public void setPlaylistId(String pid) {
		this.pid = pid;
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