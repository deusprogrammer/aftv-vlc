package com.trinary.vlc;

public class PlayerEvent {
	protected Playlist playlist;
	protected PlaylistEntry entry;
	protected PlayerEventType type;
	/**
	 * @return the entry
	 */
	public PlaylistEntry getEntry() {
		return entry;
	}
	/**
	 * @param foundEntry the entry to set
	 */
	public void setEntry(PlaylistEntry foundEntry) {
		this.entry = foundEntry;
	}
	/**
	 * @return the type
	 */
	public PlayerEventType getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(PlayerEventType type) {
		this.type = type;
	}
	/**
	 * @return the contest
	 */
	public Playlist getPlaylist() {
		return playlist;
	}
	/**
	 * @param contest the contest to set
	 */
	public void setPlaylist(Playlist playlist) {
		this.playlist = playlist;
	}
}