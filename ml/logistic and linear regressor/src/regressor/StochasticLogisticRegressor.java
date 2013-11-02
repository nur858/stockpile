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

public class StochasticLogisticRegressor extends Regressor {
	public static float convergenceRate = 0.00001f;
	public StochasticLogisticRegressor(){
		super();
		this.title = "Stochastic Logistic";
	}
	public void learn(float learningRate){
		float [] omega = new float[Fold.featureCount];  
		ArrayList<Float> RMSE = new ArrayList<Float>();
		float se = 0.0f;
		float mse = 0.0f;//se/(float)this.getData().size();
		float rmse = 0.0f;//(float)Math.sqrt(mse);
		try {
			String d = new BigDecimal(Float.toString(learningRate)).toPlainString();
			BufferedWriter bw = new BufferedWriter(new PrintWriter(new File("stochastic-logistic"+d+".txt")));							
		//RMSE.add(rmse);
			float prevRmse = 0.0f;
			int iteration = 0;
			float improvement = 1.0f;
			do{
				for(int i =0;i<this.getData().size(); i++){
					TrainingEmail email = this.getData().get(i);
					float y = email.get(Fold.labelIndex);
					float h = omega[0];
					for (int j = 1; j < Fold.featureCount ; j++ )
						h += omega[j] * email.get(j - 1);
					h = (float) (1.0f/(1.0f + Math.exp(-h)));
					omega[0] = omega[0] + learningRate * h * (1 -h) * (y - h);
					for(int j = 1; j < Fold.featureCount ; j++ )
						omega[j] = omega[j] + learningRate * h * (1 -h) * (y - h) * email.get(j - 1);
				}
				se = 0.0f;
				for(int i =0;i<this.getData().size(); i++){
					TrainingEmail email = this.getData().get(i);
					float y = email.get(Fold.labelIndex);
					float h = omega[0];
					for (int j = 1; j < Fold.featureCount ; j++ )
						h += omega[j] * email.get(j - 1);
					h = (float) (1.0f/(1.0f + Math.exp(-h)));
					se += Math.pow((h - y), 2);		
				}			
				mse = se/(float)this.getData().size();
				prevRmse = rmse;
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
