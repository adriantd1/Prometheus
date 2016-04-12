import java.util.*;

public class Feedback{
	private ArrayList<String> input;
	
	public ArrayList<String> getInput(){
		return input;
	}
	
	public void setInput(ArrayList<String> a){
		input = a;
	}
	
	public boolean respectsRule(ArrayList<String> input, String output){
		String[] ar = new String[3];
		ar = input.toArray(ar);
		int x = Integer.parseInt(ar[0]);
		int y = Integer.parseInt(ar[1]);
		if(ar[2].equals("food")){
			if(output.equals("right")){
				if(x>0){
					return true;
				}
			} else if(output.equals("left")){
				if(x<0){
					return true;
				}
			} else if(output.equals("up")){
				if(y>0){
					return true;
				}
			} else if(output.equals("down")){
				if(y<0){
					return true;
				}
			}
		}
		return false;		
	}
	
}