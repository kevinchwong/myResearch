package myCode.nashorn.multithread;
import java.util.Date;

public class ManagerClass{

	public String test(int i) {
		return ("Object " + i + ", Rule 1 : " + name + ": test() called!");
	}

	public static String test2(int i) {
		return ("Object " + i + ", Rule 2 : test2() called at " + (new Date()).getTime());
	}

	public String name = "ABC";
	public Double amt = 0.0;
}