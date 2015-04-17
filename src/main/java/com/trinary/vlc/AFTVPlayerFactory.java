package com.trinary.vlc;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class AFTVPlayerFactory {
	protected static ApplicationContext context;
	
	static {
		context = new ClassPathXmlApplicationContext(
				"applicationContext.xml");
	}
	
	public static AFTVPlayer getPlayer() {
		return (AFTVPlayer)context.getBean("player");
	}
}