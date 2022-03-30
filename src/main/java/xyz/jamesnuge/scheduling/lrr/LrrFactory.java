package xyz.jamesnuge.scheduling.lrr;

import xyz.jamesnuge.MessageParser;
import xyz.jamesnuge.SystemInformationUtil;
import xyz.jamesnuge.Util;
import xyz.jamesnuge.messaging.ConfigRequest;
import xyz.jamesnuge.messaging.NewJobRequest;
import xyz.jamesnuge.scheduling.StateConfigurationFactory;
import xyz.jamesnuge.scheduling.StateMachineFactory;

import static fj.data.Either.right;
import static xyz.jamesnuge.MessageParser.Message.OK;
import static xyz.jamesnuge.Util.chain;

public class LrrFactory {

    public static final StateMachineFactory<LRRInternalState> STATE_MACHINE = (cms) -> (message, currentState) -> {
        if (message.contains(MessageParser.InboudMessage.JOBN.name())) { // New job message
            final NewJobRequest jobRequest = NewJobRequest.parseFromJOBNMessage(message);
            final Integer nextServerId = getNextServerId(currentState);
            return chain(
                    cms.scheduleJob(jobRequest.jobId, currentState.getServerType(), nextServerId),
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

    // Generates the initial state by reading in the ds-system xml, then querying for the servers of the largest type
    public static final StateConfigurationFactory<LRRInternalState> CONFIGURATION = (cms) -> {
        return Util.flatMap(
            SystemInformationUtil.loadSystemConfig("."),
            (systemInfo) -> {
                String largestServerType = SystemInformationUtil.getLargestServerType(systemInfo);
                return cms.getServerState(ConfigRequest.createServerTypeConfigRequest(largestServerType)).rightMap((serverState) -> {
                    return new LRRInternalState(-1, largestServerType, serverState.length(), false);
                });
            }
        );
    };

    private static Integer getNextServerId(LRRInternalState currentState) {
        return (currentState.getLastAssignedServerId() + 1) % currentState.getNumberOfServers();
    }

}
