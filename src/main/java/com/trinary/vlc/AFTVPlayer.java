package com.trinary.vlc;

public interface AFTVPlayer {
	void setLoop(boolean loop);
	void play();
	void addVideo(String file);
	void setPlaylist(Playlist playlist);
	void addPlaylist(Playlist playlist);
	void setFilenameTemplate(String filenameTemplate);

}