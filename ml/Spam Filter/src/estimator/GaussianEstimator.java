package estimator;

import feature.TestingEmail;
import feature.Fold;

public class GaussianEstimator extends Estimator {
	private float []hamVar;
	private float []spamVar;
	private float []hamMean;
	private float []spamMean;
	public GaussianEstimator(){
		this.title = "Gaussian Model";
	}
	public float[] getHamVar() {
		return hamVar;
	}
	public void setHamVar(float[] hamVar) {
		this.hamVar = hamVar;
	}
	public float[] getSpamVar() {
		return spamVar;
	}
	public void setSpamVar(float[] spamVar) {
		this.spamVar = spamVar;
	}
	public float[] getHamMean() {
		return hamMean;
	}
	public void setHamMean(float[] hamMean) {
		this.hamMean = hamMean;
	}
	public float[] getSpamMean() {
		return spamMean;
	}
	public void setSpamMean(float[] spamMean) {
		this.spamMean = spamMean;
	}
	/*public void estimate(Fold testData){
		getThresholds(testData);
		calculate();
	}*/
	public float[] getThresholds(Fold testData){
		int hamCount  = getEmailCount() - getSpamCount();
		int size = testData.size();
		float []thresholds = new float[size];
		for(int i=0;i<size;i++){
			TestingEmail email = (TestingEmail)testData.getEmail(i);
			float PRwi = 0.0f;
			for(int j=0;j<57;j++){
				float Xj = email.get(j);
				float H = (float)Math.pow((Xj - hamMean[j]), 2.0);
				H = H/(2.0f *hamVar[j]);
				float S = (float)Math.pow((Xj - spamMean[j]), 2.0);
				S = S/(2.0f * spamVar[j]);				
				PRwi += H - S;												
				PRwi += Math.log((Math.sqrt(hamVar[j]))/(Math.sqrt(spamVar[j])));
			}
			PRwi += Math.log((float)(getSpamCount())/(float)(hamCount));
			email.setPredictedSpam(PRwi > Estimator.THRESHOLD);
			email.setPR(PRwi);
			thresholds[i] = PRwi;
			evaluate(PRwi > Estimator.THRESHOLD, email.isSpam());
		}
		return thresholds;		
	}
}
