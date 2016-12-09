package myCode.nashorn.javareflection;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 *
 * @project jsrules-engine
 * @author kwong
 *
 */
public class NashornWithReflection {

	public static class Apple {

		public String test() {
			return ("_this_.test() in Apple is called!");
		}

		public static String testSt() {
			return ("_this_.testSt() in Apple is called!");
		}

		public String name = "Apple";
		public final String appleOnly = "Apple Only";
		public Double price = 5.0;
	}

	public static class Walmart {

		public String test() {
			return ("_this_.test() in Walmart is called!");
		}

		public static String testSt() {
			return ("_this_.testSt() in Walmart is called!");
		}

		public final String walmartOnly = "Walmart Only";
		public String name = "Walmart";

	}

	final public ScriptEngineManager sem;
	final public ScriptEngine se;
	public List<String> scripts = new ArrayList<String>();
	
	public NashornWithReflection(){
		sem = new ScriptEngineManager();
		se = sem.getEngineByExtension("js");		
	}
	
	public Bindings createBindings(List<Object> objs){
		Bindings bd=se.createBindings();
		for(Object x:objs){
			enhanceBindings(bd,x,"_this_");
		}
		return bd;
	}
		
    public void enhanceBindings(Bindings bd, Object x, String objName) {
			Field[] fs = x.getClass().getFields();
			for (Field f : fs) {
				int mod = f.getModifiers();
				try {
					bd.put(f.getName(), f.get(Modifier.isStatic(mod) ? null : x));
				} catch (IllegalArgumentException | IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			bd.put(objName,x);
			
//			// Not support binding JAVA methods in this way
			Method[] ms = x.getClass().getMethods();
			for (Method m : ms) {
				bd.put(m.getName(), m);
			}			
	}

	public List<String> execute(Bindings bd){
		List<String> res=new ArrayList<>();
		se.setBindings(bd, ScriptContext.ENGINE_SCOPE);
		for (int i = 0; i < scripts.size(); i++) {
			se.getBindings(ScriptContext.ENGINE_SCOPE).put("i", i);
			try {
				res.add(">> Rule" + i + " : " + se.eval(scripts.get(i)));
			} catch (ScriptException e) {
				res.add(">> Rule" + i + " : " + e.toString());
			}
		}
		return res;
	}
	public void loadScript(String s){
		scripts.add(s);
	}
	
	public void clearScriptList()
	{
		scripts.clear();
	}

	public void printInfo()
	{
		List<ScriptEngineFactory> factories = sem.getEngineFactories();
		for (ScriptEngineFactory f : factories) {
			System.out.println("engine name:" + f.getEngineName());
			System.out.println("engine version:" + f.getEngineVersion());
			System.out.println("language name:" + f.getLanguageName());
			System.out.println("language version:" + f.getLanguageVersion());
			System.out.println("names:" + f.getNames());
			System.out.println("mime:" + f.getMimeTypes());
			System.out.println("extension:" + f.getExtensions());
			System.out.println("-----------------------------------------------");
		}
	}
		
	public static void main(String[] args) throws ScriptException {
		
		Apple a = new Apple();
		a.name = "Apple IIE";
		a.price = 12.0;

		Walmart b = new Walmart();
		b.name = "WALMART";

		NashornWithReflection engine=new NashornWithReflection();
		engine.printInfo();
		
		// M Javascript
		engine.loadScript("" + "price > 5.0;" + "");
		engine.loadScript("" + "name;" + "");
		engine.loadScript("" + "appleOnly;" + "");
		engine.loadScript("" + "walmartOnly;" + "");
		engine.loadScript("" + "test();" + "");
		engine.loadScript("" + "test;" + "");
		engine.loadScript("" + "_this_.test();" + "");
		engine.loadScript("" + "_this_.class.static.testSt();" + "");

		System.out.println("\nCall with loading Apple ...");
		Bindings appleBindings = engine.createBindings(Arrays.asList(new Object[]{a}));
		List<String> result=engine.execute(appleBindings);
		for(String s:result){
			System.out.println(s);
		}

		System.out.println("\nCall with loading Walmart ...");
		Bindings walmartBindings = engine.createBindings(Arrays.asList(new Object[]{b}));
		List<String> result2=engine.execute(walmartBindings);
		for(String s:result2){
			System.out.println(s);
		}

		System.out.println("\nCall with loading Apple and then Walmart ...");
		Bindings appleWalmartBindings = engine.createBindings(Arrays.asList(new Object[]{a,b}));
		List<String> result3=engine.execute(appleWalmartBindings);
		for(String s:result3){
			System.out.println(s);
		}
	}
}
