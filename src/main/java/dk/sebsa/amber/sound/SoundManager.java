package dk.sebsa.amber.sound;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.openal.ALCCapabilities;

import dk.sebsa.amber.math.Vector2f;
import dk.sebsa.amber.math.Vector3f;

import org.lwjgl.openal.ALC;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.system.MemoryUtil.NULL;
import org.lwjgl.openal.AL;

public class SoundManager {
	private long device;
    private long context;
    private SoundListener listener;
    private final List<AudioClip> audioClips;
    private final Map<String, SoundSource> soundSourceMap;

    public SoundManager() {
    	audioClips = new ArrayList<>();
        soundSourceMap = new HashMap<>();
    }
    
    public void init() throws Exception {
        this.device = alcOpenDevice((ByteBuffer) null);
        if (device == NULL) {
            throw new IllegalStateException("Failed to open the default OpenAL device.");
        }
        ALCCapabilities deviceCaps = ALC.createCapabilities(device);
        this.context = alcCreateContext(device, (IntBuffer) null);
        if (context == NULL) {
            throw new IllegalStateException("Failed to create OpenAL context.");
        }
        alcMakeContextCurrent(context);
        AL.createCapabilities(deviceCaps);
    }

    public void addSoundSource(String name, SoundSource soundSource) {
        this.soundSourceMap.put(name, soundSource);
    }

    public SoundSource getSoundSource(String name) {
        return this.soundSourceMap.get(name);
    }

    public void playSoundSource(String name) {
        SoundSource soundSource = this.soundSourceMap.get(name);
        if (soundSource != null && !soundSource.isPlaying()) {
            soundSource.play();
        }
    }

    public void removeSoundSource(String name) {
        this.soundSourceMap.remove(name);
    }

    public void addAudioClip(AudioClip ac) {
        this.audioClips.add(ac);
    }

    public SoundListener getListener() {
        return this.listener;
    }

    public void setListener(SoundListener listener) {
        this.listener = listener;
    }

    public void updateListenerPosition(Vector2f p) {        
        listener.setPosition(new Vector3f(p.x, p.y, 0));
    }
    
    public void cleanup() {
        for (SoundSource soundSource : soundSourceMap.values()) {
            soundSource.cleanup();
        }
        soundSourceMap.clear();
        
        for (AudioClip ac : audioClips) {
            ac.cleanup();
        }
        audioClips.clear();
        
        if (context != NULL) {
            alcDestroyContext(context);
        }
        if (device != NULL) {
            alcCloseDevice(device);
        }
    }
}
