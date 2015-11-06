package spiglet;

public class Interval implements Comparable<Interval> {
	public int begin, end;
	public boolean isS;
	public int tmpId;

	public Interval(int t, int b, int e) {
		begin = b;
		end = e;
		tmpId = t;
		isS = false;
	}

	public int compareTo(Interval arg0) {
		if (begin == arg0.begin)
			return end - arg0.end;
		else return begin - arg0.begin;
	}
}