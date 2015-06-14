/* Sun, Jun 14 2015, 17:54 EEST
Container for a Control Bus, and operators for linking it it to output from Control Synths and to input of Chucks.
*/

ControlBus {
	var <name, numChannels = 1, <server, <bus;
	var <writers, <readers;

	*all { ^Library.at(ControlBus).values }

	*new { | name, numChannels = 1, server |
		name = name.asSymbol;
		^Registry(ControlBus, name, {
			this.newCopyArgs(name, numChannels, server.asTarget.server).init;
		});
	}

	init {
		this.makeBus;
		ServerBootCheck add: { this.makeBus };
		writers = Set();
		readers = Set();
	}
	
	makeBus {  bus = Bus.control(server, numChannels) }

	index { ^bus.index }

	free {
		/* never initiate free from here.
			Instged, remove writers and readers, and let this remove
			itself when it no longer has any readers or writers!
		*/
		this.objectClosed; // this should also remove ... ?
	}

	addReader { }
	removeReader { }
	addWriter { }
	removeWriter { }
}

/*
TODO: Before pursuing the known notification approach, examine if 
Sending the map command in a bundle at the time of the creation of the Synth would be better, in the manner of: Synth:newPaused

*/

BusMapper { // contains parameter and chuck
	var <controlBus, <chuck, <parameter;

	addToChuck { | param, chuck |
		parameter = param;
		chuck.args[parameter] = this;
		if (chuck.isPlaying) { chuck.output.map(parameter, controlBus.index) };
		this.addNotifier(chuck, \synthStarted, {
			 chuck.output.map(parameter, controlBus.index) 
		});
	}

	removeFromChuck {
		chuck.args // []
	}
}


