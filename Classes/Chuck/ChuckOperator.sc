+ Object {

	=> { | symbol, adverb |
		^Registry.doIfFound (Chuck, symbol, { | chuck |
			chuck.setProcessParameter (adverb, this)
		})
	}
}

+ Function {
	=> { | symbol |
		^Registry(Chuck, symbol, { Chuck(symbol) }).play (this);
	}
}

