package feature;

import java.util.ArrayList;

public class TrainingEmail extends ArrayList<Float> {
	private static final long serialVersionUID = 4284281349648202182L;
	public TrainingEmail(int size){
		super(size);
	}
	

	public boolean isSpam(){
		//add assertion		
		
		return this.get(57) > 0.0f; 
	}
}
