package dk.sebsa.amber.sound;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import static org.lwjgl.openal.AL10.*;
import org.lwjgl.stb.STBVorbisInfo;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.stb.STBVorbis.*;
import org.lwjgl.system.MemoryStack;

import dk.sebsa.amber.util.FileUtils;
import static org.lwjgl.system.MemoryUtil.*;

public class AudioClip {
	private final int bufferId;
    private ShortBuffer pcm = null;
	public final String name;
	
    private static List<AudioClip> audioClips = new ArrayList<AudioClip>();
	private static int i;
    
	public AudioClip(String file, SoundManager sm) throws IOException {
		this.bufferId = alGenBuffers();
		String[] nameSplit = file.split("\\\\");
		name = nameSplit[nameSplit.length-1];
        try (STBVorbisInfo info = STBVorbisInfo.malloc()) {
            ShortBuffer pcm = readVorbis(file, 32 * 1024, info);

            // Copy to buffer
            alBufferData(bufferId, info.channels() == 1 ? AL_FORMAT_MONO16 : AL_FORMAT_STEREO16, pcm, info.sample_rate());
        }
        sm.addAudioClip(this);
        audioClips.add(this);
    }

    public int getBufferId() {
        return this.bufferId;
    }

    public void cleanup() {
    	alDeleteBuffers(this.bufferId);
        if (pcm != null) {
            memFree(pcm);
        }
    }
    
    private ShortBuffer readVorbis(String resource, int bufferSize, STBVorbisInfo info) throws IOException {
        try (MemoryStack stack = MemoryStack.stackPush()) {
        	ByteBuffer vorbis = FileUtils.ioResourceToByteBuffer(resource, bufferSize);
            IntBuffer error = stack.mallocInt(1);
            long decoder = stb_vorbis_open_memory(vorbis, error, null);
            if (decoder == NULL) {
                throw new IOException("Failed to open Ogg Vorbis file. Error: " + error.get(0));
            }

            stb_vorbis_get_info(decoder, info);

            int channels = info.channels();

            int lengthSamples = stb_vorbis_stream_length_in_samples(decoder);

            pcm = memAllocShort(lengthSamples);

            pcm.limit(stb_vorbis_get_samples_short_interleaved(decoder, channels, pcm) * channels);
            stb_vorbis_close(decoder);

            return pcm;
        }
    }
    
    public static AudioClip getClip(String name) {
    	for(i = 0; i < audioClips.size(); i++) {
    		if(audioClips.get(i).name.equals(name)) return audioClips.get(i);
    	}
    	return null;
    }
}
