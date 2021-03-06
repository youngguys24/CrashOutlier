package cn.edu.whu.cstar.algorithms;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import cn.edu.whu.cstar.algorithms.HilOut.CrashNode;
import cn.edu.whu.cstar.utils.ARFFReader;
import cn.edu.whu.cstar.utils.DistanceCalculator;
import weka.core.Instance;
import weka.core.Instances;

/***
 * <p><b>HilOut</b> is one of the most famous distance-based outlier detection algorithms. It's provided by XX et al.</p>
 * <p>It detect outliers by calculating weight of each data point, the weight is the sum of distances 
 * between one point to its k-nearest neighbors. Thus, those data points who has the higher weight are 
 * more likely to be the outliers.</p>
 * <li>1. calculating distances of any pair of points.</li>
 * <li>2. getting <b>K</b>-nearest neighbors of each points.</li>
 * <li>3. counting the weight of each point.</li>
 * <li>4. ranking the points in terms of weights, the top-<b>N</b> points are detected as outliers.</li>
 */
public class HilOut {
	
	private static Instances dataset;
	
	private static final int K = 10;
	
	private static final double P = 0.1;
	
	private static List<CrashNode> nodeset = new ArrayList<CrashNode>();
	
	private static List<double[]> lsDistance = new ArrayList<double[]>();;
	
	/**To initialize the dataset by <b>ARFFReader.read(String)</b>, then save all the instances in nodeset.*/
	public HilOut(String path){
		ARFFReader reader = new ARFFReader(path);
		dataset = reader.getDataset();
		for(int i=0; i<dataset.numInstances(); i++){
			CrashNode node = new CrashNode(dataset.get(i));
			nodeset.add(node);
		}
		
		calculateKNeighbors();
		
		rankingByWeights();
	}
	
	public Instance getIns(int index){
		return dataset.get(index);
	}
	
	public void calculateKNeighbors(){
		int size = nodeset.size();
		
		/** save distance between pair of nodes into lsDistance*/
		for(int i=0; i<size; i++){ // for each instance
			double[] lsEach = new double[size];
			for(int j=0; j<size; j++){ // calculate distance from other instance
				lsEach[j] = nodeset.get(i).getDistanceToOther(nodeset.get(j));
			}
			lsDistance.add(lsEach);
		}
		
		/** set K-nearesr neighbors to each instance*/
		for(int i=0; i<lsDistance.size(); i++){
			double kdis = DistanceCalculator.findKDistance(lsDistance.get(i), K);
			CrashNode currentInstance = nodeset.get(i);
			for(int j=0; j<lsDistance.size(); j++){
				if(currentInstance.getDistanceToOther(nodeset.get(j)) <= kdis && j != i){
					currentInstance.setNeighbor(nodeset.get(j));
				}	
			}	
			currentInstance.setWeight();
		}
				
	}
	
	/**To rank the instance by weight-values. */
	public void rankingByWeights(){
		nodeset.sort(new WeightComparator());
		int outlierNum = (int)(nodeset.size()*P); 
		
		for(int i=0; i<outlierNum; i++){
			nodeset.get(i).setPrelabel("outlier");
		}
		
	}
	
	public void showResult(){
		for(int i=0; i<nodeset.size(); i++){
			if(nodeset.get(i).isOutlier())
				System.out.println("Weight: " + nodeset.get(i).getWeight() + ", Label: " + nodeset.get(i).getLabel());
		}
	}
	
	/***
	 * <p>This class <b>CrashNode</b> is used to simulate the characteristic of each instance.</p>
	 * <p>We use the <b>KNeightbors</b> to save the k nearest neighbor list, use <b>label</b> to save 
	 * the original class label, use <b>lsAttr</b> to save the feature list, then use <b>prelabel</b> to
	 * save the outlierness of this instance, finally, <b>weight</b> to save the weight value.</p>
	 *
	 */
	class CrashNode{
		
		private List<CrashNode> KNeighbors = new ArrayList<CrashNode>(); // k-nearest neighbors
		
		private String label; // class label
		
		private String prelabel = "normal"; // outlier or normal
		
		private List<Double> lsAttr = new ArrayList<Double>(); // feature list
		
		private double weight = 0.0d; // weight value
		
		/**To initialize the instance with features and class label */
		CrashNode(Instance instance){
			int lenAttr = instance.numAttributes();
			label = instance.stringValue(lenAttr-1); // set true label
			for(int i=0; i<lenAttr-1; i++){ // set feature-values
				lsAttr.add(instance.value(i));
			}
		}
		
		/**<p>To get <b>feature-values</b> of instance. */
		public List<Double> getAttr(){
			return lsAttr;
		}
		
		/**To save predicted flag, i.e., '<b>normal</b>' or '<b>outlier</b>'.*/
		public void setPrelabel(String flag){
			this.prelabel = flag;
		}
		
		/**To get the original class label.*/
		public String getLabel(){
			return label;
		}
		
		/**To judge whether the instance is predicted as a outlier. */
		public boolean isOutlier(){
			if(prelabel == "outlier"){
				return true;
			}else{
				return false;
			}
		}
		
		/**To get distance to other <b>node</b>. Note the default distance is the Euclidean Distance.*/
		public double getDistanceToOther(CrashNode node){
			double distance = 0.0d;
			List<Double> attr1 = lsAttr;
			List<Double> attr2 = node.getAttr();
			
			distance = DistanceCalculator.distanceEculidean(attr1, attr2); //List<Double> ls1, List<Double> ls2
			
			return distance;
		}
		
		/**To add <b>node</b> into its K-nearest neighbors list. */
		public void setNeighbor(CrashNode node){
			if(KNeighbors.size() < K)
				KNeighbors.add(node);
		}
		
		/**To set weight( <b>sum distance to its k-nearest neighbors</b> ) of instance. */
		public void setWeight(){		
			for(CrashNode nodes: KNeighbors){
				weight += getDistanceToOther(nodes);
			}		
		}
		
		/**To get weight( <b>sum distance to its k-nearest neighbors</b> ) of instance.*/
		public double getWeight(){					
			return weight;
		}
		
		/**
		 * <p>To show the detailed information ( <b>feature & label</b> ) of instance</p>
		 * <pre> Instance Details: [Label]
		 * ------------------
		 * x1, x2, x3, x4, x5, ..., xn
		 * </pre>
		 */
		public void showNode(){
			System.out.println("Instance Details: [" + label + "]\n---------------------");
			for(double feature: lsAttr){
				System.out.print(feature + ", ");
			}
			System.out.println("");
		}
		
	}

}


/**
 * <p>Construct a comparator to sort the top-n instances which have the highest weight.</p>
 * @param <CrashNode>
 */
class WeightComparator implements Comparator<CrashNode>{

	public int compare(CrashNode o1, CrashNode o2) {
		if(o1.getWeight() > o2.getWeight()){
			return -1;
		}else if(o1.getWeight() < o2.getWeight()){
			return 1;
		}else{
			return 0;
		}

	}

}


