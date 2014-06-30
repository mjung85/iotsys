package an.xacml;

/**
 * Represent the product version, for example: AN System 1.0.0
 * 
 * Should provide method to read the version from
 * JAR's manifest
 */
public class Version {
    private int major;
    private int release;
    private int maintenance;

    /**
     * This is a version for element, is intend for make the implementation 
     * compatible between XACML 2.0 and future 3.0
     */
    private int xacmlVersion;
    private String product;

    public Version() {
        // TODO: read version info from JAR's manifest
        // Temporarily give a default version.
        product = "*AN System*";
        major = 0;
        release = 1;
        maintenance = 0;
        xacmlVersion = 2;
    }

    public Version(String product, int major, int release, int maintenace) {
        this.product = product;
        this.major = major;
        this.release = release;
        this.maintenance = maintenace;
    }

    public String getProduct() {
        return product;
    }

    public int getMajor() {
        return major;
    }

    public int getRelease() {
        return release;
    }

    public int getMaintenance() {
        return maintenance;
    }

    public String getVersion() {
        return major + "." + release + "." + maintenance + ", XACML v" + xacmlVersion;
    }

    public int getXACMLVersion() {
        return xacmlVersion;
    }

    public String toString() {
        return getVersion();
    }
}