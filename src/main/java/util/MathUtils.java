package util;

public class MathUtils {
    
    public static double log(int x, int base) {
        // no safety check?
        return Math.log(x) / Math.log(base);
    }

}
