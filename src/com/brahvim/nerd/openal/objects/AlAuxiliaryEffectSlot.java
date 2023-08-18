package com.brahvim.nerd.openal.objects;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.lwjgl.openal.AL10;
import org.lwjgl.openal.EXTEfx;
import org.lwjgl.system.MemoryStack;

import com.brahvim.nerd.openal.al_exceptions.AlUnflaggedException;
import com.brahvim.nerd.openal.al_ext_efx.al_effects.AlAutowah;
import com.brahvim.nerd.openal.al_ext_efx.al_effects.AlChorus;
import com.brahvim.nerd.openal.al_ext_efx.al_effects.AlCompressor;
import com.brahvim.nerd.openal.al_ext_efx.al_effects.AlDistortion;
import com.brahvim.nerd.openal.al_ext_efx.al_effects.AlEaxReverb;
import com.brahvim.nerd.openal.al_ext_efx.al_effects.AlEcho;
import com.brahvim.nerd.openal.al_ext_efx.al_effects.AlEqualizer;
import com.brahvim.nerd.openal.al_ext_efx.al_effects.AlFlanger;
import com.brahvim.nerd.openal.al_ext_efx.al_effects.AlFrequencyShifter;
import com.brahvim.nerd.openal.al_ext_efx.al_effects.AlPitchShifter;
import com.brahvim.nerd.openal.al_ext_efx.al_effects.AlReverb;
import com.brahvim.nerd.openal.al_ext_efx.al_effects.AlRingModulator;

public class AlAuxiliaryEffectSlot extends AlNativeResource<Integer> {

	/*
	 * let arr = [
	 * "DENSITY",
	 * "DIFFUSION",
	 * "GAIN",
	 * "GAINHF",
	 * "GAINLF",
	 * "DECAY_TIME",
	 * "DECAY_HFRATIO",
	 * "DECAY_LFRATIO",
	 * "REFLECTIONS_GAIN",
	 * "REFLECTIONS_DELAY",
	 * "REFLECTIONS_PAN",
	 * "LATE_REVERB_GAIN",
	 * "LATE_REVERB_DELAY",
	 * "LATE_REVERB_PAN",
	 * "ECHO_TIME",
	 * "ECHO_DEPTH",
	 * "MODULATION_TIME",
	 * "MODULATION_DEPTH",
	 * "AIR_ABSORPTION_GAINHF",
	 * "HFREFERENCE",
	 * "ROOM_ROLLOFF_FACTOR",
	 * "DECAYHF_LIMIT"
	 * ];
	 * 
	 * let camelCaseArr = [];
	 * 
	 * for (let i = 0; i < arr.length; i++) {
	 * let s = arr[i];
	 * let split = s.split('_');
	 * 
	 * if (split.length == 1)
	 * camelCaseArr[i] = s.toLowerCase();
	 * continue;
	 * 
	 * let result;
	 * for (let j = 0; j < split.length; j++) {
	 * let t = split[j].toLowerCase();
	 * result = '';
	 * 
	 * if (j > 0)
	 * result.charAt(1).toUpperCase()
	 * .concat(t.substring(1, t.length));
	 * }
	 * }
	 * 
	 * for (let i = 0; i < arr.length; i++)
	 * console.log(`public void set${camelCaseArr[i]}(float p_value) {
	 * super.setFloat(EXTEfx.AL_${"REVERB"}_${arr[i]}, p_value);
	 * }`);
	 */

	// region Fields.
	protected static final Vector<AlAuxiliaryEffectSlot> ALL_INSTANCES = new Vector<>(0);

	protected AlEffect effect;
	protected AlSource source;
	// endregion

	// region Constructors.
	public AlAuxiliaryEffectSlot(final NerdAl p_alMan) {
		super(p_alMan);
		super.id = EXTEfx.alGenAuxiliaryEffectSlots();
		super.MAN.checkAlError();

		this.effect = super.MAN.DEFAULTS.effect;
		AlAuxiliaryEffectSlot.ALL_INSTANCES.add(this);
	}

