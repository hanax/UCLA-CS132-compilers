package spiglet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import spiglet.myGraph.myGraph;
import spiglet.myGraph.myVertex;

public class AssignTMP {
	private HashMap<String, ProcBlock> ProcMap;
	private myGraph CurGraph;
	private ProcBlock CurProc;
	
	public AssignTMP(HashMap<String, ProcBlock> _ProcMap){
		ProcMap = _ProcMap;
	}
	
	private void GetInOut(){
	    boolean flg = true;
	    while(flg){
	    	flg = false;
		    for(int i = CurGraph.vNode.size() - 1; i >= 0; i --){
		        myVertex v = CurGraph.mNode.get(i);
		        for(myVertex t : v.next)
			        v.out.addAll(t.in);
		        
		        HashSet<Integer> new_in = new HashSet<Integer>();
		        new_in.addAll(v.out);
		        new_in.removeAll(v.left);
		        new_in.addAll(v.right);
		        if(!v.in.equals(new_in)){
		        	v.in = new_in;
		        	flg = true;
		        }
			}
	    }
	}
	
	private void GetInterval(){
	    for(int i = 0; i < CurGraph.mNode.size(); i ++){	    	
	    	myVertex v = CurGraph.mNode.get(i);	      
	    	for(Integer ii : v.in)
	    		CurProc.tmpMap.get(ii).end = i;
	    	for(Integer ii : v.out)
	    		CurProc.tmpMap.get(ii).end = i;
	    }
	    
	    for(Interval itv: CurProc.tmpMap.values()){
	    	for(Integer ii: CurGraph.sCallPos){
	    		if(itv.begin < ii && itv.end > ii)
	    			itv.isS = true;
	    	}
	    }
	}
	
	public void assign(){		
		for(ProcBlock p : ProcMap.values()){
			CurProc = p;
			CurGraph = p.graph;
			GetInOut();
			GetInterval();
			
			ArrayList<Interval> itvs = new ArrayList<Interval>();
			for(Interval itv : p.tmpMap.values())
				itvs.add(itv);
			Collections.sort(itvs);

			Interval[] setT = new Interval[8];
			Interval[] setS = new Interval[8];
			for(Interval itv: itvs){
				int longestT = -1, longestS = -1;
				int emptyT = -1, emptyS = -1;
				for(int m = 7; m >= 0; m --){
					if(setT[m] != null){
						if(setT[m].end <= itv.begin){
							p.regT.put("TEMP " + setT[m].tmpId, "t" + m);
							setT[m] = null;
							emptyT = m;
						}else{
							if(longestT == -1 || setT[m].end > setT[longestT].end)
								longestT = m;
						}
					}else{
						emptyT = m;
					}
				}
				for(int m = 7; m >= 0; m --){
					if(setS[m] != null){
						if(setS[m].end <= itv.begin){
							p.regS.put("TEMP " + setS[m].tmpId, "s" + m);
							setS[m] = null;
							emptyS = m;
						}else{
							if(longestS == -1 || setS[m].end > setS[longestS].end)
								longestS = m;
						}
					}else{
						emptyS = m;
					}
				}
				if(itv != null && !itv.isS){
					if(emptyT != -1){
						setT[emptyT] = itv;
						itv = null;
					}else{
						if(itv.end < setT[longestT].end){
							// swap itv & setT[longestT]
							Interval tmp = setT[longestT];
							setT[longestT] = itv;
							itv = tmp;
						}
					}
				}
				if(itv != null){
					if(emptyS != -1){
						setS[emptyS] = itv;
						itv = null;
					}else{
						if(itv.end < setS[longestS].end){
							Interval tmp = setS[longestS];
							setS[longestS] = itv;
							itv = tmp;
						}
					}
				}
				if(itv != null)
					p.regSpi.put("TEMP " + itv.tmpId, "");
			}
			for(int l = 0; l < 8; l ++){
				if(setT[l] != null)
					p.regT.put("TEMP " + setT[l].tmpId, "t" + l);
				if(setS[l] != null)
					p.regS.put("TEMP " + setS[l].tmpId, "s" + l);
			}
			int spilledIdx = (p.nArg > 4 ? p.nArg - 4 : 0) + p.regS.size();
			for(String s : p.regSpi.keySet()){
				p.regSpi.put(s, "SPILLEDARG " + spilledIdx);
				spilledIdx ++;
			}
			p.maxStack = spilledIdx;
		}
	}
}
