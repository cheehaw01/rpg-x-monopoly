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
		if (v < min)
			v = max - (min - v) % (max - min);
		else
			v = min + (v - min) % (max - min);

		return v;
	}
}
