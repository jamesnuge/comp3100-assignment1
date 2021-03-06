package xyz.jamesnuge.fixtures;

import xyz.jamesnuge.state.ServerStateItem;

import static xyz.jamesnuge.state.ServerStateItem.ServerStatus.INACTIVE;

public class ServerStateItemFixtures {
    private ServerStateItemFixtures() {}

    public static final ServerStateItem SERVER_STATE_ITEM = generateServerStateItem(1);

    public static ServerStateItem generateServerStateItem(Integer id) {
        return generateServerStateItem("type" + id, id);
    }

    public static ServerStateItem generateServerStateItem(String type, Integer id) {
        return new ServerStateItem(
                type,
                id,
                INACTIVE,
                -1L,
                id,
                id.longValue(),
                id.longValue(),
                id,
                id
        );
    }

    public static ServerStateItem generateServerStateItemWithNoJobs(String type, Integer id) {
        return generateServerStateItem(type, id, 0, 0);
    }

    public static ServerStateItem generateServerStateItem(String type, Integer id, Integer activeJobs, Integer waitingJobs) {
        return new ServerStateItem(
                type,
                id,
                INACTIVE,
                -1L,
                id,
                id.longValue(),
                id.longValue(),
                activeJobs,
                waitingJobs
        );
    }

    public static String createServerStateString(ServerStateItem item) {
        return item.getType() + " " +
                item.getId() + " " +
                item.getStats() + " " +
                item.getCurrentStartTime() + " " +
                item.getCores() + " " +
                item.getMemory() + " " +
                item.getDisk() + " " +
                item.getWaitingJobs() + " " +
                item.getRunningJobs();
    }
}
