package com.brahvim.nerd.openal.objects;

import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALC11;

public class AlContextSettings {

	// OpenAL default values:
	public int frequency = 44100, monoSources = 32, stereoSources = 8, refresh = 40;
	public boolean sync; // `false` by default for OpenAL.

	public int[] asAttribArray() {
		return new int[] {
				ALC10.ALC_FREQUENCY, this.frequency,
				ALC11.ALC_MONO_SOURCES, this.monoSources,
				ALC11.ALC_STEREO_SOURCES, this.stereoSources,
				ALC10.ALC_REFRESH, this.refresh,
				ALC10.ALC_SYNC, this.sync ? ALC10.ALC_TRUE : ALC10.ALC_FALSE,
		};
	}

}