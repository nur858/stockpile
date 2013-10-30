package feature;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import chart.MultiROCCurve;
import estimator.BernouliEstimator;
import estimator.GaussianEstimator;
import estimator.HistogramEstimator;

public class Extractor {
	private Fold []folds;
		
	public Extractor() {
		folds = new Fold[10];
		for(int i = 0; i<10;i++) folds[i] = new Fold();
		try {
			BufferedReader br = new BufferedReader(new FileReader("spambase/spambase.data"));
			String str = "";
			for (int i= 0;i<461;i++){
				for(int j = 0; j<10;j++){					
					if((str = br.readLine()) != null){						
						folds[j].add(i, this.toFloat(str.split(",")), j==9);
					}
				}
			}
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	float[] toFloat(String[] str){
		float []f = new float[58];
		int counter = 0;
		for (String s : str)
			f[counter++] = Float.parseFloat(s);
		return f;
	}
	public void estimate(){
		Fold testData = folds[9]; // treat last fold as test data.
		System.out.println("			False Positive			False Negative			TruePositive			Overall");
		System.out.println("-------------------------------------------------------------------------------------------");
		System.out.print("Bernouli 		");
		for (int i = 0; i<10;i++){
			BernouliEstimator e = folds[i].getBernouliEstimator();
			e.estimate(testData);
			System.out.print("			");
		}
		System.out.println();
		System.out.print("Gaussian 		");
		for (int i = 0; i<10;i++){
			GaussianEstimator e = folds[i].getGaussianEstimator();
			e.estimate(testData);
			System.out.print("			");			
		}
		System.out.println();
		System.out.print("Histogram 		");
		for (int i = 0; i<10;i++){
			HistogramEstimator e = folds[i].getHistogramEstimator();
			e.estimate(testData);
			System.out.print("			");
		}
		System.out.println();
		
		MultiROCCurve curve = new MultiROCCurve("ROC Curve");		
		
		BernouliEstimator be = folds[0].getBernouliEstimator();
		float[] thresholds = be.getThresholds(testData);		
		curve.addCurve("Bernouli", be.ROCCurve(testData, thresholds));
		
		GaussianEstimator ge = folds[0].getGaussianEstimator();
		thresholds = ge.getThresholds(testData);
		curve.addCurve("Gaussian", ge.ROCCurve(testData, thresholds));	
		
		HistogramEstimator he = folds[0].getHistogramEstimator();
		thresholds = he.getThresholds(testData);					
		curve.addCurve("Histogram", he.ROCCurve(testData, thresholds));
	}
}
