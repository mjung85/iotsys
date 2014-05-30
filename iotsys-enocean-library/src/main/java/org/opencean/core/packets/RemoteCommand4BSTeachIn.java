package org.opencean.core.packets;

import org.opencean.core.utils.Bits;
import org.opencean.core.utils.ByteArray;
import org.opencean.core.utils.ByteBitSet;

public class RemoteCommand4BSTeachIn extends BasicPacket {

    public static final byte PACKET_TYPE = 0x07;

    // data
    private byte rorg = RadioPacket4BS.RADIO_TYPE;
    private byte functionNumber = 0;
    private byte type = 0;
    private short manufactorerId = 0;
    private LearnType learnType;
    private boolean eepResult;
    private boolean learnResult;
    private boolean learnStatus;
    private LearnBit learnBit;

    // optionalData
    private int destinationId;
    private int sourceId;
    private byte dBm;
    private boolean sendWithDelay;

    public RemoteCommand4BSTeachIn(byte functionNumber, short manufactorerId, int destinationId, int sourceId, byte dBm,
            boolean sendWithDelay) {
        this.functionNumber = functionNumber;
        this.manufactorerId = manufactorerId;
        this.destinationId = destinationId;
        this.sourceId = sourceId;
        this.dBm = dBm;
        this.sendWithDelay = sendWithDelay;
    }

    /**
     * 
     * The profile-less unidirectional teach-in procedure functions according to
     * the same principle as the 1BS telegram: if the data Then bit no is EEP
     * DB_0.BIT_3 profile identifier = 0, then and no teach-in manufacturer
     * telegram IDs are sent. transferred. This includes the 'LRN TYPE'
     * DB_0.BIT_7 = 0 data bit.
     * 
     */
    public RemoteCommand4BSTeachIn() {
        learnType = LearnType.BROADCAST;
        learnBit = LearnBit.TEACH_IN;
    }

    @Override
    protected void fillData() {
        super.fillData();
        ByteArray wrapper = new ByteArray();
        wrapper.addByte(rorg);
        ByteBitSet db_0 = new ByteBitSet();
        ByteBitSet db_1 = new ByteBitSet();
        ByteBitSet db_2 = new ByteBitSet();
        ByteBitSet db_3 = new ByteBitSet();

        int i = 7;
        db_0.setBit(i--, Bits.isBitSet(functionNumber, 5));
        db_0.setBit(i--, Bits.isBitSet(functionNumber, 4));
        db_0.setBit(i--, Bits.isBitSet(functionNumber, 3));
        db_0.setBit(i--, Bits.isBitSet(functionNumber, 2));
        db_0.setBit(i--, Bits.isBitSet(functionNumber, 1));
        db_0.setBit(i--, Bits.isBitSet(functionNumber, 0));
        db_0.setBit(i--, Bits.isBitSet(type, 6));
        db_0.setBit(i--, Bits.isBitSet(type, 5));
        wrapper.addByte(db_0.getByte());

        i = 7;
        db_1.setBit(i--, Bits.isBitSet(type, 4));
        db_1.setBit(i--, Bits.isBitSet(type, 3));
        db_1.setBit(i--, Bits.isBitSet(type, 2));
        db_1.setBit(i--, Bits.isBitSet(type, 1));
        db_1.setBit(i--, Bits.isBitSet(type, 0));
        db_1.setBit(i--, Bits.isBitSet(manufactorerId, 10));
        db_1.setBit(i--, Bits.isBitSet(manufactorerId, 9));
        db_1.setBit(i--, Bits.isBitSet(manufactorerId, 8));
        wrapper.addByte(db_1.getByte());

        i = 7;
        db_2.setBit(i--, Bits.isBitSet(manufactorerId, 7));
        db_2.setBit(i--, Bits.isBitSet(manufactorerId, 6));
        db_2.setBit(i--, Bits.isBitSet(manufactorerId, 5));
        db_2.setBit(i--, Bits.isBitSet(manufactorerId, 4));
        db_2.setBit(i--, Bits.isBitSet(manufactorerId, 3));
        db_2.setBit(i--, Bits.isBitSet(manufactorerId, 2));
        db_2.setBit(i--, Bits.isBitSet(manufactorerId, 1));
        db_2.setBit(i--, Bits.isBitSet(manufactorerId, 0));
        wrapper.addByte(db_2.getByte());

        i = 7;
        db_3.setBit(i--, learnType.toBoolean());
        db_3.setBit(i--, eepResult);
        db_3.setBit(i--, learnResult);
        db_3.setBit(i--, learnStatus);
        db_3.setBit(i--, learnBit.toBoolean());
        wrapper.addByte(db_3.getByte());
        payload.setData(wrapper.getArray());
    }

    public enum LearnType {
        BROADCAST, DIRECT;

        public boolean toBoolean() {
            return this.equals(DIRECT);
        }

        public static LearnType fromBoolean(boolean type) {
            if (type) {
                return LearnType.DIRECT;
            } else {
                return LearnType.BROADCAST;
            }
        }
    }

    public enum LearnBit {
        TEACH_IN, DATA;

        public boolean toBoolean() {
            return this.equals(DATA);
        }

        public static LearnBit fromBoolean(boolean type) {
            if (type) {
                return LearnBit.DATA;
            } else {
                return LearnBit.TEACH_IN;
            }
        }
    }
}
