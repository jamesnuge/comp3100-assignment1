package xyz.jamesnuge.scheduling.lrr;

import fj.Ord;
import fj.data.List;
import xyz.jamesnuge.MessageParser;
import xyz.jamesnuge.scheduling.StateConfigurationFactory;
import xyz.jamesnuge.scheduling.StateMachineFactory;
import xyz.jamesnuge.state.ServerStateItem;

import static fj.Ord.on;
import static fj.Ord.ordDef;
import static fj.data.Either.right;
import static xyz.jamesnuge.MessageParser.Message.OK;
import static xyz.jamesnuge.Util.chain;

public class LrrFactory {

    public static final StateMachineFactory<LRRInternalState> STATE_MACHINE = (cms) -> (message, currentState) -> {
        if (message.contains(MessageParser.InboudMessage.JOBN.name())) { // New job message
            final List<String> params = List.list(message.substring(5).split(" "));
            final Integer nextServerId = getNextServerId(currentState);
            return chain(
                    cms.scheduleJob(Integer.valueOf(params.index(1)), currentState.getServerType(), nextServerId),
                    (_s) -> cms.getMessage(),
                    (s) -> s.equals(OK.name()) ? cms.signalRedy() : chain(cms.getMessage(), (_s) -> cms.signalRedy())
            ).rightMap((_s) -> currentState.copyWithServerTypeAndNumberOfServers(nextServerId, false));
        } else if (message.contains(MessageParser.InboudMessage.RESF.name())) { // Server failure message (Out of scope for assignment - NOOP)
            return right(currentState);
        } else if (message.contains(MessageParser.InboudMessage.JCPL.name())) { // Job completion message
            return cms.signalRedy().rightMap((_s) -> currentState);
        } else if (message.contains(MessageParser.InboudMessage.NONE.name())) { // Finished message
            return right(new LRRInternalState(-1, "", -1, true));
        } else { // Unknown message - Perform NOOP
            return right(currentState);
        }
    };

    public static final StateConfigurationFactory<LRRInternalState> CONFIGURATION = (cms) -> cms.getServerState().rightMap(
            (state) -> {
                String highestCapacityServerType = getHighestCapacityServerType(state);
                return new LRRInternalState(
                        -1,
                        highestCapacityServerType,
                        state.filter((server) -> server.getType().equals(highestCapacityServerType)).length(),
                        false
                );
            }
    );

    private static Integer getNextServerId(LRRInternalState currentState) {
        return (currentState.getLastAssignedServerId() + 1) % currentState.getNumberOfServers();
    }

    public static String getHighestCapacityServerType(final List<ServerStateItem> config) {
        return config.maximum(ordDef(on(ServerStateItem::getCores, Ord.intOrd))).getType();
    }
}
