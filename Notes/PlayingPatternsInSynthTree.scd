/*
Stydying PatternPlayer in order to develop the version for playing
paterns in SynthTree, pbind-like. 
Tue, Apr  1 2014, 21:19 EEST
*/

//: TEST 1: Play a PatternInstrument with a notifier action
//: !!!!!!!!!!!!!!!! OK !!!!!!!!!!!!!!!!

p = [freq: Prand((30..90).midicps, inf)] => 0.02;

\test.addNotifier (p.pattern, \value, { | value |
	var synth;
	synth = Synth ('default', value.params);
	SystemClock.sched (value.dur, { synth.release });
	[value.params, value.dur].postln; });

p.start;

//: TEST 2: Play some events in the PatternSynth of a SynthTree
// !!!!!!!!!!!!!!!! OK
\test.asSynthTree =!> \st;
~pstest = PatternSynth(~st);

~synth = ~pstest.addSynth;
//: TEST 3: Let the SynthTree create the PatternSynth and put it in its synth
\test.asSynthTree =!> \st;
~pstest = PatternSynth(~st);

~synth = ~pstest.addSynth;


//:
~synth.release;

//:
~st.addNotifier(a, \value, { | synthEvent |
	var synth;
	synth = ~pstest.addSynth(\default, synthEvent.params);
	SystemClock.sched(synthEvent.dur, { synth.release });
});
//:
~pstest.removeNotifier(~pattern, \value);
//:
~pstest.busIndex;
//:

~synth = Synth(\default, [\out, 16]);
//:
~synth.release;
//:
~synth.set(\out, 4)

//:
SynthDescLib.global.browse;
//:
~synth = ~pstest.addSynth;
//:
~synth.release;
//:
~pattern = [freq: Prand((30..90).midicps, inf)] %> 0.02;
//:
~pattern.start;
//:
~pattern addDependant: { | a, b, c | c.params.postln; };
~pattern.release;