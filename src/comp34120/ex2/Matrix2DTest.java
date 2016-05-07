package comp34120.ex2;

/**
 * Created by mbax2vh2 on 07/05/16.
 */
public class Matrix2DTest {
    public static void main(String[] args) {
        Matrix2D matrix = new Matrix2D(5,6);
        Matrix2x2 matrix2 = new Matrix2x2(1,3,2,4);

        Matrix2D result = matrix.multiply(matrix2);
        Matrix2x2 resultTwo = matrix.multiplyCorrect(matrix);
        System.out.println(result);
        System.out.println(resultTwo);

    }
}
