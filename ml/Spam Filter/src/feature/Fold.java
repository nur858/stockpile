package feature;
import java.util.ArrayList;
import java.util.HashMap;

import estimator.BernouliEstimator;
import estimator.HistogramEstimator;
import estimator.GaussianEstimator;

public class Fold implements Chunk { 
	private HashMap<Integer, TrainingEmail> data;
	private int spamCount;
	public Fold(){
		spamCount = 0;
		data = new HashMap<Integer, TrainingEmail>();
	}
	public void add(int position, float[]list, boolean isTestEmail){
		TrainingEmail f = isTestEmail? new TestingEmail(58) : new TrainingEmail(58);
	    for (int i = 0; i<list.length; i++){ 
	        f.add(list[i]);
	    }
	    if(f.isSpam())spamCount++;
		data.put(new Integer(position), f);
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
	public BernouliEstimator getBernouliEstimator(){
		int size = data.size();
		int []lessThanEqualHam = new int[57];
		int []greaterThanSpam  = new int[57];
		for(int k=0;k<57;k++){
			for(int j = 0;j<size;j++){
				float mean = Fold.mean[k];
				float feature = data.get(j).get(k);
				if(data.get(j).isSpam()){ // spam
					if(feature>mean)greaterThanSpam[k]++;
				}
				else{
					if(feature<=mean)lessThanEqualHam[k]++;
				}
			}
		}		
		BernouliEstimator be = new BernouliEstimator();
		be.setLikelySpamCount(greaterThanSpam);
		be.setUnlikelyHamCount(lessThanEqualHam);
		be.setEmailCount(size);
		be.setSpamCount(spamCount);
		return be;
	}
	public GaussianEstimator getGaussianEstimator(){
		int size = data.size();
		int hamCount  = size - spamCount; 
		float []spamMean = new float[57];
		float []hamMean  = new float[57];
		float []spamVar  = new float[57];
		float []hamVar   = new float[57];
		for(int k=0;k<57;k++){
			float spamSum = 0.0f;
			float hamSum  = 0.0f;
			for(int j = 0;j<size;j++){ 
				float feature = data.get(j).get(k);
				if(data.get(j).isSpam()){ // spam					
					spamSum += feature;
				}									
				else{ // ham
					hamSum  += feature;
				}
			}
			spamMean[k] = (spamSum)/(float)(spamCount );
			hamMean[k]  = (hamSum)/(float)(hamCount);				
			for(int j = 0;j<size;j++){				
				float Xj = data.get(j).get(k);
				if(data.get(j).isSpam()){ // spam
					spamVar[k] += (float)Math.pow((Xj - spamMean[k]), 2.0);
				}else{ // ham						
					hamVar[k] += (float)Math.pow((Xj - hamMean[k]), 2.0);
				}					
			}				
			spamVar[k]  = spamVar[k] /(float)(spamCount - 1);
			spamVar[k]  = (spamVar[k] + 1)/(size + 2);  // laplace smoothing
			hamVar[k]   = hamVar[k] /(float)(hamCount - 1);
			hamVar[k]  = (hamVar[k] + 1)/(size + 2); // laplace smoothing
		}		
		
		GaussianEstimator ge = new GaussianEstimator();
		ge.setEmailCount(size);
		ge.setSpamCount(spamCount);
		ge.setHamMean(hamMean);
		ge.setSpamMean(spamMean);
		ge.setSpamVar(spamVar);
		ge.setHamVar(hamVar);
		return ge;
	}
	public HistogramEstimator getHistogramEstimator(){
		int size = data.size();
		int hamCount = size - spamCount;
		float []spamClassMean = new float[57];
		float []hamClassMean  = new float[57];
		HashMap<Integer,ArrayList<Float>> spamBins = new HashMap<Integer,ArrayList<Float>>();
		HashMap<Integer,ArrayList<Float>> hamBins = new HashMap<Integer,ArrayList<Float>>();
		for (int i = 0; i<57; i++){
			float spamSum = 0.0f;
			float hamSum  = 0.0f;
			for ( int j = 0; j < size; j++){
				TrainingEmail list = data.get(j);
				if(list.isSpam())
					spamSum += list.get(i);
				else
					hamSum  += list.get(i);
			}
			spamClassMean[i] = (spamSum + 1)/(spamCount + 57);
			hamClassMean[i]  = (hamSum + 1)/(hamCount + 57);			
		}
		
		for ( int i = 0; i<57; i++ ){		
			int spamBin1 = 0;int spamBin2 = 0;
			int spamBin3 = 0;int spamBin4 = 0;
			int hamBin1 = 0;int hamBin2 = 0;
			int hamBin3 = 0;int hamBin4 = 0;			
			for (int j = 0; j<size; j++){
				TrainingEmail email = data.get(j);
				float feature = email.get(i);				 
				if( feature <= Math.min(spamClassMean[i], hamClassMean[i])){
					if(email.isSpam())
						spamBin1++;
					else
						hamBin1++;
				}
				else if (( Math.min(spamClassMean[i], hamClassMean[i]) < feature ) && (feature <= Fold.mean[i]) ){
					if(email.isSpam())
						spamBin2++;
					else
						hamBin2++;				
				}
				else if ( (Fold.mean[i] < feature) && (feature <= Math.max(spamClassMean[i], hamClassMean[i])) ){
					if(email.isSpam())
						spamBin3++;
					else
						hamBin3++;				
				}
				else if( Math.max(spamClassMean[i], hamClassMean[i]) < feature ){
					if(email.isSpam())
						spamBin4++;
					else
						hamBin4++;				
				}
			}
			ArrayList<Float> list = null;
			list = new ArrayList<Float>(4);
			list.add((spamBin1+1)/(float)(spamCount + 2));
			list.add((spamBin2+1)/(float)(spamCount + 2));
			list.add((spamBin3+1)/(float)(spamCount + 2));
			list.add((spamBin4+1)/(float)(spamCount + 2));
			spamBins.put(i, list);
			list = new ArrayList<Float>(4);
			list.add((hamBin1+1)/(float)(hamCount + 2));
			list.add((hamBin2+1)/(float)(hamCount + 2));
			list.add((hamBin3+1)/(float)(hamCount + 2));
			list.add((hamBin4+1)/(float)(hamCount + 2));			
			hamBins.put(i, list);
		}
		HistogramEstimator be = new HistogramEstimator();
		be.setHamBins(hamBins);
		be.setSpamBins(spamBins);
		be.setSpamCount(spamCount);
		be.setEmailCount(size);
		be.setHamClassMean(hamClassMean);
		be.setSpamClassMean(spamClassMean);
		return be;
	}
}