	public AlAuxiliaryEffectSlot(final NerdAl p_alMan, final AlEffect p_effect) {
		super(p_alMan);
		super.id = EXTEfx.alGenAuxiliaryEffectSlots();
		super.MAN.checkAlError();

		this.setEffect(p_effect);
		AlAuxiliaryEffectSlot.ALL_INSTANCES.add(this);
	}

	public AlAuxiliaryEffectSlot(final NerdAl p_alMan, final int p_id) {
		super(p_alMan);
		super.id = p_id;
		super.MAN.checkAlError();
		AlAuxiliaryEffectSlot.ALL_INSTANCES.add(this);
	}

	public AlAuxiliaryEffectSlot(final NerdAl p_alMan, final int p_id, final AlEffect p_effect) {
		super(p_alMan);
		super.id = p_id;
		super.MAN.checkAlError();

		this.setEffect(p_effect);
		AlAuxiliaryEffectSlot.ALL_INSTANCES.add(this);
	}
	// endregion

	// region Instance collection queries.
	public static int getNumInstances() {
		return AlAuxiliaryEffectSlot.ALL_INSTANCES.size();
	}

	public static List<AlAuxiliaryEffectSlot> getAllInstances() {
		return new ArrayList<>(AlAuxiliaryEffectSlot.ALL_INSTANCES);
	}
	// endregion

	// region Getters.
	public float getGain() {
		return this.getFloat(EXTEfx.AL_EFFECTSLOT_GAIN);
	}

	public AlSource getSource() {
		return this.source;
	}

	public AlEffect getEffect() {
		return this.effect == null ? super.MAN.DEFAULTS.effect : this.effect;
		// return Objects.requireNonNullElse(this.effect, super.MAN.DEFAULTS.effect);
	}

