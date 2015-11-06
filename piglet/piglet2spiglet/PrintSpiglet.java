package piglet.piglet2spiglet;

public class PrintSpiglet {	
	private static int nTab = 0;
	private static boolean enter = false;
	public static int nTmp;

	public static void p(String s) {
		if(enter){
			System.out.println();
			//System.out.println(nTab);
			for(int i = 0; i < nTab; i ++) System.out.print("  ");
		} enter = false;
		System.out.printf(s);
	}
	public static void pln(String s) {
		p(s);
		enter = true;
	}
	public static int getT(){
		return nTab;
	}
	public static void setT(int n){
		nTab = n;
	}
	public static void addT(){
		nTab ++;
	}
	public static void decT(){
		nTab --;
	}

}
