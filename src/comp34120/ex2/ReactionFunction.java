package comp34120.ex2;

import java.rmi.RemoteException;
import java.util.IllegalFormatCodePointException;

/**
 * Created by mbax2vh2 on 22/04/16.
 */
public class ReactionFunction extends Matrix2D{

    public ReactionFunction(float a, float b) {
        super(a, b);
    }

    private float getFollowersApproxPrice(float leaderPrice){
        return a + b * leaderPrice;
    }

    public float getbStar() {
        return b;
    }

    public float getaStar() {
        return a;
    }

    @Override
    protected ReactionFunction plus(Matrix2D m){
        return new ReactionFunction(a + m.a, b + m.b);
    }

    @Override
    protected ReactionFunction multiply(float s){
        return new ReactionFunction(a * s, b * s);
    }

    @Override
    protected ReactionFunction divide(float s){
        return multiply(1 / s);
    }

    public static ReactionFunction getFollowersReactionFunction(Record[] records){
        float lSum = getLeaderSum(records);
        float fSum = getFollowerSum(records);
        float lfpSum = getLeaderFollowerProductSum(records);
        float lSumSquared = getLeaderSumSquared(records);

        float aStarNumerator = (lSumSquared * fSum) - (lSum * lfpSum);
        float denominator = (records.length * lSumSquared) - (lSum * lSum);

        float aStar = aStarNumerator / denominator;

        float bStarNumerator = (records.length * lfpSum) - (lSum * fSum);


        float bStar = bStarNumerator / denominator;



        return new ReactionFunction(aStar, bStar);
    }

    private static float FORGETTING_FACTOR =  1.0f;

    public static float initializeForgettingFactor(int mkOpponent){
        switch (mkOpponent){
            case 1: FORGETTING_FACTOR = 0.995f; break;
            case 2: FORGETTING_FACTOR = 1.00f; break;
            case 3: FORGETTING_FACTOR = 1.00f; break;
            default: throw new IllegalArgumentException("Invalid MkOpponent number " + mkOpponent);
        }
        return FORGETTING_FACTOR;
    }

    private static final int HISTORICAL_DAYS = 5;
    private static float Pt = 0;

    public static ReactionFunction getTheta() {
        return theta;
    }

    private static ReactionFunction theta = null;

    public static float initializePt(Record[] records){
        //Best initial condition for recursive least squared approach. Slides 13

        for(int d = 1; d <= HISTORICAL_DAYS; d++){
            float leaderPrice = records[d-1].m_leaderPrice;
            Pt += Math.pow(FORGETTING_FACTOR, HISTORICAL_DAYS-d) * (1 + leaderPrice*leaderPrice);
        }
        return Pt;
    }

    public static ReactionFunction initializeTheta(Record[] records){
        theta = getFollowersReactionFunction(records);
        return theta;
    }

    public static ReactionFunction initializeThetaWithForgettingFactor(Record[] records){

        theta = new ReactionFunction(0, 0);

        for(int d = 1; d <= HISTORICAL_DAYS; d++){
            float leaderPrice = records[d-1].m_leaderPrice;
            float followerPrice = records[d-1].m_followerPrice;

            Matrix2D phiMatrix = new Matrix2D(followerPrice, followerPrice * leaderPrice);
            Matrix2D forgettingFactorMatrix = phiMatrix.multiply((float)Math.pow(FORGETTING_FACTOR, HISTORICAL_DAYS-d));
            theta = theta.plus(forgettingFactorMatrix);
            /*try {
                p.log(PlayerType.LEADER, "phiMatrix: " + phiMatrix);
               // p.log(PlayerType.LEADER, "forgettingMatrix: " + forgettingFactorMatrix);
                p.log(PlayerType.LEADER, "theta: " + theta);
            } catch (RemoteException e) {
                e.printStackTrace();
            }*/


        }

        theta = theta.divide(Pt);
        return theta;
    }

    public static void updateThetaLeastSquaredApproach(Platform m_platformStub, PlayerType type, int day)
            throws RemoteException{

        Record record = m_platformStub.query(type,day);
        float leaderPrice = record.m_leaderPrice;
        float followerPrice = record.m_followerPrice;

        Matrix2D adjustingFactor = calculateAdjustingFactor(leaderPrice);

        float predictionError = followerPrice - theta.getFollowersApproxPrice(leaderPrice);

        //m_platformStub.log(type,
        //        "Prediction error: (" + followerPrice + " - " + theta.getFollowersApproxPrice(leaderPrice)
        //                + " = " + predictionError);

        Matrix2D secondPart = adjustingFactor.multiply(predictionError);

        theta = theta.plus(secondPart);

        updatePt(leaderPrice);
    }

    private static void updatePt(float leaderPrice){
        float scalar = 1 / FORGETTING_FACTOR;

        float phiNumerator = new Matrix2D(1, leaderPrice).multiply(new Matrix2D(1, leaderPrice));

        float numerator = Pt * Pt * phiNumerator;
        float denominator = calculatePhiDenominator(leaderPrice);


        Pt = scalar * (Pt - numerator/denominator);
    }

    public static Matrix2D calculateAdjustingFactor(float leaderPrice){
        float denominator = calculatePhiDenominator(leaderPrice);

        Matrix2D numerator = new Matrix2D(1, leaderPrice).multiply(Pt);

        return numerator.divide(denominator);
    }

    private static float calculatePhiDenominator(float leaderPrice){
        return FORGETTING_FACTOR + Pt + (Pt * leaderPrice * leaderPrice);
    }

    private static float getLeaderSum(Record[] records){
        float d = 0;

        for(Record r : records){
            d += r.m_leaderPrice;
        }

        return d;
    }

    private static float getFollowerSum(Record[] records){
        float d = 0;

        for(Record r : records){
            d += r.m_followerPrice;
        }

        return d;
    }

    private static float getLeaderFollowerProductSum(Record[] records){
        float d = 0;

        for(Record r : records){
            d += (r.m_leaderPrice * r.m_followerPrice);
        }

        return d;
    }


    private static float getLeaderSumSquared(Record[] records){
        float d = 0;

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
