import java.util.*;
import java.lang.*;

public class Neuron{
	protected int 							name;
	protected static int 					population;
	protected HashMap<Neuron, Integer> 		edges_from = new HashMap<Neuron, Integer>();
	protected HashMap<Neuron, Integer> 		edges_to = new HashMap<Neuron, Integer>();
	protected int 							excitation_level;
	protected int 							threshold;
	protected boolean 						firing;
	private String 							info;
	public HashSet<Neuron>					parent = new HashSet<Neuron>();
	
	public Neuron(){
		name = 								population;
		excitation_level = 					0;
		threshold = 						1;
		firing =							false;
		population++;
		info = 								"default";
	}
	
	public Neuron
	(HashMap<Neuron, Integer> edges_to, int threshold, String info){
		this.edges_to = edges_to;
		this.threshold = threshold;
		this.info = info;
		this.name = population;
		population++;
	}
	
	public int getName(){									return name;}
	public int getPopulation(){								return population;}
	public HashMap<Neuron, Integer> getEdgesFrom(){			return edges_from;}
	public HashMap<Neuron, Integer> getEdgesTo(){			return edges_to;}
	public int getExcitationLevel() {						return excitation_level;}
	public int getThreshold(){								return threshold;}
	public boolean getFiring(){								return firing;}
	public String getInfo(){								return info;}
	
	public void setEdgesFrom(HashMap<Neuron, Integer> a){	edges_from = a;}
	public void setEdgesTo(HashMap<Neuron, Integer> a){		edges_to = a;}
	public void setExcitationLevel(int a) {					excitation_level = a;}
	public void setThreshold(int a){						threshold = a;}
	public void setFiring(boolean a){						firing = a;}
	public void setInfo(String a){							info = a;}

	public void addConnection(Neuron n, int weight){
		edges_to.put(n, weight);
		n.getEdgesFrom().put(n, weight);
	}
	
	public void removeConnection(Neuron n){
		edges_to.remove(name);
		n.getEdgesFrom().remove(this);
	}
	
	public void updateConnection(Neuron n, int weight){
		addConnection(n, weight);
	}
	
	public void printAction(){
		System.out.println(info.substring(1));
	}
	
	public boolean isActionNeuron(){
		if(info.charAt(0) == '#'){
			return true;
		} else{
			return false;
		}
	}
	
	public void fires(){
		//System.out.println(name);
		firing = true;
		// propagates AP
		for(Neuron m: edges_to.keySet()){
			if(!m.getFiring()){
				m.setExcitationLevel(m.getExcitationLevel() + getEdgesTo().get(m));
				m.parent.add(this);
			}
		}
	}
}












