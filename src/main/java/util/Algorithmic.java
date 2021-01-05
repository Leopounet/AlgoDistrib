package util;

import java.util.List;
import java.util.Collections;

public class Algorithmic {
    
    public static int PosDiff(int x, int y) {
        // BinaryNumber bnx = new BinaryNumber(x);
        // BinaryNumber bny = new BinaryNumber(y);
        // int p = BinaryNumber.getDiffBit(bnx, bny);
        // return 2 * p + bnx.getElement(p).value;
        int p = Algorithmic.ffs(x ^ y) - 1;
        return (p<<1)|((x>>p)&1);
    }

    private static int ffs(int x) {
        char table[] =
        {
            0,1,2,2,3,3,3,3,4,4,4,4,4,4,4,4,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,
            6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,
            7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,
            7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,
            8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,
            8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,
            8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,
            8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8
        };
        int a;
        x = x & -x;

        a = x <= 0xffff ? (x <= 0xff ? 0 : 8) : (x <= 0xffffff ?  16 : 24);

        return table[x >> a] + a;
    }

    public static int FirstFree(List<Integer> X) {
        Collections.sort(X);

        int index = 0;
        for(int x: X) {
            if(index != x) {
                break;
            }
            index++;
        }
        return index;
    }
}
