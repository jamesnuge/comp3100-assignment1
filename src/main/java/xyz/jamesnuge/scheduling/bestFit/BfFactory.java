package xyz.jamesnuge.scheduling.bestFit;

import fj.F;
import fj.Ord;
import fj.data.List;
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

public class BfFactory {

    private static final F<List<ServerStateItem>, List<ServerStateItem>> orderServerStateByNumberOfCoresAscending =
            (l) -> l.sort(Ord.intOrd.contramap(ServerStateItem::getCores));

    public static final StateMachineFactory<BfInternalState> STATE_MACHINE = (cms) -> (message, currentState) -> {
        if (message.contains(MessageParser.InboudMessage.JOBN.name())) { // New job message
            // Get the details of the job request
            final NewJobRequest jobRequest = NewJobRequest.parseFromJOBNMessage(message);
            // Build the request to get all machines capable of running the job
            final ConfigRequest capableRequest = ConfigRequest.createCapableTypeConfigRequestForJob(jobRequest);
            return flatMap(
                    // Get all machines capable of running our job, then order by number of cores, ascending
                    cms.getServerState(capableRequest).rightMap(orderServerStateByNumberOfCoresAscending),
                    (list) -> {
                        // Pick the server with no jobs and the least number of cores
                        // If there is no servers not running jobs, pick the server with the least number of cores
                        final ServerStateItem serverToHandleJob = list
                                .filter(ServerStateItem::hasNoJobs)
                                .headOption()
                                .orSome(list.head());
                        return chain(
                                cms.scheduleJob(jobRequest.jobId, serverToHandleJob.getType(), serverToHandleJob.getId()),
                                (_s) -> cms.getMessage(),
                                (s) -> s.equals(OK.name()) ? cms.signalRedy() : chain(cms.getMessage(), (_s) -> cms.signalRedy())
                        );
                    }).rightMap((_s) -> currentState);
        } else if (message.contains(MessageParser.InboudMessage.NONE.name())) { // Finished message
            return right(new BfInternalState(true));
        } else if (message.contains(MessageParser.InboudMessage.JCPL.name())) { // Job completion message
            return cms.signalRedy().rightMap((_s) -> currentState);
        } else { // Unknown message - Perform NOOP
            return right(currentState);
        }
    };

    public static final StateConfigurationFactory<BfInternalState> CONFIGURATION = (cms) -> right(new BfInternalState(false));

}
