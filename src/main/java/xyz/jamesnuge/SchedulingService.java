package xyz.jamesnuge;

import java.io.File;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;

public class SchedulingService {

    private static final String DS_SYSTEM_FILENAME = "/ds-system.xml";

    private Boolean isInitialized = false;
    private final String fileBasePath;

    public SchedulingService(final String fileBasePath) {
        this.fileBasePath = fileBasePath;
    }

    public void init() {
        final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        final String fileName = System.getProperty("user.dir") + DS_SYSTEM_FILENAME;

        try {
            final File xmlFile = new File(fileName);
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            final DocumentBuilder db = dbf.newDocumentBuilder();
            final Document doc = db.parse(new File(fileName));

            doc.getDocumentElement().normalize();

            System.out.println("Root Element :" + doc.getDocumentElement().getNodeName());
            System.out.println("------");
            this.isInitialized = true;
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

}
