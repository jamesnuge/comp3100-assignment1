package xyz.jamesnuge.scheduling.lrr;

import fj.data.Either;
import fj.data.List;
import xyz.jamesnuge.MessageParser;
import xyz.jamesnuge.Pair;
import xyz.jamesnuge.Util;
import xyz.jamesnuge.messaging.ClientMessagingService;
import xyz.jamesnuge.scheduling.AlgorithmFactory;
import xyz.jamesnuge.scheduling.StateMachine;
import xyz.jamesnuge.state.ServerStateItem;

import static xyz.jamesnuge.MessageParser.Message.OK;
import static xyz.jamesnuge.Util.chain;
import static xyz.jamesnuge.Util.flatMap;
import static xyz.jamesnuge.Util.tryEither;
import static xyz.jamesnuge.scheduling.lrr.LRRInternalState.generateInitialState;
import static xyz.jamesnuge.scheduling.lrr.LRRStateMachine.getHighestCapacityServerType;

public class LrrFactory implements AlgorithmFactory<LRRInternalState> {
    @Override
    public Pair<StateMachine<LRRInternalState, String>, Either<String, LRRInternalState>> createAlgorithm(ClientMessagingService cms) {
        return Pair.of(
                (message, currentState) -> {
                    if (message.contains(MessageParser.InboudMessage.JOBN.name())) {
                        return Util.flatMap(
                                getNextServerId(currentState),
                                (nextServer) -> {
                                    final List<String> params = List.list(message.substring(5).split(" "));
                                    return flatMap(
                                            chain(
                                                    cms.scheduleJob(Integer.valueOf(params.index(1)), "", nextServer),
                                                    (_s) -> cms.getMessage(),
                                                    (s) -> s.equals(OK.name()) ? clientMessagingService.signalRedy() : chain(clientMessagingService.getMessage(), (_s) -> clientMessagingService.signalRedy())
                                            ),
                                            (_s) -> getCurrentState(),
                                            (state) -> tryEither(() -> generateState.f(nextServer.getLeft(), nextServer.getRight()))
                                    );
                                });
                    } else if (trigger.contains(MessageParser.InboudMessage.RESF.name())) {
                        this.currentState = Util.flatMap(
                                currentState,
                                (state) -> tryEither(() -> generateState.f(
                                        state.getLastAssignedServerId(),
                                        state.getNumberOfServers()
                                ))
                        );
                    } else if (trigger.contains(MessageParser.InboudMessage.JCPL.name())) {
                        clientMessagingService.signalRedy();
                    } else if (trigger.contains(MessageParser.InboudMessage.NONE.name())) {
                        this.currentState = tryEither(() -> generateFinalState.f(-1, -1));
                    }


                },
                cms.getServerState()
                        .rightMap((s) -> s.filter((item) -> item.getType().equals(getHighestCapacityServerType(s))).index(0))
                        .rightMap(ServerStateItem::getType)
                        .rightMap(generateInitialState)
        );
    }

    private Integer getNextServerId(LRRInternalState currentState) {
        return (currentState.getLastAssignedServerId() + 1) % currentState.getNumberOfServers();
    }
}
