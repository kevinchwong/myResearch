package myCode.nashorn;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
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
public class STSample {

	public static class Utils {
		static long last = (new Date()).getTime();

		public static long timeMe() {
			long res = (new Date()).getTime() - last;
			last = (new Date()).getTime();
			return res;
		}

		public static void printRunTime() {
			System.out.println("Time used:" + timeMe() + "ms");
		}
	}

	public static class CustomerClass {

		public String test(int i) {
			return ("Object " + i + ", Rule 1 : " + name + ": test() called!");
		}

		public static String test2(int i) {
			return ("Object " + i + ", Rule 2 : test2() called at " + (new Date()).getTime());
		}

		public String name = "ABC";
		public Double amt = 0.0;
	}

	public static void main(String[] args) throws ScriptException {

		final ScriptEngine se = new ScriptEngineManager().getEngineByExtension("js");
	    List<ScriptEngineFactory> factories = new ScriptEngineManager().getEngineFactories();
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
	    
		// Test 1: Passing Outer Class
		se.getBindings(ScriptContext.GLOBAL_SCOPE).put("manager", new ManagerClass());
		se.eval("manager.test(0); manager.class.static.test2(0);print(manager.name);");

		// Test 2: Passing Inner Class
		se.getBindings(ScriptContext.ENGINE_SCOPE).put("customer", new CustomerClass());
		se.eval("customer.test(0); customer.class.static.test2(0);print(customer.name);");

		// Test 3: Passing Object>[]
		CustomerClass[] ccArray = new CustomerClass[1];
		ccArray[0] = new CustomerClass();
		ccArray[0].name = "BCD";
		se.getBindings(ScriptContext.ENGINE_SCOPE).put("ccArray", ccArray);
		se.getBindings(ScriptContext.ENGINE_SCOPE).put("i", 0);
		se.eval("ccArray[i].test(i); ccArray[i].class.static.test2(i);print(ccArray[i].name);");

		// Test 4: Passing List<Object>
		List<CustomerClass> ccList2 = new ArrayList<CustomerClass>();
		for (int i = 0; i < 3; i++) {
			CustomerClass c = new CustomerClass();
			c.name = "CDE" + i;
			ccList2.add(c);
		}
		se.getBindings(ScriptContext.ENGINE_SCOPE).put("ccList", ccList2);
		for (int i = 0; i < 3; i++) {
			se.getBindings(ScriptContext.ENGINE_SCOPE).put("i", i);
			se.eval("ccList.get(i).test(i); ccList.get(i).class.static.test2(i);print(ccList.get(i).name);");
		}

		// Test 5: Single-threads with 1 ScriptEngine and N Classes with Executor
		// Service
		System.out.println("\nSingle-threads with 1 ScriptEngine and N Classes with Executor Service.");
		System.out.println("===========================================================================");
		Utils.timeMe();

		// N objects of 1 Class CustomerClass
		int N = 1000;

		List<CustomerClass> ccList = new ArrayList<CustomerClass>();
		
		// N bindings
		List<Bindings> bds=new ArrayList<>();
		se.getBindings(ScriptContext.ENGINE_SCOPE).put("ccList", ccList);
	    CompiledScript compiled = ((Compilable) se).compile("function sayHello(i){return \"pre-CompiledCode - \"+ccList.get(i).name;}");
	    Object preCompiledCodeObj = compiled.eval();
		for (int i = 0; i < N; i++) {
			Bindings b=se.createBindings();
			b.put("ccList", ccList);
			b.put("preCompiledCode",preCompiledCodeObj);
			b.put("i", i);
			bds.add(i,b);
		}
		
		for (int i = 0; i < N; i++) {
			CustomerClass c = new CustomerClass();
			c.name = "CDE" + i;
			c.amt = 100.0 * i;
			ccList.add(c);
		}

		// M JSRules
		List<String> scripts = new ArrayList<>();
		scripts.add("ccList.get(i).test(i);");
		scripts.add("ccList.get(i).class.static.test2(i);");
		scripts.add("'Object '+i+', Rule 3 : '+ccList.get(i).name;");
		scripts.add("'Object '+i+', Rule 4 : '+(ccList.get(i).amt>2000.0);");
		scripts.add("'Object '+i+', Rule 5 : '+preCompiledCode(i);");
		scripts.add(""
			+ "var input={"
			+ "\"object\":i,"
			+ "\"rule\":6,"
			+ "\"firstName\":\"Kevin\""
			+ "};"
			+ "\"Object \"+input.object+\", Rule \"+input.rule+\" : passed!\";");		
		int M = scripts.size();

		// Core execution
		for(int i=0;i<N;i++){
			se.setBindings(bds.get(i),ScriptContext.ENGINE_SCOPE);
			for(int j=0;j<M;j++){
				System.out.println(">>"+se.eval(scripts.get(j)));
			}
		}
		
		System.out.println(N + " Objects");
		System.out.println(M + " JS Rules");
		System.out.println(1 + " Threads");
		Utils.printRunTime();
	}
}
