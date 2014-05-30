package org.opencean.core.common;

public class EEPId {

    /** Deprecated EEP_ID but still sent by captor (EltakoLumSensor) */
    public static final EEPId EEP_07_06_01 = new EEPId("07:06:01");
    public static final EEPId EEP_A5_02_05 = new EEPId("A5:02:05");
    public static final EEPId EEP_A5_07_03 = new EEPId("A5:07:03");
    public static final EEPId EEP_A5_08_02 = new EEPId("A5:08:02");
    public static final EEPId EEP_A5_09_04_enoluz = new EEPId("A5:09:04", "enoluz");
    public static final EEPId EEP_A5_09_05 = new EEPId("A5:09:05");
    /**
     * Electronic switches and Type dimmers 0x08 (description: with Energy
     * Measurement see table) and Local Control
     */
    public static final EEPId EEP_D2_01_08 = new EEPId("D2:01:08");
    public static final EEPId EEP_D5_00_01 = new EEPId("D5:00:01");
    public static final EEPId EEP_F6_02_01 = new EEPId("F6:02:01");
    public static final EEPId EEP_F6_02_02 = new EEPId("F6:02:02");

    private String id;

    /**
     * Some devices do not behave exactly as specified in the EEP. They get a
     * variant (mostly manufacturer name) to separate them from the real EEP
     * implementations.
     */
    private String variant;

    public EEPId(String id) {
        this.id = id;
    }

    public EEPId(String id, String variant) {
        this.id = id;
        this.variant = variant;
    }

    public String getId() {
        if (variant != null) {
            return id + "-" + variant;
        }
        return id;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((variant == null) ? 0 : variant.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        EEPId other = (EEPId) obj;
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        if (variant == null) {
            if (other.variant != null) {
                return false;
            }
        } else if (!variant.equals(other.variant)) {
            return false;
        }
        return true;
    }

}
