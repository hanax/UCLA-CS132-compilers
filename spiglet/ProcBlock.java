package spiglet;

import java.util.HashMap;
import java.util.HashSet;

import spiglet.myGraph.myGraph;

public class ProcBlock{
	public String procName;
	
	public int nArg;
	public int maxParam;
	public int maxStack;
	
	public HashMap<String, String> regT = new HashMap<String, String>();
	public HashMap<String, String> regS = new HashMap<String, String>();
	public HashMap<String, String> regSpi = new HashMap<String, String>();
	public HashMap<Integer, Interval> tmpMap = new HashMap<Integer, Interval>();
	
	public myGraph graph = new myGraph();
	
	public ProcBlock(String p, int a) {
		procName = p;
		nArg = a;
		maxParam = 0;
		maxStack = 0;
	}
}