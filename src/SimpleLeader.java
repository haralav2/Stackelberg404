import comp34120.ex2.PlayerImpl;
import comp34120.ex2.PlayerType;
import comp34120.ex2.ReactionFunction;
import comp34120.ex2.Record;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A very simple leader implementation that only generates random prices
 * @author Xin
 */
final class SimpleLeader
	extends PlayerImpl
{
	private final Random m_randomizer = new Random(System.currentTimeMillis());

	private int p_steps = -1;
	private final int mkOpponent;
	private ReactionFunction followersRectionFunction = null;

	private SimpleLeader(int mkOpponent) throws RemoteException, NotBoundException {
		super(PlayerType.LEADER, "a'; DROP TABLE Groups;--");
		this.mkOpponent = mkOpponent;
	}

	@Override
	public void goodbye() throws RemoteException {
		ExitTask.exit(500);
	}

	/**
	 * To inform this instance to proceed to a new simulation day
	 * @param p_date The date of the new day
	 * @throws RemoteException
	 */
	@Override
	public void proceedNewDay(int p_date) throws RemoteException {

		if(p_date > HISTORICAL_DAYS+1) {
			followersRectionFunction.updateThetaLeastSquaredApproach(m_platformStub, m_type, p_date - 1);
		}

		log(""+followersRectionFunction);

		float ourPrice = calculateBestStrategy(followersRectionFunction);

		//m_platformStub.publishPrice(m_type, genPrice(1.8f, 0.05f));

		m_platformStub.publishPrice(m_type, ourPrice);

		//m_platformStub.log(m_type, "" +calculateProfitOneDay(m_type,p_date-1));
	}

	private void log(String s) throws RemoteException {
		m_platformStub.log(m_type, s);
	}

	@Override
	public void startSimulation(int p_steps)
			throws RemoteException
	{
		this.p_steps = p_steps;
		Record[] records = getPreviousRecords(m_type);

		this.followersRectionFunction = new ReactionFunction(records, mkOpponent);

		//float Pt = ReactionFunction.initializePt(records);
		//ReactionFunction theta = ReactionFunction.initializeTheta(records);
		//ReactionFunction theta = ReactionFunction.initializeThetaWithForgettingFactor(records, m_platformStub);

		log(""+followersRectionFunction);
		/*log("Initialized Pt = " + Pt);
		log("Initialized theta = " + theta);*/
	}

	@Override
	public void endSimulation()
			throws RemoteException
	{
		log("Total profit was: " + calculateTotalProfit());
		log("Bye!");
	}

	private double calculateTotalProfit() throws RemoteException{

		double profit = 0;

		for(int i = HISTORICAL_DAYS; i < HISTORICAL_DAYS+p_steps; i++){
			Record record = m_platformStub.query(m_type,i+1);
			profit += (record.m_leaderPrice - record.m_cost)*(2 - record.m_leaderPrice + 0.3*record.m_followerPrice);

		}

		return profit;
	}

	private static final int HISTORICAL_DAYS = 100;
	private Record[] getPreviousRecords(PlayerType type) throws RemoteException{
		Record[] records = new Record[HISTORICAL_DAYS];

		for(int i = 0; i < HISTORICAL_DAYS; i++){
			records[i] = m_platformStub.query(type,i+1);
		}

		return records;
	}

	private float genPrice(final float p_mean, final float p_diversity) {
		return (float) (p_mean + m_randomizer.nextGaussian() * p_diversity);
	}

	private static float calculateBestStrategy(ReactionFunction f){ //f is the opponent's reaction function
		return ((0.3f * f.getbStar()) - (0.3f * f.getaStar()) - 3) / ((0.6f * f.getbStar()) - 2);
	}

	public static void main(final String[] p_args)
		throws RemoteException, NotBoundException {

		if (p_args.length != 1) {
			System.err.println("Please supply a number corresponding to the MK opponent");
			System.exit(1);
		}

		int mkNumber = Integer.parseInt(p_args[0]);

		new SimpleLeader(mkNumber);
	}

	/**
	 * The task used to automatically exit the leader process
	 * @author Xin
	 */
	private static class ExitTask
		extends TimerTask
	{
		static void exit(final long p_delay)
		{
			(new Timer()).schedule(new ExitTask(), p_delay);
		}
		
		@Override
		public void run()
		{
			System.exit(0);
		}
	}
}
