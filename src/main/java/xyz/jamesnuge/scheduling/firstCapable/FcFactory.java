package xyz.jamesnuge.scheduling.firstCapable;

import xyz.jamesnuge.MessageParser;
import xyz.jamesnuge.messaging.ConfigRequest;
import xyz.jamesnuge.messaging.NewJobRequest;
import xyz.jamesnuge.scheduling.StateConfigurationFactory;
import xyz.jamesnuge.scheduling.StateMachineFactory;
import xyz.jamesnuge.state.ServerStateItem;

import static fj.data.Either.right;
import static xyz.jamesnuge.MessageParser.Message.OK;
import static xyz.jamesnuge.Util.chain;
import static xyz.jamesnuge.Util.flatMap;

public class FcFactory {

    public static final StateMachineFactory<FcInternalState> STATE_MACHINE = (cms) -> (message, currentState) -> {
        if (message.contains(MessageParser.InboudMessage.JOBN.name())) { // New job message
            final NewJobRequest jobRequest = NewJobRequest.parseFromJOBNMessage(message);
            final ConfigRequest capableRequest = ConfigRequest.createCapableTypeConfigRequest(jobRequest.core, jobRequest.memory, jobRequest.disk);
            return flatMap(cms.getServerState(capableRequest), (list) -> {
                final ServerStateItem first = list.head();
                return chain(
                        cms.scheduleJob(jobRequest.jobId, first.getType(), first.getId()),
                        (_s) -> cms.getMessage(),
                        (s) -> s.equals(OK.name()) ? cms.signalRedy() : chain(cms.getMessage(), (_s) -> cms.signalRedy())
                );
            }).rightMap((_s) -> currentState);
        } else if (message.contains(MessageParser.InboudMessage.NONE.name())) { // Finished message
            return right(new FcInternalState(true));
        } else if (message.contains(MessageParser.InboudMessage.JCPL.name())) { // Job completion message
            return cms.signalRedy().rightMap((_s) -> currentState);
        } else { // Unknown message - Perform NOOP
            return right(currentState);
        }
    };

    public static final StateConfigurationFactory<FcInternalState> CONFIGURATION = (cms) -> right(new FcInternalState(false));
}
