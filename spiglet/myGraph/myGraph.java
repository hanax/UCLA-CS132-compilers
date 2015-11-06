package spiglet.myGraph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

public class myGraph{
	public Vector<myVertex> vNode;
	public HashMap<Integer, myVertex> mNode;
	public HashSet<Integer> sCallPos;
	public int nStt;
	
	public myGraph(){
		vNode = new Vector<myVertex>();
		mNode = new HashMap<Integer, myVertex>();
		sCallPos = new HashSet<Integer>();
		nStt = 0;
	}
	
	public void addVertex(int _nStt){
		myVertex v = new myVertex(_nStt);
		this.vNode.add(v);
		this.mNode.put(_nStt, v);
	}
	
	public void addEdge(int _src, int _dst){
		myVertex v_src = mNode.get(_src);
		myVertex v_dst = mNode.get(_dst);
		v_src.next.add(v_dst);
		v_dst.pre.add(v_src);
	}
	
	public myVertex getVertex(int _nStt){
		return mNode.get(_nStt);
	}
}
