package org.opencean.core.utils;

public class ByteArray {

    private byte[] array;

    public ByteArray() {
        clear();
    }

    public ByteArray(byte[] array) {
        this.array = array;
    }

    public ByteArray clear() {
        array = new byte[0];
        return this;
    }

    public ByteArray setInt(int i, int pos) {
        array[pos] = (byte) ((i >> 24) & 0xFF);
        array[pos + 1] = (byte) ((i >> 16) & 0xFF);
        array[pos + 2] = (byte) ((i >> 8) & 0xFF);
        array[pos + 3] = (byte) ((i) & 0xFF);
        return this;
    }

    public ByteArray addShort(short i) {
        int oldLength = array.length;
        expandArray(2);
        array[oldLength] = (byte) ((i >> 8) & 0xFF);
        array[oldLength + 1] = (byte) ((i) & 0xFF);
        return this;
    }

    public ByteArray addInt(int i) {
        int oldLength = array.length;
        expandArray(4);
        array[oldLength] = (byte) ((i >> 24) & 0xFF);
        array[oldLength + 1] = (byte) ((i >> 16) & 0xFF);
        array[oldLength + 2] = (byte) ((i >> 8) & 0xFF);
        array[oldLength + 3] = (byte) ((i) & 0xFF);
        return this;
    }

    public ByteArray setByte(byte b, int pos) {
        array[pos] = b;
        return this;
    }

    public ByteArray addBytes(byte[] bytes) {
        int oldLength = array.length;
        expandArray(bytes.length);
        System.arraycopy(bytes, 0, array, oldLength, bytes.length);
        return this;
    }

    public ByteArray addByte(byte b) {
        int oldLength = array.length;
        expandArray(1);
        array[oldLength] = b;
        return this;
    }

    public byte[] getArray() {
        return array;
    }

    private void expandArray(int additionalBytes) {
        byte[] newArray = new byte[array.length + additionalBytes];
        System.arraycopy(array, 0, newArray, 0, array.length);
        array = newArray;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < array.length; i++) {
            if (i != 0) {
                sb.append(", ");
            }
            sb.append(String.format("%02X", array[i]));
        }
        sb.append("]");
        return sb.toString();
    }
}
