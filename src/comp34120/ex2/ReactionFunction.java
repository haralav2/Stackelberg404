package comp34120.ex2;

import java.rmi.RemoteException;

/**
 * Created by mbax2vh2 on 22/04/16.
 */
public class ReactionFunction extends Matrix2D{

    public ReactionFunction(double a, double b) {
        super(a, b);
    }

    private double getFollowersApproxPrice(double leaderPrice){
        return a + b * leaderPrice;
    }

    public double getbStar() {
        return b;
    }

    public double getaStar() {
        return a;
    }


    @Override
    protected ReactionFunction plus(Matrix2D m){
        return new ReactionFunction(a + m.a, b + m.b);
    }

    @Override
    protected ReactionFunction multiply(double s){
        return new ReactionFunction(a * s, b * s);
    }

    @Override
    protected ReactionFunction divide(double s){
        return multiply(1 / s);
    }

    public static ReactionFunction getFollowersReactionFunction(Record[] records){
        double lSum = getLeaderSum(records);
        double fSum = getFollowerSum(records);
        double lfpSum = getLeaderFollowerProductSum(records);
        double lSumSquared = getLeaderSumSquared(records);

        //TODO REFACTOR!!!!!!!
        double aStarDenominator = (lSumSquared * fSum) - (lSum * lfpSum);
        double nominator = (records.length * lSumSquared) - (lSum * lSum);

        double aStar = aStarDenominator / nominator;

        double bStarDenominator = (records.length * lfpSum) - (lSum * fSum);

        double bStar = bStarDenominator / nominator;

        return new ReactionFunction(aStar, bStar);
    }

    private static final int HISTORICAL_DAYS = 100;
    private static final double FORGETTING_FACTOR = 0.95;
    private static double Pt = 0;

    public static ReactionFunction getTheta() {
        return theta;
    }

    private static ReactionFunction theta = null;


    public static double initializePt(Record[] records){
        for(int d = 1; d <= HISTORICAL_DAYS; d++){ //TODO
            double leaderPrice = records[d-1].m_leaderPrice;
            Pt += Math.pow(FORGETTING_FACTOR, HISTORICAL_DAYS-d) * (1 + leaderPrice*leaderPrice);
        }
        return Pt;
    }

    public static ReactionFunction initializeTheta(Record[] records){
        theta = getFollowersReactionFunction(records);
        return theta;
    }

    public static void updateThetaLeastSquaredApproach(Platform m_platformStub, PlayerType type, int day) throws RemoteException{

        Record record = m_platformStub.query(type,day);
        double leaderPrice = record.m_leaderPrice;
        double followerPrice = record.m_followerPrice;

        Matrix2D adjustingFactor = calculateAdjustingFactor(leaderPrice);

        double predictionError = followerPrice - theta.getFollowersApproxPrice(leaderPrice);

        //TODO absolute value????

        m_platformStub.log(type, "Prediction error: (" + followerPrice + " - " + theta.getFollowersApproxPrice(leaderPrice) + " = " + predictionError);

        Matrix2D secondPart = adjustingFactor.multiply(predictionError);

        theta = theta.plus(secondPart);

        updatePt(leaderPrice);

        //todo floor

    }

    private static void updatePt(double leaderPrice){
        double scalar = 1 / FORGETTING_FACTOR;

        double phiNumerator = new Matrix2D(1, leaderPrice).multiply(new Matrix2D(1, leaderPrice));

        //TODO DOUBLE CHECK THE Pt^2
        double numerator = Pt * Pt * phiNumerator;
        double denominator = calculatePhiDenominator(leaderPrice);


        Pt = scalar * (Pt - numerator/denominator);
    }

    public static Matrix2D calculateAdjustingFactor(double leaderPrice){
        double nominator = calculatePhiDenominator(leaderPrice);

        Matrix2D denominator = new Matrix2D(1, leaderPrice).multiply(Pt);

        return denominator.divide(nominator);
    }

    private static double calculatePhiDenominator(double leaderPrice){
        return FORGETTING_FACTOR + Pt + Pt * leaderPrice * leaderPrice;
    }



    private static double getLeaderSum(Record[] records){
        double d = 0;

        for(Record r : records){
            d += r.m_leaderPrice;
        }

        return d;
    }

    private static double getFollowerSum(Record[] records){
        double d = 0;

        for(Record r : records){
            d += r.m_followerPrice;
        }

        return d;
    }

    private static double getLeaderFollowerProductSum(Record[] records){
        double d = 0;

        for(Record r : records){
            d += (r.m_leaderPrice * r.m_followerPrice);
        }

        return d;
    }


    private static double getLeaderSumSquared(Record[] records){
        double d = 0;

        for(Record r : records){
            d += (r.m_leaderPrice * r.m_leaderPrice);
        }

        return d;
    }

    @Override
    public String toString() {
        return "R^ = " + a + " + " + b + "Ul";
    }
}
