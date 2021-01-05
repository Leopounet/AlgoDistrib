package util;

public class BinaryNumber {
    
    private final Bit binary_array[]; 
    public final int length;

    public BinaryNumber(int x) {
        this.length = this.getBinaryLength(x);
        this.binary_array = new Bit[this.length];
        this.fillBinaryArray(x, this.binary_array, this.length);
    }

    public int getBinaryLength(int x) {
        if(x == 0) {
            return 1;
        }

        double res = Math.ceil(MathUtils.log(x, 2));
        if((int)res == res) {
            return (int)(res) + 1;
        }
        return (int)res;
    }

    public Bit getElement(int pos) {
        if(pos >= 0 && pos < this.length) {
            return this.binary_array[pos];
        }
        return new Bit(0);
    }

    private void fillBinaryArray(int x, Bit arr[], int length) {
        int index = length - 1;

        while(index >= 0) {
            Bit bit = new Bit(x % 2);
            arr[index] = bit;
            x = x >> 1;
            index--;
        }
    }

    public static int getDiffBit(BinaryNumber x, BinaryNumber y) {
        if(x == y) {
            return -1;
        }
        
        int index;
        for(index = 0; index < Math.min(x.length, y.length); index++) {
            if(x.getElement(index) != y.getElement(index)) {
                return index;
            }
        }
        return index;
    }
}
