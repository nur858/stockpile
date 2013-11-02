package estimator;

import java.util.ArrayList;
import java.util.HashMap;
import feature.Fold;
import feature.TestingEmail;

public class HistogramEstimator  extends Estimator{
	private HashMap<Integer,ArrayList<Float>> spamBins;
	private HashMap<Integer,ArrayList<Float>> hamBins;
	private float []spamClassMean;
	private float []hamClassMean;
	public HistogramEstimator(){
		spamBins = new HashMap<Integer,ArrayList<Float>>();
		hamBins = new HashMap<Integer,ArrayList<Float>>();
		this.title = "Histogram Model";
	}
	public HashMap<Integer, ArrayList<Float>> getSpamBins() {
		return spamBins;
	}
	public void setSpamBins(HashMap<Integer, ArrayList<Float>> spamBins) {
		this.spamBins = spamBins;
	}
	public HashMap<Integer, ArrayList<Float>> getHamBins() {
		return hamBins;
	}
	public void setHamBins(HashMap<Integer, ArrayList<Float>> hamBins) {
		this.hamBins = hamBins;
	}
	public float[] getSpamClassMean() {
		return spamClassMean;
	}
	public void setSpamClassMean(float[] spamClassMean) {
		this.spamClassMean = spamClassMean;
	}
	public float[] getHamClassMean() {
		return hamClassMean;
	}
	public void setHamClassMean(float[] hamClassMean) {
		this.hamClassMean = hamClassMean;
	}
	/*public void estimate(Fold testData){
		getThresholds(testData);
		calculate();
	}*/
	public float[] getThresholds(Fold testData){
		int hamCount  = getEmailCount() - getSpamCount();		
		int size = testData.size();
		float []thresholds = new float[size];
		for(int j=0;j<size;j++){
			float PRwi = 0.0f;
			TestingEmail email = (TestingEmail)testData.getEmail(j);			
			for(int i=0;i<57;i++){
				float feature = email.get(i);
				if((feature <= Math.min(spamClassMean[i], hamClassMean[i])))
						PRwi += Math.log(spamBins.get(i).get(0)/hamBins.get(i).get(0));
				else if (( Math.min(spamClassMean[i], hamClassMean[i]) < feature ) && (feature <= Fold.mean[i]) )
					PRwi += Math.log(spamBins.get(i).get(1)/hamBins.get(i).get(1));
				else if ( (Fold.mean[i] < feature) && (feature <= Math.max(spamClassMean[i], hamClassMean[i])) )
					PRwi += Math.log(spamBins.get(i).get(2)/hamBins.get(i).get(2));
				else if( (Math.max(spamClassMean[i], hamClassMean[i]) < feature))
					PRwi += Math.log(spamBins.get(i).get(3)/hamBins.get(i).get(3));
			}
			PRwi += Math.log((float)(getSpamCount())/(float)(hamCount));
			email.setPredictedSpam(PRwi > Estimator.THRESHOLD);
			email.setPR(PRwi);
			thresholds[j] = PRwi;
			evaluate(PRwi > Estimator.THRESHOLD, email.isSpam());
		}				
		return thresholds;		
	}
}
