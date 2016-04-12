import java.util.*;
import java.lang.Math.*;

public class NeuralMap{
	public HashMap<String, Neuron>	sensors				 	= new HashMap<String, Neuron>();
	public HashSet<Neuron>			output					= new HashSet<Neuron>();
	public HashSet<Neuron>			active					= new HashSet<Neuron>();
	public Queue<Neuron>			toUpdate				= new LinkedList<Neuron>();
	public ArrayList<String>		input					= new ArrayList<String>();
	public HashMap<Integer, Neuron>	all						= new HashMap<Integer,Neuron>();
	public Feedback 				goal 					= new Feedback();
	
	//Position data for displacement test
	int curX = 0;
	int curY = 0;
	int preX = 0;
	int preY = 0;
	
	int foodX;
	int foodY;
	
	int distFromFood;
	
	public NeuralMap(Feedback g){
		goal = g;
	}
	
	public void nextStep(){
		Neuron n;
		for(int i = 0; i<toUpdate.size(); i++){
			n = toUpdate.poll();
			HashSet<Neuron> toRemove = new HashSet<Neuron>();
			if(n.isActionNeuron()){
				n.setFiring(true);
				n.printAction();
				if(!goal.respectsRule(input, n.getInfo().substring(1))){
					for(Neuron p: n.parent){
						if(p.getFiring()){
							if(p.getEdgesTo().get(n) > 0){
								p.getEdgesTo().put(n,p.getEdgesTo().get(n) - 1);
							}
						}
					}
					n.setExcitationLevel(0);
					n.setFiring(false);
					active.remove(n);
					for(Neuron out: output){
						if(goal.respectsRule(input, out.getInfo().substring(1))){
							for(Neuron from: out.getEdgesFrom().keySet()){
								if(from.getFiring() && from.getEdgesTo().get(out) == 0){
									from.getEdgesTo().put(out, 1);
								}
							}
						}
					}
				}
			} else{
				n.fires();
				for(Neuron m: n.getEdgesTo().keySet()){
					if( m.getExcitationLevel() >= m.getThreshold() && !active.contains(m)){
						active.add(m);
						toUpdate.add(m);
					}
				}
			}
		}
	}
	
	public void receiveInput(ArrayList<String> input){
		Neuron n;
		for(String in : input){
			if(sensors.containsKey(in)){
				n = sensors.get(in);
				n.setExcitationLevel(n.getThreshold());
				n.setFiring(true);
				active.add(n);
				toUpdate.add(n);
			}
		}
	}
	
	public void stopsFiring(Neuron n){
		if(n.getFiring()){
			active.remove(n);
			n.firing = false;
			for(Neuron m: n.edges_to.keySet()){
				int newVal = m.getExcitationLevel() - n.getEdgesTo().get(m);
				if(newVal>=0){
					m.setExcitationLevel(m.getExcitationLevel() - n.getEdgesTo().get(m));
				} else{
					m.setExcitationLevel(0);
				}
				active.remove(m);
				m.parent.remove(n);
				if(m.getExcitationLevel() < m.getThreshold() && m.getFiring()){
					stopsFiring(m);
				}
			}
		}
	}
	
	public void printNeuralNetwork(){
		for(Neuron n: all.values()){
			System.out.println("Number: " + n.getName() + " Info: " + n.getInfo() + " " + n.getFiring() 
								+ " Excitation " + n.getExcitationLevel());
			System.out.println("Edges to: ");
			for(Neuron m: n.getEdgesTo().keySet()){
				System.out.println(m.getName() + ":" + m.getInfo() + " " + n.getEdgesTo().get(m));
			}
			System.out.println();
		}
		System.out.print("Active Neurons: ");
		for(Neuron n: active){
			System.out.print(n.getName() + " ");
		}
		System.out.println();
	}
	
	public int addSensor(String info){
		Neuron temp = new NeuronBuilder().setInfo(info).build();
		sensors.put(temp.getInfo(), temp);
		all.put(temp.getName(), temp);
		return temp.getName();
	}
	
	public int addInterneuron(Neuron[] ar, int threshold){
		Neuron temp = new NeuronBuilder().setThreshold(threshold).build();
		all.put(temp.getName(), temp);
		for(int i = 0; i<ar.length; i++){
			connect(ar[i], temp);
		}
		return temp.getName();
	}
	
