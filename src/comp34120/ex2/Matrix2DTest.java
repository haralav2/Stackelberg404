package comp34120.ex2;

public class Matrix2DTest {
    public static void main(String[] args) {
        Matrix2D matrix = new Matrix2D(5,6);
        Matrix2x2 matrix2 = new Matrix2x2(1,2,3,4);

        Matrix2D result = matrix.multiply(matrix2);
        Matrix2x2 resultTwo = matrix.multiply(matrix);
        Matrix2x2 resultThree = matrix2.inverse();
        Matrix2x2 resultFour = matrix2.multiply(matrix2);

        System.out.println(result);
        System.out.println(resultTwo);
        System.out.println(resultThree);
        System.out.println();
        System.out.println(resultFour);
    }
}
