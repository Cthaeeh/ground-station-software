/**
 * This class is for calculating CRC-16 (X^16 + X^12 + X^5 + 1 CCITT)values for byte arrays.
 * It was copied completely from some ESA software that has to do with the Herschel Space Observatory
 * (some kind of ultimate groundstation software).
 * See: http://herschel.esac.esa.int/hcss-doc-14.0/load/hcss_drm/api/herschel/share/util/CRC16.html
 *
 */
public class CRC16 {
    private int value = 65535;

    /**
     * Updates the CRC-16 with the LSB (Least Significant Byte) of the specified int.
     * @param b the byte to update the checksum with (as an int)
     */
    public void update(int b) {
        this.update((byte)b);
    }

    /**
     * Updates the CRC-16 with the specified array of bytes.
     * @param b the byte array to update the checksum with
     * @param offset - the start offset of the data
     * @param length - the number of bytes to use for the update
     */
    public void update(byte[] b, int offset, int length) {
        for (int i = offset; i < offset + length; ++i) {
            this.update(b[i]);
        }
    }

    /**
     * Updates the CRC-16 with the specified array of bytes.
     * @param b the byte array to update the checksum with
     */
    public void update(byte[] b) {
        for (int i = 0; i < b.length; ++i) {
            this.update(b[i]);
        }
    }

    /**
     * Updates the CRC-16 with the specified byte.
     * @param b the byte to update the checksum with
     */
    public void update(byte b) {
        int n2 = b;
        for (int i = 0; i < 8; ++i) {
            this.value = (n2 & 128 ^ (this.value & 32768) >> 8) != 0 ? (this.value << 1 ^ 4129) & 65535 : this.value << 1 & 65535;
            n2 <<= 1;
        }
    }

    /**
     * Returns the CRC-16 value
     * @return the current checksum value
     */
    public int getValue() {
        return this.value;
    }

    /**
     *  Reset the CRC-16 to the initial value
     *  e.g 0xff
     */
    public void reset() {
        this.value = 65535;
    }
}

