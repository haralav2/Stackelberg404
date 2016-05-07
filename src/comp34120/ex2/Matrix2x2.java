package comp34120.ex2;

public class Matrix2x2 {
    protected final float a, b, c, d;

    public Matrix2x2(float a, float b,float c,float d) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }

    protected Matrix2x2 inverse(){
        Matrix2x2 negativeMatrix = new Matrix2x2(this.d, - this.b, -this.c, this.a);
        float denominator = this.a * this.d - this.b*this.c;
        return negativeMatrix.multiply(1 / denominator);
    }

    protected Matrix2x2 multiply(float f){
        return new Matrix2x2(this.a * f, this.b * f, this.c * f, this.d * f);

    }

    protected Matrix2x2 multiply(Matrix2x2 m){
        return new Matrix2x2(this.a * m.a + this.b * m.c, this.a * m.b + this.b * m.d,
                             this.c * m.a + this.d * m.c, this.c * m.b + this.d * m.d);
    }

    protected Matrix2x2 plus(Matrix2x2 m){
        return new Matrix2x2(this.a + m.a, this.b + m.b, this.c + m.c, this.d + m.d);

    }

    protected Matrix2x2 minus(Matrix2x2 m){
        return new Matrix2x2(this.a - m.a, this.b - m.b, this.c - m.c, this.d - m.d);

    }

    public String toString() {
        return "[" + a + ", " + b + "]" + "\n" + "[" + c + ", " + d + "]" ;
    }
}
