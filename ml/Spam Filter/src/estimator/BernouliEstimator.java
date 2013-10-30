package estimator;

import feature.TestingEmail;
import feature.Fold;

public class BernouliEstimator extends Estimator{
	private int []unlikelyHamCount;
	private int []likelySpamCount;
	public int[] getUnlikelyHamCount() {
		return unlikelyHamCount;
	}

	public void setUnlikelyHamCount(int[] unlikelyHamCount) {
		this.unlikelyHamCount = unlikelyHamCount;
	}

	public int[] getLikelySpamCount() {
		return likelySpamCount;
	}

	public void setLikelySpamCount(int[] likelySpamCount) {
		this.likelySpamCount = likelySpamCount;
	}

	public BernouliEstimator() {
		this.title = "Bernouli Model";
	}
	public float[] getThresholds(Fold testData){
		int hamCount = getEmailCount() - getSpamCount();
		int size = testData.size();
		float []thresholds = new float[size];
		for(int i=0;i<size;i++){
			float PRwi = 0.0f;
			TestingEmail email = (TestingEmail)testData.getEmail(i);			
			for(int j=0;j<57;j++){
				float feature = email.get(j);
				float mean = Fold.mean[j];
				int likelyHamCount =  hamCount - unlikelyHamCount[j];
				int unlikelySpamCount = getSpamCount() - likelySpamCount[j];				
				if(feature<=mean){
					PRwi += Math.log( ((unlikelySpamCount+1)/(float)(getSpamCount() + 2))/((unlikelyHamCount[j]+1)/(float)(hamCount + 2)));					
				}
				if(feature>mean){
					PRwi += Math.log( ((likelySpamCount[j]+1)/(float)(getSpamCount() + 2)) / ((likelyHamCount+1)/(float)(hamCount + 2)));
				}
			}
			PRwi += Math.log((float)(getSpamCount())/(float)(hamCount));
			thresholds[i] = PRwi;
			email.setPredictedSpam(PRwi > Estimator.THRESHOLD);
			email.setPR(PRwi);
			evaluate(PRwi > Estimator.THRESHOLD, email.isSpam());
		}
		return thresholds;		
	}
}
