package com.player;

import javax.sound.sampled.*;
import java.io.File;

public class WavPlayer {
	private Clip audioClip;

	public WavPlayer() {
		super();
	}

    public void play(String sFile) throws Exception {
        audioClip = AudioSystem.getClip();
        audioClip.addLineListener(event -> {
            if(LineEvent.Type.STOP.equals(event.getType())) {
            	audioClip.close();
            }
        });
        AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File(sFile));
        audioClip.open(audioStream);
        audioClip.start();
    }

    public void stop() {
        audioClip.stop();
    }
}