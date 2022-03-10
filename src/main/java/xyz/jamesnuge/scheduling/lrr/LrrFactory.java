package xyz.jamesnuge.scheduling.lrr;

import fj.data.Either;
import fj.data.List;
import xyz.jamesnuge.MessageParser;
import xyz.jamesnuge.Pair;
import xyz.jamesnuge.messaging.ClientMessagingService;
import xyz.jamesnuge.scheduling.AlgorithmFactory;
import xyz.jamesnuge.scheduling.StateMachine;

import static fj.data.Either.right;
import static xyz.jamesnuge.MessageParser.Message.OK;
import static xyz.jamesnuge.Util.chain;
import static xyz.jamesnuge.scheduling.lrr.LRRStateMachine.getHighestCapacityServerType;

public class LrrFactory implements AlgorithmFactory<LRRInternalState> {
    @Override
    public Pair<StateMachine<LRRInternalState, String>, Either<String, LRRInternalState>> createAlgorithm(ClientMessagingService cms) {
        return Pair.of(
                (message, currentState) -> {
                    if (message.contains(MessageParser.InboudMessage.JOBN.name())) {
                        final List<String> params = List.list(message.substring(5).split(" "));
                        System.out.println(currentState);
                        final Integer nextServerId = getNextServerId(currentState);
                        return chain(
                                cms.scheduleJob(Integer.valueOf(params.index(1)), currentState.getServerType(), nextServerId),
                                (_s) -> cms.getMessage(),
                                (s) -> s.equals(OK.name()) ? cms.signalRedy() : chain(cms.getMessage(), (_s) -> cms.signalRedy())
                        ).rightMap((_s) -> currentState.copyWithServerTypeAndNumberOfServers(nextServerId, false));
                    } else if (message.contains(MessageParser.InboudMessage.RESF.name())) {
                        return right(currentState);
                    } else if (message.contains(MessageParser.InboudMessage.JCPL.name())) {
                        return cms.signalRedy().rightMap((_s) -> currentState);
                    } else if (message.contains(MessageParser.InboudMessage.NONE.name())) {
                        return right(new LRRInternalState(-1, "", -1, true));
                    } else {
                        return right(currentState);
                    }
                },
                cms.getServerState()
                        .rightMap((s) -> s.filter((item) -> item.getType().equals(getHighestCapacityServerType(s))))
                        .rightMap((s) -> new LRRInternalState(-1, s.index(0).getType(), s.length(), false))
        );
    }

    private Integer getNextServerId(LRRInternalState currentState) {
        return (currentState.getLastAssignedServerId() + 1) % currentState.getNumberOfServers();
    }
}
