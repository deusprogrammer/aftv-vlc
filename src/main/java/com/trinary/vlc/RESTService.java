package com.trinary.vlc;

import java.io.IOException;

public interface RESTService {
	void createContest(Contest contest);
	void sendTrigger(PlayerEvent playerEvent);
	void uploadThumbnail(Contest contest, ContestEntry entry, String thumbnailPath) throws IOException;
}