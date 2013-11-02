package feature;

public class TestingEmail extends TrainingEmail {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean isPredictedSpam;
	private float PR;
	public TestingEmail(int size) {
		super(size);
		isPredictedSpam = false;
		PR = 0.0f;
	}
	public boolean isPredictedSpam() {
		return isPredictedSpam;
	}

	public void setPredictedSpam(boolean isPredictedSpam) {
		this.isPredictedSpam = isPredictedSpam;
	}
	public float getPR() {
		return PR;
	}
	public void setPR(float pR) {
		PR = pR;
	}
	public boolean isPredictedSpamWithDifferentThreshold(float threshold){
		return PR>threshold;
	}
}
