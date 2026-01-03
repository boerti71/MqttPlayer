package com.player;

import javazoom.jl.player.Player;
import java.io.FileInputStream;

public class MP3Player {

	Player player;
	public MP3Player() {
		super();
	}

	public void play(String sFile) {
		try {
			FileInputStream fileInputStream = new FileInputStream(sFile);
			player = new Player(fileInputStream);
			player.play();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}