	// Clearly, this is stupid! Can't dereference `this.effect` when it's `null`!:
	@Deprecated
	protected AlEffect supplyNonExistentEffect() {
		final int id = this.getInt(EXTEfx.AL_EFFECTSLOT_EFFECT);

		if (id == this.effect.id)
			return this.effect;
		else
			for (final AlEffect e : AlEffect.ALL_INSTANCES) {
				if (e.id == id)
					return e;
			}

		final int effectType = EXTEfx.alGetEffecti(id, EXTEfx.AL_EFFECTSLOT_EFFECT);

		// region Construct new effect according to `EFFECT_TYPE`.
		// The JS code that generated the next part. Hee-hee!:
		/*
		 * // I literally copied these from the documentation.
		 * // Edited 'em using VSCode's selection features LOL.
		 * 
		 * let arr = [
		 * "AL_EFFECT_EAXREVERB",
		 * "AL_EFFECT_REVERB",
		 * "AL_EFFECT_CHORUS",
		 * "AL_EFFECT_DISTORTION",
		 * "AL_EFFECT_ECHO",
		 * "AL_EFFECT_FLANGER",
		 * "AL_EFFECT_FREQUENCY_SHIFTER",
		 * "AL_EFFECT_VOCAL_MORPHER",
		 * "AL_EFFECT_PITCH_SHIFTER",
		 * "AL_EFFECT_RING_MODULATOR",
		 * "AL_EFFECT_AUTOWAH",
		 * "AL_EFFECT_COMPRESSOR",
		 * "AL_EFFECT_EQUALIZER",
		 * ];
		 * 
		 * for (let x of arr)
		 * console.log(
		 * 
		 * `else if (EFFECT_TYPE == EXTEfx.${x})
		 * return (T) new ${capitalizeFirstChar(
		 * upperSnakeToCamelCase(x))}() {
		 * 
		 * @Override
		 * protected int getEffectType() {
		 * return EFFECT_TYPE;
		 * }
		 * 
		 * };`);
		 * 
		 * function upperSnakeToCamelCase(p_str) {
		 * p_str = p_str.toLowerCase();
		 * let build = '';
		 * 
		 * const strLen = p_str.length;
		 * let lastUn = 0, secLastUn = 0;
		 * 
		 * for (let i = 0; i != strLen; i++) {
		 * if (p_str.charAt(i) != '_')
		 * continue;
		 * 
		 * secLastUn = lastUn;
		 * lastUn = i;
		 * 
		 * if (secLastUn == 0)
		 * build += p_str.substring(0, i);
		 * else build += capitalizeFirstChar(p_str.substring(
		 * secLastUn + 1, lastUn
		 * ));
		 * }
		 * 
		 * build += capitalizeFirstChar(p_str.substring(1 + p_str.lastIndexOf('_')));
		 * 
		 * return build;
		 * }
		 * 
		 * function capitalizeFirstChar(p_str) {
		 * return p_str.charAt(0).toUpperCase() + p_str.substring(1);
		 * }
		 */

		if (effectType == EXTEfx.AL_EFFECT_EAXREVERB)
			return new AlEaxReverb(super.MAN);

		else if (effectType == EXTEfx.AL_EFFECT_REVERB)
			return new AlReverb(super.MAN);

		else if (effectType == EXTEfx.AL_EFFECT_CHORUS)
			return new AlChorus(super.MAN);

		else if (effectType == EXTEfx.AL_EFFECT_DISTORTION)
			return new AlDistortion(super.MAN);

		else if (effectType == EXTEfx.AL_EFFECT_ECHO)
			return new AlEcho(super.MAN);

		else if (effectType == EXTEfx.AL_EFFECT_FLANGER)
			return new AlFlanger(super.MAN);

		else if (effectType == EXTEfx.AL_EFFECT_FREQUENCY_SHIFTER)
			return new AlFrequencyShifter(super.MAN);

		else if (effectType == EXTEfx.AL_EFFECT_VOCAL_MORPHER) {
			System.err.println(
					"LWJGL has not yet implemented `AL_VOCAL_MORPHER`, sorry.");
			// `return (T) new AlVocalMorpher();`
			return new AlEffect(super.MAN) {
				@Override
				protected int getEffectType() {
					return effectType;
				}
			};
		}

		else if (effectType == EXTEfx.AL_EFFECT_PITCH_SHIFTER)
			return new AlPitchShifter(super.MAN);

		else if (effectType == EXTEfx.AL_EFFECT_RING_MODULATOR)
			return new AlRingModulator(super.MAN);

		else if (effectType == EXTEfx.AL_EFFECT_AUTOWAH)
			return new AlAutowah(super.MAN);

		else if (effectType == EXTEfx.AL_EFFECT_COMPRESSOR)
			return new AlCompressor(super.MAN);

		else if (effectType == EXTEfx.AL_EFFECT_EQUALIZER)
			return new AlEqualizer(super.MAN);

		// Instead of throwing this exception, I could just use reflection to see
		// what classes extend `AlEffect` with the assumption that the default
		// constructor exists in them...
		else
			throw new AlUnflaggedException("""
					No idea what this OpenAL effect is...
					Come to this line and modify Nerd's source to fix this!""");
		// endregion

	}
	// endregion

	// region Setters.
	public AlAuxiliaryEffectSlot setGain(final float p_value) {
		this.setFloat(EXTEfx.AL_EFFECTSLOT_GAIN, p_value);
		return this;
	}

	public AlAuxiliaryEffectSlot setSource(final AlSource p_source) {
		this.source = p_source;
		return this;
	}

