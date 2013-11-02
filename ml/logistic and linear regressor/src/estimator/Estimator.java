package estimator;

import java.util.ArrayList;
import java.util.Arrays;

import org.jfree.data.xy.XYSeries;

import feature.Fold;
import feature.TestingEmail;

abstract public class Estimator {
	public static final int THRESHOLD = 0; 
	private int spamCount = 0;
	protected String title = null;
	private int emailCount = 0;
	protected int falsePositive = 0;
	protected int falseNegative = 0;
	protected int truePositive  = 0;
	protected int trueNegative  = 0;			
	protected int s = 0;
	private ArrayList<Float> truePositiveRate;
	private ArrayList<Float> falsePositiveRate;
	public void setSpamCount(int spamCount) {
		this.spamCount = spamCount;
	}
	public int getSpamCount() {
		return spamCount;
	}	
	public int getEmailCount() {
		return emailCount;
	}
	public void setEmailCount(int emailCount) {
		this.emailCount = emailCount;
	}
	public void estimate(Fold testData){		
		float []thresholds = getThresholds(testData);
		calculate();
		AUC(testData, thresholds);
	}
	public float[] getThresholds(Fold testData){
		return null;
	}
	protected void evaluate(boolean isSpamPredicted, boolean isActuallySpam){
		if((isSpamPredicted) && !isActuallySpam) falsePositive++; // missclassified as spam
		if((!isSpamPredicted) && isActuallySpam) falseNegative++; // missclassified as non-spam	
		if((!isSpamPredicted) && !isActuallySpam) trueNegative++;
		if((isSpamPredicted) && isActuallySpam) truePositive++;	  // 1 - falseNegative
		if(isSpamPredicted) s++;
	}
	protected void calculate(){
//		System.out.println("P:"+falsePositive);
		float fpr = (float)falsePositive/(float)(falsePositive + trueNegative);
		float fnr = (float)falseNegative/(float)(falseNegative + truePositive);
		float tpr = (float)(truePositive) / (float)(truePositive + falseNegative);
		System.out.println(fpr+ "			"+ fnr +"			"+tpr +"			" + (fpr  + fnr)/2.0f);		
	}
	protected void ROC(Fold testFold, float []thresholds){
		Arrays.sort(thresholds);
		this.truePositiveRate = new ArrayList<Float>();
		this.falsePositiveRate = new ArrayList<Float>();
		this.falsePositiveRate.add(0.0f);
		this.truePositiveRate.add(0.0f);				
		for (int i = thresholds.length - 1; i>= 0; i--){
			if(thresholds[i]<0)break;
			int truePositive = 0;
			int trueNegative = 0;
			int falsePositive= 0;
			int falseNegative= 0;
			
			for(int j = 0; j<testFold.size(); j++){
				TestingEmail email = (TestingEmail)testFold.getEmail(j);
				if(email.getPR() == thresholds[i])continue;
				
				if(email.isPredictedSpamWithDifferentThreshold(thresholds[i]) && email.isSpam()) truePositive++;
				if(email.isPredictedSpamWithDifferentThreshold(thresholds[i]) && !email.isSpam()) falsePositive++;
				if(!email.isPredictedSpamWithDifferentThreshold(thresholds[i]) && email.isSpam()) falseNegative++;
				if(!email.isPredictedSpamWithDifferentThreshold(thresholds[i]) && !email.isSpam()) trueNegative++; 				
			}
			float truePositiveRate = (float)truePositive/(float)(truePositive + falseNegative);
			this.truePositiveRate.add(truePositiveRate);
			float falsePositiveRate= (float)falsePositive/(float)(falsePositive + trueNegative);
			this.falsePositiveRate.add(falsePositiveRate);
		}
		this.falsePositiveRate.add(1.0f);
		this.truePositiveRate.add(1.0f);
	}
	public XYSeries ROCCurve(Fold testFold, float []thresholds){
		ROC(testFold, thresholds);
		XYSeries series =  new XYSeries(this.title);
		for(int i = 0; i<this.truePositiveRate.size();i++){
			series.add(this.falsePositiveRate.get(i), this.truePositiveRate.get(i));
		}				
		return series;
	}
	public void AUC(Fold testFold, float []thresholds){
		ROC(testFold, thresholds);
		float auc = 0.0f;
		for(int i = 1; i<this.truePositiveRate.size();i++){
			auc += (this.truePositiveRate.get(i) + this.truePositiveRate.get(i - 1))
					* (this.falsePositiveRate.get(i) - this.falsePositiveRate.get(i - 1));
		}
		auc = 0.5f * auc;
		System.out.println("AUC:" + auc);
	}
}
