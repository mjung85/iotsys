package an.config;

import static an.xml.XMLParserWrapper.parse;
import static an.xml.XMLParserWrapper.verifySchemaFile;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.w3c.dom.Element;

/**
 * The Configuration provide mechanism to read configurations from XML file to Java object. It also can save 
 * configuration object to XML configure file.
 */
public class Configuration {
    public static final String CONFIGURATION_SCHEMA = System.getProperty("an.config.Configuration.Schemas", "configuration.xsd");

    /**
     * The root element of the configuration.
     */
    private ConfigElement rootElement = null;

    /**
     * Construct a Configuration object from an InputStream. The InputStream object should supply the
     * configuration XML document.
     * @param in
     * @throws ConfigurationException
     */
    public Configuration(InputStream in) throws ConfigurationException {
        System.out.print("Loading configurations ... ");
        initialize(in);
        try {
            in.close();
        } catch (IOException e) {
            throw new ConfigurationException(e.getMessage(), e);
        }
        System.out.println("done.");
    }

    /**
     * Construct a Configuration object from a configuration file, which file should include a XML document.
     * @param configFile
     * @throws FileNotFoundException
     * @throws ConfigurationException
     */
    public Configuration(String configFile) throws FileNotFoundException, ConfigurationException {
        this(new FileInputStream(configFile));
    }

    /**
     * Create an empty configuration, then call getRootElement() to get an empty root element,
     * and then add child elements and attributes to the element.
     */
    public Configuration() {
        // TODO: provide an empty root configure element.
    }

    protected void initialize(InputStream in) throws ConfigurationException {
        try {
            String[] verifiedSchemas = CONFIGURATION_SCHEMA.trim().split("\\s+");
            for (int x = 0; x < verifiedSchemas.length; x ++) {
                verifiedSchemas[x] = verifySchemaFile(verifiedSchemas[x]);
                System.out.println(verifiedSchemas[x]);
            }

            Element root = parse(in, verifiedSchemas);
            System.out.println("initialize");
            System.out.println(in.toString());
            // initialize the root XML element from DOM element
            rootElement = new ConfigElement(root);
        }
        catch (Exception e) {
        	System.out.println(e.getMessage());
            throw new ConfigurationException("Error occurs during parse configuration file.", e);
        }
    }

    /**
     * Get the root configuration element of configuration file.
     * @return
     */
    public ConfigElement getConfigurationElement() {
        return rootElement;
    }

    /**
     * Save configuration to output stream
     */
    public void save(OutputStream out) {
        // TODO: save configuration XML to the specific output stream
    }

    /**
     * Return an XML represent of Configuration.
     */
    public String toString() {
        // TODO: implement it.
        return super.toString();
    }
}