package xyz.jamesnuge;

import java.io.File;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import xyz.jamesnuge.config.SystemConfig;

public class SchedulingService {

    private static final String DS_SYSTEM_FILENAME = "/ds-system.xml";

    private Boolean isInitialized = false;
    private final String fileBasePath;

    public SchedulingService(final String fileBasePath) {
        this.fileBasePath = fileBasePath;
    }

    public void init() {
        final String fileName = System.getProperty("user.dir") + DS_SYSTEM_FILENAME;

        try {
            final File xmlFile = new File(fileName);
            JAXBContext jaxbContext = JAXBContext.newInstance(SystemConfig.class);

            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            SystemConfig sys = (SystemConfig) jaxbUnmarshaller.unmarshal(xmlFile);
            System.out.println(sys);
            this.isInitialized = true;
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

}
