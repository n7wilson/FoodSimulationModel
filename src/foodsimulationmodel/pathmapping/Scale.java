package foodsimulationmodel.pathmapping;

import java.util.Iterator;

import repast.simphony.space.graph.RepastEdge;

public class Scale {

	// Finds the max/min of x/y coordinates in an Iterable list of NetworkEdges (roadNetwork.getEdges())
	public static double findCritPts(Iterable<RepastEdge<Junction>> lst, boolean max, boolean x){
		Iterator<RepastEdge<Junction>> itr = lst.iterator();
		if(x){
			double maxCor = ((NetworkEdge<Junction>) itr.next()).getRoad().getCoords().x;
			double minCor = maxCor;
			double curCor;
			while (itr.hasNext()){
				curCor = ((NetworkEdge<Junction>) itr.next()).getRoad().getCoords().x;
				if (curCor >= maxCor){
					maxCor = curCor;
				}
				if (curCor <= minCor){
					minCor = curCor;
				}
			}
			if (max) { return maxCor; }
			else { return minCor; }
		}
		else {
			double maxCor = ((NetworkEdge<Junction>) itr.next()).getRoad().getCoords().y;
			double minCor = maxCor;
			double curCor;
			while (itr.hasNext()){
				curCor = ((NetworkEdge<Junction>) itr.next()).getRoad().getCoords().y;
				if (curCor >= maxCor){
					maxCor = curCor;
				}
				if (curCor <= minCor){
					minCor = curCor;
				}
			}
			if (max) { return maxCor; }
			else { return minCor; }
		}
	}
	
	// Scales a number n based on old range [min, max] and new range [a, b]
	public static double corScale(double n, double min, double max, float a, float b){
	
		double sFactor;
		if (max == min){
			sFactor = (b - a);
		}
		else {
			sFactor = (b - a)/(max - min);
		}
		return sFactor*(n - min) + a;
	}
}