	/**
	 * @param p_effect is the {@link AlEffect} object to use.
	 * @return The previous {@link AlEffect} object attached to this
	 *         {@link AlAuxiliaryEffectSlot}. {@link AlEffect#NULL_AL_EFFECT}
	 *         if no {@link AlEffect} is attached.
	 */
	public AlEffect setEffect(final AlEffect p_effect) {
		final AlEffect toRet = this.getEffect();
		this.effect = p_effect == null ? super.MAN.DEFAULTS.effect : p_effect;
		this.effect.slot = this;
		EXTEfx.alAuxiliaryEffectSloti(super.id, EXTEfx.AL_EFFECTSLOT_EFFECT, this.effect.id);
		super.MAN.checkAlError();

		return toRet;
	}

	public AlAuxiliaryEffectSlot setAutoSend(final boolean p_value) {
		EXTEfx.alAuxiliaryEffectSloti(
				super.id, EXTEfx.AL_EFFECTSLOT_AUXILIARY_SEND_AUTO,
				p_value ? AL10.AL_TRUE : AL10.AL_FALSE);
		return this;
	}
	// endregion

	// region C-style OpenAL getters.
	public int getInt(final int p_alEnum) {
		MemoryStack.stackPush();
		final IntBuffer buffer = MemoryStack.stackMallocInt(1);

		if (super.hasDisposed)
			return Integer.MIN_VALUE;

		EXTEfx.alGetAuxiliaryEffectSloti(super.id, p_alEnum, buffer);

		MemoryStack.stackPop();
		super.MAN.checkAlError();

		return buffer.get();
	}

	public int[] getIntVector(final int p_alEnum, final int p_vecSize) {
		MemoryStack.stackPush();
		final IntBuffer buffer = MemoryStack.stackMallocInt(p_vecSize);

		if (super.hasDisposed)
			return new int[0];

		EXTEfx.alGetAuxiliaryEffectSlotiv(super.id, p_alEnum, buffer);

		MemoryStack.stackPop();
		super.MAN.checkAlError();

		return buffer.array();
	}

	public float getFloat(final int p_alEnum) {
		MemoryStack.stackPush();
		final FloatBuffer buffer = MemoryStack.stackMallocFloat(1);

		if (super.hasDisposed)
			return -Float.MAX_VALUE;

		EXTEfx.alGetAuxiliaryEffectSlotf(super.id, p_alEnum, buffer);

		MemoryStack.stackPop();
		super.MAN.checkAlError();

		return buffer.get();
	}

	public float[] getFloatVector(final int p_alEnum, final int p_vecSize) {
		MemoryStack.stackPush();
		final FloatBuffer buffer = MemoryStack.stackMallocFloat(p_vecSize);

		if (super.hasDisposed)
			return new float[0];

		EXTEfx.alGetAuxiliaryEffectSlotfv(super.id, p_alEnum, buffer);

		MemoryStack.stackPop();
		super.MAN.checkAlError();

		return buffer.array();
	}
	// endregion

	// region C-style OpenAL setters.
	public void setInt(final int p_alEnum, final int p_value) {
		EXTEfx.alAuxiliaryEffectSloti(super.id, p_alEnum, p_value);
		super.MAN.checkAlError();
	}

	public void setIntVector(final int p_alEnum, final int... p_values) {
		EXTEfx.alAuxiliaryEffectSlotiv(super.id, p_alEnum, p_values);
		super.MAN.checkAlError();
	}

	public void setFloat(final int p_alEnum, final float p_value) {
		EXTEfx.alAuxiliaryEffectSlotf(super.id, p_alEnum, p_value);
		super.MAN.checkAlError();
	}

	public void setFloatVector(final int p_alEnum, final float... p_values) {
		EXTEfx.alAuxiliaryEffectSlotfv(super.id, p_alEnum, p_values);
		super.MAN.checkAlError();
	}
	// endregion

	@Override
	protected void disposeImpl() {
		this.setEffect(super.MAN.DEFAULTS.effect);
		this.source.setEffectSlot(super.MAN.DEFAULTS.auxiliaryEffectSlot);
		EXTEfx.alDeleteAuxiliaryEffectSlots(super.id);
		AlAuxiliaryEffectSlot.ALL_INSTANCES.remove(this);
	}

}
