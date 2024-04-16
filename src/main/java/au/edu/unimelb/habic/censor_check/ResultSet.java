package au.edu.unimelb.habic.censor_check;

/**
 * Class to keep track of the classification statistics. 
 */
public class ResultSet implements Cloneable {

	private int truePositives;
	private int falsePositives;
	private int trueNegatives;
	private int falseNegatives;
	private int classMiss;

	/**
	 * Create a new Result set. It will have all zero values.
	 */
	public ResultSet() {
		truePositives = 0;
		falsePositives = 0;
		trueNegatives = 0;
		falseNegatives = 0;
		classMiss = 0;
	}
	
	/**
	 * Copy constructor.
	 * @param truePositives
	 * @param falsePositives
	 * @param trueNegatives
	 * @param falseNegatives
	 * @param classMiss
	 */
	public ResultSet(int truePositives, int falsePositives, int trueNegatives, int falseNegatives, int classMiss) {
		this.truePositives = truePositives;
		this.falsePositives = falsePositives;
		this.trueNegatives = trueNegatives;
		this.falseNegatives = falseNegatives;
		this.classMiss = classMiss;
	}

	/**
	 * Record a true positive.
	 */
	public void addTruePositive() {
		truePositives++;
	}

	/**
	 * Record a false positive.
	 */
	public void addFalsePositive() {
		falsePositives++;
	}

	/**
	 * Record a true negative.
	 */
	public void addTrueNegative() {
		trueNegatives++;
	}

	/**
	 * Record a false negative.
	 */
	public void addFalseNegative() {
		falseNegatives++;
	}

	/**
	 * Record a class miss (this is typically paired with a true positive).
	 */
	public void addClassMiss() {
		classMiss++;
	}

	public int truePositives() {
		return truePositives;
	}

	public int falsePositive() {
		return falsePositives;
	}

	public int trueNegative() {
		return trueNegatives;
	}

	public int falseNegative() {
		return falseNegatives;
	}

	public int classMiss() {
		return classMiss;
	}
	
	@Override
	public ResultSet clone() {
		return new ResultSet(truePositives, falsePositives, trueNegatives, falseNegatives, classMiss);
	}

	/**
	 * Add the values of the other result set creating a new one containing the results.
	 * @param other The other values to add.
	 * @return A new ResultSet containing the sum of the values.
	 */
	public ResultSet add(ResultSet other) {
		return new ResultSet(other.truePositives + truePositives, other.falsePositives + falsePositives, other.trueNegatives + trueNegatives, other.falseNegatives + falseNegatives, other.classMiss + classMiss);
	}
	
	@Override
	public String toString() {
		return String.format("tp: %d fp: %d tn: %d fn: %d classMiss: %d", truePositives, falsePositives, trueNegatives, falseNegatives, classMiss);
	}
}
