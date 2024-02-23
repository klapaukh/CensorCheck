package au.edu.unimelb.habic.censor_check;

public class ResultSet implements Cloneable {

	private int truePositives;
	private int falsePositives;
	private int trueNegatives;
	private int falseNegatives;
	private int classMiss;

	public ResultSet() {
		truePositives = 0;
		falsePositives = 0;
		trueNegatives = 0;
		falseNegatives = 0;
		classMiss = 0;
	}
	
	public ResultSet(int truePositives, int falsePositives, int trueNegatives, int falseNegatives, int classMiss) {
		this.truePositives = truePositives;
		this.falsePositives = falsePositives;
		this.trueNegatives = trueNegatives;
		this.falseNegatives = falseNegatives;
		this.classMiss = classMiss;
	}

	public void addTruePositive() {
		truePositives++;
	}

	public void addFalsePositive() {
		falsePositives++;
	}

	public void addTrueNegative() {
		trueNegatives++;
	}

	public void addFalseNegative() {
		falseNegatives++;
	}

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

	public ResultSet add(ResultSet other) {
		return new ResultSet(other.truePositives + truePositives, other.falsePositives + falsePositives, other.trueNegatives + trueNegatives, other.falseNegatives + falseNegatives, other.classMiss + classMiss);
	}
	
	@Override
	public String toString() {
		return String.format("tp: %d fp: %d tn: %d fn: %d classMiss: %d", truePositives, falsePositives, trueNegatives, falseNegatives, classMiss);
	}
}
