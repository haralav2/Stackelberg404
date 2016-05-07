package comp34120.ex2;

import java.rmi.RemoteException;
import java.util.IllegalFormatCodePointException;

/**
 * Created by mbax2vh2 on 22/04/16.
 */
public class ReactionFunction {

    private Matrix2D theta = null;
    private Matrix2x2 Pt = new Matrix2x2(0,0,0,0);
    private final int mkOpponent;
    private static final int HISTORICAL_DAYS = 100;


    public ReactionFunction(Record[] records, int mkOpponent) {
        this.mkOpponent = mkOpponent;
        initializeForgettingFactor();
        initalizePt(records);
        initializeTheta(records);
    }

    private void initializeForgettingFactor(){
        switch (mkOpponent){
            case 1: FORGETTING_FACTOR = 0.995f; break;
            case 2: FORGETTING_FACTOR = 1.00f; break;
            case 3: FORGETTING_FACTOR = 1.00f; break;
            default: throw new IllegalArgumentException("Invalid MkOpponent number " + mkOpponent);
        }
    }

    private void initializeTheta(Record[] records){
        float lSum = getLeaderSum(records);
        float fSum = getFollowerSum(records);
        float lfpSum = getLeaderFollowerProductSum(records);
        float lSumSquared = getLeaderSumSquared(records);

        float aStarNumerator = (lSumSquared * fSum) - (lSum * lfpSum);
        float denominator = (records.length * lSumSquared) - (lSum * lSum);

        float aStar = aStarNumerator / denominator;

        float bStarNumerator = (records.length * lfpSum) - (lSum * fSum);


        float bStar = bStarNumerator / denominator;

        theta = new Matrix2D(aStar, bStar);
    }

    private void initalizePt(Record[] records){
        for(int d = 1; d <= HISTORICAL_DAYS; d++){
            float leaderPrice = records[d-1].m_leaderPrice;
            Matrix2D leaderMatrix = new Matrix2D(1, leaderPrice);
            Matrix2x2 multipliedLeaders = leaderMatrix.multiply(leaderMatrix);
            Matrix2x2 scalarMatrix = multipliedLeaders.multiply((float)Math.pow(FORGETTING_FACTOR, HISTORICAL_DAYS - d));
            Pt = Pt.plus(scalarMatrix);
        }
    }


    private float getFollowersApproxPrice(float leaderPrice){
        return theta.a + theta.b * leaderPrice;
    }

    public float getbStar() {
        return theta.b;
    }

    public float getaStar() {
        return theta.a;
    }

    private static float FORGETTING_FACTOR =  1.0f;

    /*
    public static ReactionFunction initializeThetaWithForgettingFactor(Record[] records){

        followersRectionFunction = new ReactionFunction(0, 0);

        for(int d = 1; d <= HISTORICAL_DAYS; d++){
            float leaderPrice = records[d-1].m_leaderPrice;
            float followerPrice = records[d-1].m_followerPrice;

            Matrix2D phiMatrix = new Matrix2D(followerPrice, followerPrice * leaderPrice);
            Matrix2D forgettingFactorMatrix = phiMatrix.multiply((float)Math.pow(FORGETTING_FACTOR, HISTORICAL_DAYS-d));
            followersRectionFunction = followersRectionFunction.plus(forgettingFactorMatrix);



        }
        Matrix2x2 inversePt = Pt.inverseMatrix();
        theta = theta.multiply(inversePt);
        return theta;
    }
    */

    public void updateThetaLeastSquaredApproach(Platform m_platformStub, PlayerType type, int day)
            throws RemoteException{

        Record record = m_platformStub.query(type,day);
        float leaderPrice = record.m_leaderPrice;
        float followerPrice = record.m_followerPrice;

        Matrix2D adjustingFactor = calculateAdjustingFactor(leaderPrice);

        float predictionError = followerPrice - getFollowersApproxPrice(leaderPrice);

        //m_platformStub.log(type,
        //        "Prediction error: (" + followerPrice + " - " + theta.getFollowersApproxPrice(leaderPrice)
        //                + " = " + predictionError);

        Matrix2D secondPart = adjustingFactor.multiply(predictionError);

        theta = theta.plus(secondPart);

        updatePt(leaderPrice);
    }

    private void updatePt(float leaderPrice){

        Matrix2D leaderMatrix = new Matrix2D(1, leaderPrice);
        Matrix2D leaderMatrixTimesPt = leaderMatrix.multiply(Pt);

        Matrix2x2 phiNumerator = leaderMatrixTimesPt.multiply(leaderMatrix).multiply(Pt);

        float denominator = calculatePhiDenominator(leaderPrice);
        phiNumerator = phiNumerator.multiply(1/denominator);


        Pt = Pt.minus(phiNumerator).multiply(1 / FORGETTING_FACTOR);
    }

    public Matrix2D calculateAdjustingFactor(float leaderPrice){
        float denominator = calculatePhiDenominator(leaderPrice);

        Matrix2D numerator = new Matrix2D(1, leaderPrice).multiply(Pt);

        return numerator.divide(denominator);
    }

    private float calculatePhiDenominator(float leaderPrice){

        Matrix2D leaderMatrix = new Matrix2D(1, leaderPrice);
        Matrix2D leaderMatrixTimesPt = leaderMatrix.multiply(Pt);

        float result = leaderMatrixTimesPt.multiplyTwo(leaderMatrix);

        return FORGETTING_FACTOR + result;
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
/*
    @Override
    public String toString() {
        //return "R^ = " + theta.a + " + " + theta.b + "Ul";
    }*/

    @Override
    public String toString() {
        return "ReactionFunction{" +
                "theta=" + theta +
                ", Pt=" + Pt +
                ", mkOpponent=" + mkOpponent +
                '}';
    }

    public Matrix2x2 getPt() {
        return Pt;
    }
}
