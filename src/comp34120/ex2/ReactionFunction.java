package comp34120.ex2;

import java.rmi.RemoteException;

/**
 * Created by mbax2vh2 on 22/04/16.
 */
public class ReactionFunction {
    private double aStar, bStar;

    public ReactionFunction(double aStar, double bStar) {
        this.aStar = aStar;
        this.bStar = bStar;
    }

    private double getFollowersApproxPrice(double leaderPrice){
        return aStar + bStar * leaderPrice;
    }

    public double getbStar() {
        return bStar;
    }

    public double getaStar() {
        return aStar;
    }

    public static ReactionFunction getFollowersReactionFunction(Record[] records){
        double lSum = getLeaderSum(records);
        double fSum = getFollowerSum(records);
        double lfpSum = getLeaderFollowerProductSum(records);
        double lSumSquared = getLeaderSumSquared(records);

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

    /*
    public static ReactionFunction recursiveLeastSquareApproach(Platform m_platformStub, PlayerType type, int day) throws RemoteException{
        if(day == 1){
            //base case
        }
        else {

            ReactionFunction oldReactionFunction = recursiveLeastSquareApproach(m_platformStub, type, day-1);

            Record record = m_platformStub.query(type,day);

            double predictionError = record.m_followerPrice - oldReactionFunction.getFollowersApproxPrice(record.m_leaderPrice);
            //todo floor

        }

    }

    public static double calculateP(int day){
        if(day == 100){

        }
        else{
            double oldP = calculateP(day-1);


        }
    }
`   */

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
        return "R^ = " + aStar + " + " + bStar + "Ul";
    }
}
