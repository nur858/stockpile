package regressor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.jfree.data.xy.XYSeries;

import chart.MultiROCCurve;
import feature.Fold;
import feature.TestingEmail;
import feature.TrainingEmail;

public class Regressor {
	private HashMap<Integer, TrainingEmail> data;
	private float[] omega;
	private float []thresholds;
	private Fold testData;
	private float truePositive  = 0.0f;
	private float trueNegative  = 0.0f;
	private float falsePositive = 0.0f;
	private float falseNegative = 0.0f;
	private ArrayList<Float> truePositiveRate;
	private ArrayList<Float> falsePositiveRate;
	protected String title;
	public HashMap<Integer, TrainingEmail> getData() {
		return data;
	}

	public void setData(HashMap<Integer, TrainingEmail> data) {
		this.data = data;
	}

	public float[] getOmega() {
		return omega;
	}

	public void setOmega(float[] omega) {
		this.omega = omega;
	}
	public Regressor predict(Fold testData){	
		truePositive  = 0.0f;
		trueNegative  = 0.0f;
		falsePositive = 0.0f;
		falseNegative = 0.0f;
		this.testData = testData;
		thresholds = new float[testData.size()];
		for (int i = 0; i < testData.size();i++){
			float predicted = 0.0f;
			TestingEmail t = (TestingEmail)testData.getEmail(i);
			predicted = omega[0];
			for(int j = 1; j < Fold.featureCount;j++ )
				predicted += omega[j] * t.get(j);
			if ( this instanceof StochasticLogisticRegressor || this instanceof BatchLogisticRegressor)
				predicted = (float) (1.0f /(1.0f + Math.exp(-predicted)));
			thresholds[i] = predicted;			
			t.setPredictedSpam(predicted > 0);
			t.setPR(predicted);
			if((t.isPredictedSpam()) && !t.isSpam()) falsePositive++; // missclassified as spam
			if((!t.isPredictedSpam()) && t.isSpam()) falseNegative++; // missclassified as non-spam	
			if((!t.isPredictedSpam()) && !t.isSpam()) trueNegative++;
			if((t.isPredictedSpam()) && t.isSpam()) truePositive++;	  // 1 - falseNegative			
		}
		return this;
	}
	public Regressor ROC(MultiROCCurve curve){
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
			
			for(int j = 0; j<testData.size(); j++){
				TestingEmail email = (TestingEmail)testData.getEmail(j);
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
		XYSeries series =  new XYSeries(this.title);
		for(int i = 0; i<this.truePositiveRate.size();i++){
			series.add(this.falsePositiveRate.get(i), this.truePositiveRate.get(i));
		}				
		curve.addCurve(this.title, series);		
		return this;
	}
	
	public Regressor AUC(){
		float auc = 0.0f;
		for(int i = 1; i<this.truePositiveRate.size();i++){
			auc += (this.truePositiveRate.get(i) + this.truePositiveRate.get(i - 1))
					* (this.falsePositiveRate.get(i) - this.falsePositiveRate.get(i - 1));
		}
		auc = 0.5f * auc;
		System.out.println(this.title+ " AUC:" + auc);
		return this;
	}
	public void RmseVSIteration(MultiROCCurve curve, float rate1, float rate2, float rate3){
		try {
			String d = new BigDecimal(Float.toString(rate1)).toPlainString();
			BufferedReader br = new BufferedReader(new FileReader("stochastic-gradient0.01.txt"));
			String str = null;
			XYSeries series =  new XYSeries("" + rate1);
			while((str = br.readLine()) != null){
				String []features = str.split(" ");
				series.add(Float.parseFloat(features[0]), Float.parseFloat(features[1]));				
			}
			br.close();
			curve.addCurve("" + rate1, series);
			d = new BigDecimal(Float.toString(rate2)).toPlainString();
			br = new BufferedReader(new FileReader("stochastic-gradient0.001.txt"));
			str = null;
			series =  new XYSeries("" + rate2);
			while((str = br.readLine()) != null){
				String []features = str.split(" ");
				series.add(Float.parseFloat(features[0]), Float.parseFloat(features[1]));				
			}
			br.close();
			curve.addCurve("" + rate2, series);
			d = new BigDecimal(Float.toString(rate3)).toPlainString();
			br = new BufferedReader(new FileReader("stochastic-gradient0.00010.txt"));
			str = null;
			series =  new XYSeries("" + rate3);
			while((str = br.readLine()) != null){
				String []features = str.split(" ");
				series.add(Float.parseFloat(features[0]), Float.parseFloat(features[1]));				
			}
			br.close();
			curve.addCurve("" + rate3, series);			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}