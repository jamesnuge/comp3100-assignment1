package xyz.jamesnuge;

import fj.data.Either;
import java.io.File;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import xyz.jamesnuge.config.SystemConfig;

public class SystemInformationUtil {

    private static final String DS_SYSTEM_FILENAME = "/ds-system.xml";

    public static Either<String, SystemConfig> loadSystemConfig(String fileBasePath) {
        try {
            final File xmlFile = new File(fileBasePath + DS_SYSTEM_FILENAME);
            JAXBContext jaxbContext = JAXBContext.newInstance(SystemConfig.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            SystemConfig sys = (SystemConfig) jaxbUnmarshaller.unmarshal(xmlFile);
            return Either.right(sys);
        } catch (final Exception e) {
            return Either.left(e.getMessage());
        }
    }

}
