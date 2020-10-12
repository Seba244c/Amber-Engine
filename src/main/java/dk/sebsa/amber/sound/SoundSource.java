package dk.sebsa.amber.sound;

import static org.lwjgl.openal.AL10.*;

import dk.sebsa.amber.math.Vector2f;
import dk.sebsa.amber.math.Vector3f;

public class SoundSource {
	private final int sourceId;
	
	public SoundSource(boolean loop, boolean relative) {
        this.sourceId = alGenSources();
        if (loop) {
            alSourcei(sourceId, AL_LOOPING, AL_TRUE);
        }
        if (relative) {
            alSourcei(sourceId, AL_SOURCE_RELATIVE, AL_TRUE);
        }
        SoundManager.instance.addSoundSource(String.valueOf(sourceId), this);
    }

    public void setClip(AudioClip ac) {
        stop();
        alSourcei(sourceId, AL_BUFFER, ac.getBufferId());
    }

    public void setPosition(Vector2f position) {
        alSource3f(sourceId, AL_POSITION, position.x, position.y, 0);
    }

    public void setSpeed(Vector3f speed) {
        alSource3f(sourceId, AL_VELOCITY, speed.x, speed.y, speed.z);
    }

    public void setGain(float gain) {
        alSourcef(sourceId, AL_GAIN, gain);
    }

    public void setProperty(int param, float value) {
        alSourcef(sourceId, param, value);
    }

    public void play() {
        alSourcePlay(sourceId);
    }

    public boolean isPlaying() {
        return alGetSourcei(sourceId, AL_SOURCE_STATE) == AL_PLAYING;
    }

    public void pause() {
        alSourcePause(sourceId);
    }

    public void stop() {
        alSourceStop(sourceId);
    }

    public void cleanup() {
        stop();
        alDeleteSources(sourceId);
    }
}
