/*
Tue, Jun 16 2015, 03:08 EEST

Inspired by Steno of Julian Rohrhuber. 
https://github.com/telephon/Steno

A simpler version that only creates chucks and puts them in Groups and links them with Busses. 

*/

MiniSteno {
	var <tree;
	classvar <numLinkChucks; // for naming system-created link Chucks

	*fromString { | string |
		numLinkChucks = 0;
		string = string.inject(" ", { | a, x |
			a ++ (if ("[]()".includes(x)) { x } { format("'%', ", x) })
		});
		string = string.replace("(", "Ser( ")
		.replace("[", "Par( ")
		.replace(")", "), ")
		.replace("]", "), ")
		.replace(", )", ")");
		^format("Par( % )", string).postln.interpret;
	}

	*new { | ... specs |
		var linkChuck;
		^this.newCopyArgs(specs collect: { | s |
			if (s isKindOf: Symbol) { Chuck(s) } { s }
		});
 	}

	push { // use this instance as the global tree of linked Chucks
		var nullGroup;
		nullGroup = GroupLink.nullGroup;
		Library.put(MiniSteno, \current, this);
		this.initRootTree;
	}

	initRootTree {
		Chuck.initInactive;
		tree do: _.insertSerInPar;
		this.setBussesAndGroups(ArBusLink.nullBus, ArBusLink.nullBus, GroupLink.default, 0);
		numLinkChucks do: { | i | Chuck(i.asSymbol).playIfNotPlaying };
		Chuck.inactive do: _.setTarget(nullGroup);
		"================================================================".postln;
		MiniSteno.current.pp;
		"================================================================".postln;	
	}

	*current { ^Library.at(MiniSteno, \current) }

	pp { | levels = "" | // prettyprint
		postf ("% (        // % % %\n", levels, levels, this.class, levels);
		tree do: { | x |
			if (x isKindOf: Chuck) {
				postf("*%* % % % % %\n", x.target.level,
					levels, x, x.target, x.inBus, x.outBus);
			}{
				x.pp(levels ++ "-");
			};
		};
		postf("% )\n", levels);
	}

	// ================================================================
	// TODO: Changing the structure of the tree after it was created:
	addBefore { | chuck, asSibling = false |

	}

	addAfter { | chuck, asSibling = false |

	}

	addSibling { | newChuck oldChuck |

	}

	remove { | chuck, sendToNullGroup = false |
		/* rearrange tree by removing chuck from it, if it exists. */
		var container;
		container = tree findContainerOf: chuck;
		
	}

	doIfFound { | element, foundFunc, missingFunc |
		var container;
		container = this.findContainerOf(element);
		if (container.isNil) {
			missingFunc.(element, this);
		}{
			foundFunc.(container, element, this);
		}
	}
	
	findContainerOf { | element |
		var found;
		this.traverseDoing({ | ms | if (ms.tree includes: element) { found = ms } });
		^found;
	}

	traverseDoing { | func |
		func.(this);
		tree do: { | x | if (x isKindOf: MiniSteno) { x.traverseDoing(func) } }
	}
}

Par : MiniSteno {
	insertSerInPar {
		tree = tree collect: { | el |
			Ser(this.makeLinkChuck, el, this.makeLinkChuck)
		};
	}

	makeLinkChuck {
		var linkChuck;
		linkChuck = Chuck(numLinkChucks.asSymbol);
		linkChuck.source = { Inp.ar };
		linkChuck.permanent;
		numLinkChucks = numLinkChucks + 1;
		^linkChuck;
	}
	
	setBussesAndGroups { | inBus, outBus, group |
		tree do: { | branch, i |
			branch.setBussesAndGroups(inBus, outBus, group);
		};
	}
}

Ser : MiniSteno {
	insertSerInPar { tree do: _.insertSerInPar }

	setBussesAndGroups { | inBus, outBus, group |
		var busArray;
		this.flatten; // remove Ser in Ser nestings because they mess up Group+Bus link order
		busArray = [inBus];

		tree.size - 1 do: {
			busArray = busArray add: ArBusLink();
		};
		busArray = busArray add: outBus;		
		tree do: { | branch, i |
			branch.setBussesAndGroups(busArray[i], busArray[i + 1], group);
			group = group.getReaderGroup;
		};
	}

	flatten {
		var newTree;
		tree do: { | el |
			if (el isKindOf: Ser) {
				el.flatten;
				el.tree do: { | el2 | newTree = newTree add: el2 };
			}{
				newTree = newTree add: el;
			}
		};
		tree = newTree;
	}
	
}

+ String {
	miniSteno { ^MiniSteno.fromString(this) }
	arlink { ^this.miniSteno.push }
}