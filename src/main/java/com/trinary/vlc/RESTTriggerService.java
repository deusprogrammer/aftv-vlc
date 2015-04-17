package com.trinary.vlc;

public interface RESTTriggerService {
	void createContest(Contest contest);
	void sendTrigger(Contest contest, ContestEntry entry, String trigger);
}