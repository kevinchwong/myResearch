package myCode.nashorn.javareflection;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 *
 * @project ProvAppCLService
 * @author kwong
 *
 */

public class JavaReflectionTest {

	static public class A {
		public int axx = 0;
		public String djs = "Hello";
		static public int bxx = 12;
		static public long lxx = 12L;

		public int myOrange() {
			return 123;
		}

		static public String myApple() {
			return "red";
		}
	};

	public static void view(Object x) {
		Field[] fi = x.getClass().getFields();
		System.out.println("Class : [" + x.getClass().getCanonicalName()+"]");
		for (Field f : fi) {
			System.out.print(f.getName() + "=");
			try {
				if (Modifier.isStatic(f.getModifiers())) {
					System.out.print(" ["+f.get(null).getClass().getCanonicalName()+"] ");
					System.out.println(f.get(null).toString());
				} else {
					System.out.print(" ["+f.get(x).getClass().getCanonicalName()+"] ");
					System.out.println(f.get(x).toString());
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		Method[] mi = x.getClass().getMethods();
		for (Method m:mi){
			String mn=m.getName();
			System.out.print(m.getName() + "("+	+m.getParameterTypes().length+") -> ");
			System.out.print("["+m.getReturnType() + "] ");
			if("wait|notify|notifyAll".indexOf(mn)>=0||m.getParameterTypes().length>=1){
				System.out.println("skip...");
				continue;
			}
			try {
				if (Modifier.isStatic(m.getModifiers())) {
					System.out.println("= "+m.invoke(null, null).toString());
				}else{
					System.out.println("= "+m.invoke(x, null).toString());					
				}
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}					
		}

		return;
	}

	public static void main(String[] args) {
		A a = new A();
		view(a);
	}

}
