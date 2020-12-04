package util;

public class Util {
	public static boolean intArrayContains(int[] arr, int v){
		for (int i : arr) {
			if(v == i)
				return true;
		}

		return false;
	}

	public static int loopClampInRange(int v, int min, int max) {
		if(v > max)
		{
			int q = v / max;
			v = v - q * max;
		}
		else if (v < min){
			int q = v / max;
			v = max + (v - (max * q));
		}

		return v;
	}
}
