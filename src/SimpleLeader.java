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

	private int p_steps = -1;

	private SimpleLeader()
		throws RemoteException, NotBoundException
	{
		super(PlayerType.LEADER, "Simple Leader");
	}

	@Override
	public void goodbye()
		throws RemoteException
	{
		ExitTask.exit(500);
	}

	/**
	 * To inform this instance to proceed to a new simulation day
	 * @param p_date The date of the new day
	 * @throws RemoteException
	 */
	@Override
	public void proceedNewDay(int p_date)
		throws RemoteException
	{
		/*Record[] records = getPreviousRecords(m_type);

		m_platformStub.log(PlayerType.LEADER, "Records is: " + Arrays.toString(records));

		ReactionFunction followerReactionFunction = ReactionFunction.getFollowersReactionFunction(records);*/

		//m_platformStub.log(PlayerType.LEADER, "Reaction function is: " + followerReactionFunction);

		log("Theta before: " + ReactionFunction.getTheta().toString());

		if(p_date > 101) {
			ReactionFunction.updateThetaLeastSquaredApproach(m_platformStub, m_type, p_date-1);
		}

		log("Theta after: " + ReactionFunction.getTheta().toString());

		double ourPrice = calculateBestStrategy(ReactionFunction.getTheta());

		m_platformStub.publishPrice(m_type, (float)ourPrice);

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

		double Pt = ReactionFunction.initializePt(records);
		ReactionFunction theta = ReactionFunction.initializeTheta(records);

		m_platformStub.log(m_type, "Initialized Pt = " + Pt);
		m_platformStub.log(m_type, "Initialized theta = " + theta);
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

		for(int i = 0; i < 100+this.p_steps; i++){
			Record record = m_platformStub.query(m_type,i+1);

			profit += (record.m_leaderPrice - record.m_cost)*(2 - record.m_leaderPrice + 0.3*record.m_followerPrice);

		}

		return profit;
	}

	private Record[] getPreviousRecords(PlayerType type) throws RemoteException{
		Record[] records = new Record[100]; //todo

		for(int i = 0; i < 100; i++){
			records[i] = m_platformStub.query(type,i+1);
		}

		return records;
	}

	private static double calculateBestStrategy(ReactionFunction f){ //f is the opponent's reaction function
		return ((0.3 * f.getbStar()) - (0.3 * f.getaStar()) - 3) / ((0.6 * f.getbStar()) - 2);
	}

	public static void main(final String[] p_args)
		throws RemoteException, NotBoundException
	{
		new SimpleLeader();
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