	public int addOutput(Neuron[] ar, String action, int threshold){
		Neuron temp = new NeuronBuilder().setThreshold(threshold).setInfo("#" + action).build();
		all.put(temp.getName(), temp);
		output.add(temp);
		if(ar != null){
			for(int i = 0; i<ar.length; i++){
				connect(ar[i], temp);
			}
		}
		return temp.getName();
	}
	
	public void connect(Neuron n1, Neuron n2){
		n1.getEdgesTo().put(n2,1);
		n2.getEdgesFrom().put(n1,1);
	}
	
	public void connect(Neuron n1, Neuron n2, int strength){
		n1.getEdgesTo().put(n2,strength);
		n2.getEdgesFrom().put(n1,strength);
	}
	
	/*
	// Helper methods for displacement test
	public void displace(String direction){
		switch(direction){
			case "up":
				preY = curY;
				curY = curY + 1;
				break;
			case "down":
				preY = curY;
				curY = curY - 1;
				break;
			case "left":
				preX = curX;
				curX = curX - 1;
				break;
			case "right":
				preX = curX;
				curX = curX + 1;
				break;
		}
	}
	
	//Helper function evaluating if the robot got closer to the food
	public boolean isCloser(){
		curDist = Math.sqrt(Math.pow(curX,2) + Math.pow(curY,2));
		if(curDist <= distFromFood){
			distFromFood = curDist;
			return true;
		} else{
			distFromFood = curDist;
			return false;
		}
	}*/
	
	public static void main(String[] args){
		Feedback fb = new Feedback();
		Feedback fb2 = new Feedback(){
			@Override
			public boolean respectsRule(ArrayList<String> input, String output){
				if(output.equals("walk")){
					return true;
				}
				return false;
			}
		};
		
		NeuralMap map = new NeuralMap(fb);
		Scanner sc = new Scanner(System.in);
		String stimuli;
		
		//create the X neurons
		ArrayList<Integer> Xcoord = new ArrayList<Integer>();
		for(int i = -2; i<3; i++){
			Xcoord.add(map.addSensor(i + ""));
		}
		
		//create the Y neurons
		ArrayList<Integer> Ycoord = new ArrayList<Integer>();
		for(int i = -2; i<3; i++){
			Ycoord.add(map.addSensor(i + ""));
		}
		
		Neuron danger = new NeuronBuilder().setInfo("danger").build();
		map.all.put(danger.getName(), danger);
		map.sensors.put("danger", danger);
		Neuron food = new NeuronBuilder().setInfo("food").build();
		map.all.put(food.getName(), food);
		map.sensors.put("food", food);
		
		//create the interneurons for the food location
		ArrayList<Integer> foodLocation = new ArrayList<Integer>();
		Neuron[] a = new Neuron[3];
		a[2] = food;
		Neuron x;
		Neuron y;
		for(int i : Xcoord){
			for(int j : Ycoord){
				x = map.all.get(i);
				y = map.all.get(j);
				a[0] = x;
				a[1] = y;
				foodLocation.add(map.addInterneuron(a, 2));
			}
		}
		
		//create the output neurons
		ArrayList<Integer> outputNo = new ArrayList<Integer>();
		outputNo.add(map.addOutput(null, "left", 1));
		outputNo.add(map.addOutput(null, "right", 1));
		outputNo.add(map.addOutput(null, "up", 1));
		outputNo.add(map.addOutput(null, "down", 1));
		
		//connect all interneurons to the outputs
		for(int i: outputNo){
			for(int j: foodLocation){
				map.connect(map.all.get(j), map.all.get(i));
			}
		}

		while(true){
			System.out.println("Where is the food? (X, Y, food)");
			stimuli = sc.nextLine();
			String[] stim = stimuli.split(" ");
			map.input = new ArrayList<String>(Arrays.asList(stim));
			if(map.input.contains("e")){
				break;
			}
			if(map.input.contains("hungry")){
				map.goal = fb2;
			}
			if(map.input.contains("print")){
				map.printNeuralNetwork();
			}
			map.receiveInput(map.input);

			while(map.toUpdate.size() != 0){
				map.nextStep();
			}
			
			//map.printNeuralNetwork();
			
			for(Neuron s: map.sensors.values()){
				s.setExcitationLevel(0);
				map.stopsFiring(s);
			}
		}
	}
}