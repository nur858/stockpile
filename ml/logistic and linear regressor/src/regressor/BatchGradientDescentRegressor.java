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

public class BatchGradientDescentRegressor extends Regressor {
	public static float convergenceRate = 0.00005f;	
	public BatchGradientDescentRegressor(){
		super();
		this.title = "Batch Linear";
	}
	public void learn(float learningRate){
		float [] omega = new float[Fold.featureCount];  
		ArrayList<Float> RMSE = new ArrayList<Float>();
		float []delta = new float[Fold.featureCount];
		float se = 0.0f;
		float mse = 0.0f;
		float rmse = 0.0f;
		int iteration = 0;
		try {
			String d = new BigDecimal(Float.toString(learningRate)).toPlainString();
			BufferedWriter bw = new BufferedWriter(new PrintWriter(new File("batch-gradient"+d+".txt")));						
			float improvement = 1.0f;
			do{			
				for(int i = 0;i<this.getData().size(); i++){
					TrainingEmail email = this.getData().get(i);
					float y = email.get(Fold.labelIndex);
					float h = omega[0];					
					for (int j = 1; j < Fold.featureCount ; j++ )
						h += omega[j] * email.get(j - 1);
					delta[0] += (y - h);
					for (int j = 1; j < Fold.featureCount ; j++ )
						delta[j] += (y - h)*email.get(j - 1 );
				}			
				
				for(int j = 0; j < Fold.featureCount ; j++ )
					omega[j] = omega[j] + learningRate * delta[j];
				se = 0.0f;
				for(int i =0;i<this.getData().size(); i++){
					TrainingEmail email = this.getData().get(i);
					float y = email.get(Fold.labelIndex);
					float h = omega[0];
					for (int j = 1; j < Fold.featureCount ; j++ )
						h += omega[j] * email.get(j - 1);				
					se += Math.pow((h - y), 2);		
				}		
				mse = se/(float)this.getData().size();
				bw.write("RMSE:" + rmse + " iteration:" + iteration+"\n");
				float prevRmse = rmse;
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
