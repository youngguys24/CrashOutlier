package cn.edu.whu.cstar.utils;

import java.util.List;

public class DistanceCalculator {
	
	/***
	 * <p>To get the Euclidean Distance between 2 double lists. Note that,</p>
	 * <p>1. Two lists must have the same size.</p>
	 * <p>2. length = Math.sqart((x1 - y1)^2 + (x2 - y2)^2 + ... + (xn - yn)^2)</p>
	 * @param ls1 list 1
	 * @param ls2 list 2
	 * @return length
	 */
	public static double distanceEculidean(List<Double> ls1, List<Double> ls2){
		double length = 0.0d;
		
		if(ls1.size() != ls2.size()){ // size not equal
			return -1;
		}
		
		double delta = 0; 
		
		for(int i=0; i<ls1.size(); i++){
			delta += (ls1.get(i) - ls2.get(i))*(ls1.get(i) - ls2.get(i));
		}
		
		length = Math.sqrt(delta);
		
		return length;
	}
	
	/**
	 * <p>To get the <b>k</b>-nearest distance in <b>dis</b> array.</p>
	 * @param dis double array
	 * @param k k-th nearest
	 * @return k-nearest distance
	 */
	public static double findKDistance(double[] dis, int k){
		double kdis = 0.0d;
		
		if(k > dis.length){ // k is out of bounds
			return -1;
		}
		
		/** sorting dis by simple bubble sorting algorithm in ascending order*/
		for(int i=0; i<dis.length-1; i++){
			for(int j=0; j<dis.length-i-1; j++){
				if(dis[j] > dis[j+1]){ 
					double temp = dis[j+1];
					dis[j+1] = dis[j];
					dis[j] = temp;
				}
			}
		}

		/** assign k-distance*/
		kdis = dis[k-1];
		
		return kdis;
	}

}
