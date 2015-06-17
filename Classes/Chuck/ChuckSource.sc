/*
Thu, Jun  4 2015, 12:29 EEST
*/
ChuckSource {
	var <source, <chuck;
	var <hasNoDurControl = false, <previousHasNoDurControl = false;
	var hasAudioInputs = false;

	*new { | source, chuck |
		^this.newCopyArgs( (source ?? { ["x", 0] }).asStream, chuck).init
	}

	init {
		if (chuck.notNil and: { chuck.source.notNil}) { 
			previousHasNoDurControl = chuck.source.hasNoDurControl;
		};
		this.makeSource;
	}

	makeSource {}

	play { | output, args |
		/*
			if (portamento.next) {
			     this.setArgs(output, args);
			}{
			// insert stop previous output here
                 this.prPlay(args);
			}
		*/
		this.release; // Release previous synth
		previousHasNoDurControl = nil;
		^this.prPlay(args);
	}

	release { | argDur |
		var output;
		output = chuck.output;
		if (argDur.notNil) {
			if (output isKindOf: Node) {
				if (output.isPlaying) {
					output.release(argDur);
					output = nil;
				}{
					output.onStart (this, { | notification |
						if (notification.listener.isPlaying) {
							notification.listener.release(argDur);
							output = nil;
						}
					})
				}
			}
		}{
			if ((previousHasNoDurControl ? hasNoDurControl) and: { output isKindOf: Node }) {
				output.release(chuck.args[\fadeTime].next);
			};
		}
	}

	prPlay { | args |
		^args use: source;
	}

	asChuckSource { ^this }
}

ChuckSynthSource : ChuckSource {
	makeSource {
		var desc;
		desc = SynthDescLib.at(source.asSymbol);
		if (desc.notNil) {
			hasNoDurControl = desc.controlNames.includes(\dur).not;
		}{
			hasNoDurControl = true;
		}
	}

	prPlay { | args |
		^this.prepareSynth(
			source.play(
				args [\target].next.asTarget,
				args [\out].next,
				args [\fadeTime].next,
				args [\addAction].next,
				args.getPairs
			)
		);
	}

	prepareSynth { | synth |
		^synth.onEnd(this, {
			if (chuck.output === synth) {
				chuck.output = nil;
			}
		})
		/* // Better investigate sending map messages in bundle at creation time
		.onStart(this, { // notify for conrolbusses to map
			chuck.changed(\synthStarted);
		})
		*/
	}
}

ChuckFuncSynthSource : ChuckSynthSource {
	var <defName;

	//	*new { | source, chuck |
	//	^super.new(source, chuck).makeSynthDef;
	// }

	makeSource {
		var def, desc;
		def = source.asSynthDef(
			fadeTime: chuck.args[\fadeTime],
			name: this.makeDefName
		).add; // add to SynthDescLib for use in ChuckSynthSource
		desc = def.desc;
		if (desc.controlNames includes: 'dur') {
			hasNoDurControl = false;
		}{
			hasNoDurControl = true;
		};
		def.doSend(chuck.args[\target].server);
	}
	
	prPlay { | args |
		// play func only the first time.
		// thereafter, create synth from defName
		^this.prepareSynth(
			if (defName.isNil) {
				// [this, thisMethod.name, "Not yet compatible with ().play"].postln;
				source.cplay(
					args [\target].next.asTarget,
					args [\out].next,
					args [\fadeTime].next,
					args [\addAction].next,
					args.getPairs,
					this.makeDefName;
				);
			}{
				defName.play(
					args [\target].next.asTarget,
					args [\out].next,
					args [\fadeTime].next,
					args [\addAction].next,
					args.getPairs
				)
			}
		)
	}

	makeDefName {
		var theName;
		theName = format("<%>", chuck.name);
		// defName = theName;
		{ 0.1.wait; defName = theName }.fork; //.defer(0.1);
		^theName;
	}
}


+ Object {
	asChuckSource { | chuck | ^ChuckSource(this, chuck) }
}

+ Function {
	asChuckSource { | chuck | ^ChuckFuncSynthSource(this, chuck) }
}

+ String {
	asChuckSource { | chuck |  ^ChuckSynthSource(this, chuck) }
	play { | target, outbus, fadeTime, addAction, args |
		^Synth (this, args ++ [out: outbus, fadeTime: fadeTime], target, addAction)
	}
}

