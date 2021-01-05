package util;

public class Bit {
    public final short value;

    public Bit(int x) {
        if(x == 0 || x == 1) {
            this.value = (short)x;
        }
        else {
            this.value = -1;
        }
    }
}
