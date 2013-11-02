package feature;
import java.util.HashMap;

import regressor.BatchGradientDescentRegressor;
import regressor.BatchLogisticRegressor;
import regressor.StochasticLogisticRegressor;
import regressor.StochasticGradientDescentRegressor;


public class Fold implements Chunk { 
	public static float [] mean = null;
	public static float [] deviation = null;
	private HashMap<Integer, TrainingEmail> data;
	private int spamCount;
	public Fold(){
		spamCount = 0;
		data = new HashMap<Integer, TrainingEmail>();
	}
	public void add(int position, float[]list, boolean isTestEmail){
		TrainingEmail f = isTestEmail? new TestingEmail(Chunk.featureCount) : new TrainingEmail(Chunk.featureCount);
	    for (int i = 0; i<list.length; i++){ 
	        f.add(list[i]);
	    }
	    if(f.isSpam())spamCount++;
		data.put(new Integer(position), f);
	}
	public float [] getPartialFeatureSum(){
		float []sum = new float[Chunk.featureCount];
		for(int i = 0 ; i < data.size(); i++)
			for(int j =0; j < Chunk.featureCount; j++)
				sum[j] += data.get(i).get(j);
		return sum;
	}
	public float [] getPartialVariance(){
		assert Fold.mean.length<=0: "mean must be calculated before this." ;
		float []sum = new float[Chunk.featureCount];
		for(int i = 0 ; i < data.size(); i++)
			for(int j =0; j < Chunk.featureCount; j++)
				sum[j] += (float)Math.pow((data.get(i).get(j) - Fold.mean[j]), 2);
		return sum;		
	}
	public void normalize(){
		assert Fold.mean.length <=0 : "mean must be calculated before this." ;
		assert Fold.deviation.length <=0 : "deviation must be calculated before this." ;		
		for(int i = 0 ; i < data.size(); i++){
			for(int j =0; j < Chunk.featureCount; j++){
				float feature = data.get(i).get(j);
				feature = (feature - Fold.mean[j])/Fold.deviation[j];
				data.get(i).set(j, feature);
			}				
		}
	}
	public StochasticGradientDescentRegressor getStochasticGradientDescentRegressor(){
		StochasticGradientDescentRegressor sgd = new StochasticGradientDescentRegressor();
		sgd.setData(data);
		return sgd;
	}
	public BatchGradientDescentRegressor getBatchGradientDescentRegressor (){
		BatchGradientDescentRegressor bgd = new BatchGradientDescentRegressor();
		bgd.setData(data);
		return bgd;
	}
	
	public StochasticLogisticRegressor getStochasticLogisticRegressor(){
		StochasticLogisticRegressor lr = new StochasticLogisticRegressor();
		lr.setData(data);
		return lr;
	}
	public BatchLogisticRegressor getBatchLogisticRegressor(){
		BatchLogisticRegressor lr = new BatchLogisticRegressor();
		lr.setData(data);
		return lr;
	}	
	public int size(){
		return data.size();
	}
	@Override
	public String toString() {
		return "Fold [data=" + data + "]";
	}
	public void print(){
		System.out.println("size:" + data.size());
		for(int i = 0; i<data.size();i++){
			System.out.println(data.get(i));
		}
	}
	public int getSpamCount(){
		return spamCount;
	}
	public TrainingEmail getEmail(int email){
		return data.get(email);
	}
	public float getFeature(int email, int feature){
		// add assert
		return data.get(email).get(feature);
	}
	
}
