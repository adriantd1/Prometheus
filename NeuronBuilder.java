import java.util.*;

public class NeuronBuilder{
	private int 						name = 0;
	private static int 					population;
	private HashMap<Neuron, Integer> 	edges_from = new HashMap<Neuron, Integer>();
	private HashMap<Neuron, Integer> 	edges_to = new HashMap<Neuron, Integer>();
	private double 						excitation_level = 0;
	private int							threshold = 1;
	private boolean 					firing = false;
	private String						info = "default";
	
	public NeuronBuilder setEdgesFrom(HashMap<Neuron, Integer> a){
		edges_from = a;
		return this;
	}
	
	public NeuronBuilder setEdgesTo(HashMap<Neuron, Integer> a){
		edges_to = a;
		return this;
	}
	
	public NeuronBuilder setThreshold(int a){
		threshold = a;
		return this;
	}
	
	public NeuronBuilder setInfo(String a){
		info = a;
		return this;
	}
	
	public Neuron build(){
		return new Neuron(edges_to, threshold, info);
	}
	
}