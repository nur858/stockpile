package feature;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import regressor.BatchGradientDescentRegressor;
import regressor.BatchLogisticRegressor;
import regressor.StochasticGradientDescentRegressor;
import regressor.StochasticLogisticRegressor;
import chart.MultiROCCurve;
public class Extractor {
	private Fold testFold;
	private Fold trainFold;
	public Extractor() {
		testFold = new Fold();
		trainFold = new Fold();
		ArrayList<Integer> randomIndex = new ArrayList<Integer>();
		for (int i =0; i<4140; i++) randomIndex.add(i);
		Collections.shuffle(randomIndex, new Random(System.nanoTime()));
		try {
			BufferedReader br = new BufferedReader(new FileReader("spambase/spambase.data"));
			String str = "";
			int emailCount = 0;int testCount  = 0; int trainCount = 0;
			while((str = br.readLine()) != null){
				float []features =this.toFloat(str.split(","));
				if((emailCount+1)%10 == 1)
					testFold.add(testCount++, features, true);
				else
					trainFold.add(randomIndex.get(trainCount++), features, false);
				emailCount++;
			}
			
			float []sum = new float[Chunk.featureCount];
				sum = Extractor.add(testFold.getPartialFeatureSum(), trainFold.getPartialFeatureSum());
			for(int i=0; i<Chunk.featureCount; i++)
				sum[i] = sum[i]/Chunk.totalCount;
			Fold.mean = sum;
			float []var = new float[Chunk.featureCount];
			
			var = Extractor.add(testFold.getPartialVariance(), trainFold.getPartialVariance());
			for(int i=0; i<Chunk.featureCount; i++)
				var[i] = (float)Math.sqrt(var[i]/(Chunk.totalCount-1));
			Fold.deviation = var;
			
			testFold.normalize();
			trainFold.normalize();
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	
	float[] toFloat(String[] str){
		float []f = new float[Chunk.featureCount];
		int counter = 0;
		for (String s : str)
			f[counter++] = Float.parseFloat(s);
		return f;
	}
	public void estimate(){
		MultiROCCurve curve = new MultiROCCurve("ROC Curve");	
		/*StochasticGradientDescentRegressor sgdr = trainFold.getStochasticGradientDescentRegressor();
		sgdr.learn(0.0001f);
		sgdr.predict(testFold).ROC(curve).AUC();*/
		/*BatchGradientDescentRegressor bgdr = trainFold.getBatchGradientDescentRegressor();
		bgdr.learn(0.001f);
		bgdr.predict(testFold).ROC(curve).AUC();*/		
		
		/*StochasticLogisticRegressor sldr = trainFold.getStochasticLogisticRegressor();
		sldr.learn(0.1f);
		sldr.predict(testFold).ROC(curve).AUC();*/
		BatchLogisticRegressor bldr = trainFold.getBatchLogisticRegressor();
		bldr.learn(0.0001f);
		bldr.predict(testFold).ROC(curve).AUC();				
		return;
	}
	public static float[] add(float[]a,float[]b){
		for(int i = 0; i < a.length; i++)
			a[i] += b[i];
		return a;
	}
}
