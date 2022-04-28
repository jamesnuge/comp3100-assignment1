package xyz.jamesnuge;

import fj.data.Either;
import fj.data.Option;
import org.apache.commons.cli.*;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import xyz.jamesnuge.messaging.ClientMessagingService;
import xyz.jamesnuge.scheduling.SchedulingService;
import xyz.jamesnuge.scheduling.StateConfigurationFactory;
import xyz.jamesnuge.scheduling.StateMachineFactory;
import xyz.jamesnuge.scheduling.firstCapable.FcFactory;
import xyz.jamesnuge.scheduling.lrr.LrrFactory;

import java.util.Map;

import static xyz.jamesnuge.SocketClientSystemFactory.generateClientSystem;
import static xyz.jamesnuge.Util.mapOf;

public class Main {
    public static Logger LOGGER = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        final Args parsedArgs = parseArgs(args);
        String algorithm = parsedArgs.algorithm;
        setLogLevel(parsedArgs.logLevel);
        final Option<ClientMessagingService> maybeMessagingSystem = generateClientSystem(
                "127.0.0.1",
                50000
        );
        if (maybeMessagingSystem.isSome()) {
            Map<String, Pair<StateMachineFactory<?>, StateConfigurationFactory<?>>> configMap = mapOf("LRR", Pair.of(LrrFactory.STATE_MACHINE, LrrFactory.CONFIGURATION));
            configMap.put("FC", Pair.of(FcFactory.STATE_MACHINE, FcFactory.CONFIGURATION));
            final SchedulingService service = new SchedulingService(
                    maybeMessagingSystem.some(),
                    configMap
            );
            final Either<String, String> result = service.scheduleJobsUsingAlgorithm(algorithm)
                    .rightMap((s) -> {
                        LOGGER.info(s);
                        return s;
                    });
            if (result.isLeft()) {
                LOGGER.error("Could not schedule jobs using algorithm " + algorithm + ": " + result.left().value());
            }
        } else {
            LOGGER.error("Unable to connect to server");
        }
    }

    private static void setLogLevel(String logLevel) {
        Level level = Level.getLevel(logLevel);
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        Configuration config = ctx.getConfiguration();
        LoggerConfig loggerConfig = config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME);
        loggerConfig.setLevel(level);
        ctx.updateLoggers();
    }

    private static Args parseArgs(String[] args) {
        final Options options = new Options();
        final org.apache.commons.cli.Option algorithmOption =  new org.apache.commons.cli.Option("a", "algorithm", true, "The acronym of the algorithm to by run by the client");
        algorithmOption.setRequired(true);

        final org.apache.commons.cli.Option logLevelOption =  new org.apache.commons.cli.Option("l", "log-level", true, "The logging level for the application. Defaults to WARN");

        options.addOption(algorithmOption);
        options.addOption(logLevelOption);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;//not a good practice, it serves it purpose

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            LOGGER.error("Unable to parse arguments: ", e);
            formatter.printHelp("COMP3100 Scheduling Client", options);
            System.exit(1);
        }

        return new Args(
                cmd.getOptionValue("algorithm"),
                Option.fromNull(cmd.getOptionValue("log-level")).orSome("WARN")
        );
    }

    static class Args {
        public final String algorithm;
        public final String logLevel;

        private Args(String algorithm, String logLevel) {
            this.algorithm = algorithm;
            this.logLevel = logLevel;
        }
    }
}
