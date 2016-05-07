package comp34120.ex2;

/**
 * Created by mbax2vh2 on 07/05/16.
 */
public class Matrix2x2 {
    protected final float a, b, c, d;

    public Matrix2x2(float a, float b,float c,float d) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }
    @Override
    public String toString() {
        return "[" + a + ", " + b + "]" + "\n" + "[" + c + ", " + d + "]" ;
    }
}
