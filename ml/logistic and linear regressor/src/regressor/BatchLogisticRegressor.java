package regressor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;

import feature.Fold;
import feature.TrainingEmail;

public class BatchLogisticRegressor extends Regressor {
	public static float convergenceRate = 0.00001f;	
	public BatchLogisticRegressor(){
		super();
		this.title = "Batch Logistic";
	}
	public void learn(float learningRate){
		float [] omega = new float[Fold.featureCount];  
		float [] delta = new float[Fold.featureCount];
		ArrayList<Float> RMSE = new ArrayList<Float>();
		float se = 0.0f;
		float mse = 0.0f;//se/(float)this.getData().size();
		float rmse = 0.0f;//(float)Math.sqrt(mse);
		//RMSE.add(rmse);
		int iteration = 0;
		try {
			String d = new BigDecimal(Float.toString(learningRate)).toPlainString();
			BufferedWriter bw = new BufferedWriter(new PrintWriter(new File("batch-logistic"+d+".txt")));				
			float improvement = 1.0f;
			do{
				for(int i =0;i<this.getData().size(); i++){
					TrainingEmail email = this.getData().get(i);
					float y = email.get(Fold.labelIndex);
					float h = omega[0]; // for x0
					for (int j = 1; j < Fold.featureCount ; j++ )
						h += omega[j] * email.get(j - 1);
					h = (float) (1.0f/(1.0f + Math.exp(-h)));
					delta[0] += (y - h) * h * (1 - h);
					for (int j = 1; j < Fold.featureCount ; j++ )
						delta[j] += (y - h) * h * (1 - h) * email.get(j - 1);
				}			
				
				for(int j = 0; j < Fold.featureCount ; j++ )
					omega[j] = omega[j] + learningRate * delta[j];
				se = 0.0f;
				for(int i =0;i<this.getData().size(); i++){
					TrainingEmail email = this.getData().get(i);
					float y = email.get(Fold.labelIndex);
					float h = omega[0];
					for (int j = 1; j < Fold.featureCount  ; j++ )
						h += omega[j] * email.get(j - 1);	
					h = (float) (1.0f/(1.0f + Math.exp(-h)));
					se += Math.pow((h - y), 2);		
				}		
				mse = se/(float)this.getData().size();
				float prevRmse = rmse;//RMSE.get(interation);
				bw.write("RMSE:" + rmse + " iteration:" + iteration+"\n");
				rmse = (float)Math.sqrt(mse);			
				improvement = Math.abs(rmse - prevRmse); 
				RMSE.add(rmse);			
				iteration++;
			}while (improvement > convergenceRate);
			setOmega(omega);
			bw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
}
