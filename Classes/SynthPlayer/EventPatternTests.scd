EventPattern(()).play;

//:
EventPattern((dur: 0.1)).asStream.play;

//:
EventPattern((dur: 0.1, degree: [-5, 12, 1].pbrown + [0, [-2, 3]].prand)).play;

//:
EventPattern((dur: 0.1, degree: [-5, 12, 1].pbrown +
	(0 ! 5 ++ [-2, 3].dup(2) ++ [[0, 2, 4, 6, 8]]).prand)).play;

//:
a = EventStreamPlayer(EventPattern((dur: 0.1)).asStream);
//:
a.play;
//:
a.stream.event[\degree] = ([-5, 12, 1].pbrown + [0, [-2, 3]].prand).asStream;
//:
a.stop;
//:
a.inspect;

Group().group;

Group.before(Server.default.defaultGroup);

Server.default.queryAllNodes;

