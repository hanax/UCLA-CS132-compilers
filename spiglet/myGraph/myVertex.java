package spiglet.myGraph;

import java.util.HashSet;


public class myVertex{
	public int nStt;
	
	public HashSet<myVertex> pre;
	public HashSet<myVertex> next;
	
	public HashSet<Integer> left;
	public HashSet<Integer> right;
	
	public HashSet<Integer> in;
	public HashSet<Integer> out;
	
	public myVertex(int _nStt){
		pre = new HashSet<myVertex>();
		next = new HashSet<myVertex>();
		left = new HashSet<Integer>();
		right = new HashSet<Integer>();
		in = new HashSet<Integer>();
		out = new HashSet<Integer>();
		nStt = _nStt;
	}
}
