
BusLink {
	var index, <numChannels = 1, <server, <writers, <readers;
	var <bus;
	classvar nullBus;

	*initClass {
		ServerBootCheck addStartup: { this.init }
		 
	}

	*init {
		Library.at(ArBusLink) do: _.makeBus;
		Library.at(KrBusLink) do: _.makeBus;
	}

	init {
		bus = Bus.perform(this.rate, bus.server, numChannels);
	}

	*new { | index numChannels = 1 server |
		^this.newCopyArgs(index, numChannels, server.asTarget.server, Set(), Set())
		.makeBus
	}

	nullBus {
		nullBus ?? { nullBus = this.new(0, 1, Server.default) };
		^nullBus;
	}
	makeBus {
		// re-allocate bus on server boot, to correctly set allocation of server
		if (index.isNil) {
			bus = Bus.perform(this.rate, server.asTarget.server, numChannels)
		}{
			bus = Bus.newCopyArgs(this.rate, index, numChannels, server.asTarget.server)
		}
	}

	free { // may do more stuff later
		bus.free;
	}

	addReader { | chuck |

	}

	addWriter { | chuck |

	}

	asControlInput { ^bus.index }

	printOn { arg stream;
		stream << this.class << "(" << bus.index << ")";
	}
	storeOn { arg stream;
		stream << this.class << "(" << bus.index << ")";
	}

}

ArBusLink : BusLink {
	rate { ^\audio }
}

KrBusLink : BusLink {
	rate { ^\control }
}

+ Symbol {
	abus {
		^Registry(ArBusLink, this, { ArBusLink() });
	}

	kbus {
		^Registry(KrBusLink, this, { KrBusLink() });
	}

	@> { | chuckName, param = \in |
		
	}

	<@ {
		
	}

	%> {
		
	}

	<% {
		
	}
}

+ String {
	alink {
		var components;
		components = this.split($@).collect(_.split($>)) collect: { | a |
			a collect: _.split($.);
		};
		/*
		components = components collect: { | c |
			
			[c[0]]
			
		};
		*/
		^components;
	}

	chuckIO { | specs |
		
	}

	specs2Bus {
		
	}
	

	klink {
		
	}

	al { ^this.alink }
	kl { ^this.klink }
}