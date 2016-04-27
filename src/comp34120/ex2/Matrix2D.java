package comp34120.ex2;

public class Matrix2D {

    protected final double a, b;

    public Matrix2D(double a, double b) {
        this.a = a;
        this.b = b;
    }

    protected Matrix2D plus(Matrix2D m){
        return new Matrix2D(a + m.a, b + m.b);
    }

    protected Matrix2D multiply(double s){
        return new Matrix2D(a * s, b * s);
    }

    protected Matrix2D divide(double s){
        return multiply(1/s);
    }

    protected double multiply(Matrix2D m){
        return a * m.a + b * m.b;
    }

    @Override
    public String toString() {
        return "[" + a + ", " + b + "]";
    }

}
