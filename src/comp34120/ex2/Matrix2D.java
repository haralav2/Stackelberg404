package comp34120.ex2;

public class Matrix2D {

    protected final float a, b;

    public Matrix2D(float a, float b) {
        this.a = a;
        this.b = b;
    }

    protected Matrix2D plus(Matrix2D m){
        return new Matrix2D(a + m.a, b + m.b);
    }

    protected Matrix2D multiply(float s){
        return new Matrix2D(a * s, b * s);
    }

    protected Matrix2D divide(float s)
    {
        return multiply(1/s);
    }

    protected Matrix2x2 multiply(Matrix2D m){
        return new Matrix2x2(this.a * m.a,
                             this.a * m.b,
                             this.b * m.a,
                             this.b * m.b) ;
    }

    protected float multiplyTwo(Matrix2D m){
        return this.a * m.a + this.b * m.b;
    }

    protected Matrix2D multiply(Matrix2x2 m) {
        return new Matrix2D(this.a * m.a + this.b * m.b,
                             this.a * m.c + this.b * m.d);
    }

    public String toString() {
        return "[" + a + ", " + b + "]";
    }

}
