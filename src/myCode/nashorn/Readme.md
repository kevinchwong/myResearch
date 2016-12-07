# Building a multi-thread Javascript Engine with Nashron

By Kevin Wong [@kevinwongprovenir](https://github.com/kevinwongprovenir)

- [Nashron samples (Multi-threads Version)](MTSample.java)

- [Nashron samples (Single-thread Version)](STSample.java)

## 1. Multi-thread doesn't mean faster
- Overhead in content switching.
- Time used in setting up the ExecutorService.
- Since Nashron ScriptEngine is not thread safety, the best multi-threads strategy is using an individual SctiptEngine for each thread. But If we use more than one ScriptEngines, extra memory and initialization time are needed.
- In our testing in local laptop, Single Thread verison is 4x faster than multi threads one.

### But why do we still use the mutli-threads algorithm?
- We get the partial results earlier.
- Better performance if we have enough hardware support.
- Scalability

### Some speed-up techniques
- Enable JIT by adding this JVM option: -XX:TieredStopAtLevel=1
- Precompile the javascript which is repeatedly used.

## 2. Object binding in Nashron
- The syntax is completely different in Rhino and Nashron.
- Before ScriptEngine execute eval(script), we need to setup the BINDINGS mapping of this Script Engine. Basically, this mapping is just a hashmap where "JS variable names" as key and "Java object references" as value.

- If we use invokeFunction() instead of eval(), we don't need BINDINGS mapping. However, we *MUST* define the "function signature" in the javascript and pass the object instance as parameters.

## 3. Precompiled javascripts
- Make sure you have binded all the non-parameter variables to the Script Engine before you call the compile();
- In our case, we just bind variable "ccList", but we don't bind parameter "i" when we creating the precompiled object. 
```Java
// Pre-compile
	// se is an instance of ScriptEngine
	se.getBindings(ScriptContext.ENGINE_SCOPE).put("ccList", ccList);
	CompiledScript compiled = ((Compilable) se)
	.compile("function sayHello(i){
		return \"pre-CompiledCode - \"+ccList.get(i).name;
	}");
	Object preCompiledCodeObj = compiled.eval();
```
- Then you can attached the preCompiledCodeObj to the actual ScriptEngine Bindings when you call the precompiled script later.
```Java
	List<Bindings> bds=new ArrayList<>();
    for (int i = 0; i < N; i++) {
		Bindings b=se.createBindings();
		b.put("ccList", ccList);
		b.put("preCompiledCode",preCompiledCodeObj);
		b.put("i", i);
		bds.add(i,b);
	}
```
- To execute the precompiled script, just call the preCompiledCodeObj as as javascript function.
```Java
	String script = "preCompiledCode(i);";
	se.eval(script);
```
- Please notice that original function name of the javascript is "SayHello(i)", but in the actual eval() call, the name "preCompoliedCode(i)" defined in the Bindings have been used.
- A compiled script can access the binded global variables defined outside of the function.

### 4. Same number of ScriptEngines and threads
- The prefect situation is we have same number of ScriptEngine, threads and processors

```
There will be many overhead if we recreate the ScriptEngine whenever a task is started, so we use the following approaches:

- Initially, we put P ScriptEngines into a Stack<ScriptEngine>.
- When a thread need a ScriptEngine, just call pop() to get it.
- When a thread completed its task, just push
() it back to recycle it.
```

### 5. Use Lambda function and closure scope technique to pass different identifer to each thread.
- Precise identifiers (just a integer ID in this case) are neccessary for each callable task (threads) to locate the object which they needed to handle.
- We use IntStream.range(0,N).foreach(i->{...}) to achieve this.
```Java
	IntStream.range(0, M).forEach(j -> {
		IntStream.range(0, N).forEach(i -> {
			futures.add(executor.submit(new Callable<String>() {
				@Override
				public String call() throws Exception {
					try {
						ScriptEngine s=ses.pop();
						s.setBindings(bds.get(i),ScriptContext.ENGINE_SCOPE);													
						String r=(String) s.eval(scripts.get(j));
						ses.push(s);
						return r;
					} catch (ScriptException e) {
						e.printStackTrace();
					}
					return null;
				}
			}));
		});
	});
```


### 6. Future and Queue
- For asynchronous programming, main flow does not keep waiting until one task is completed.
- This mean that after we have initiated several tasks in the executor, the main flow and these tasks are executing concurrently.
- Future objects associated to the tasks are also changing its state concurrently.
- Whenever a task is completed, the state of its associated future will become .isDone()--> true immediately.
- Is our program, we use a *Future Queue* to keep track all status of all future objects.
```Java
	// Core execution
	try {
		LinkedList<Future<String>> q = new LinkedList<Future<String>>();
		q.addAll(futures);
		while (!q.isEmpty()) {
			Future<String> fs = q.pollFirst();
			if (!fs.isDone())
			{
				q.offerLast(fs);
			}else
				System.out.println("future.get = " + fs.get());
		}
	} catch (ExecutionException | InterruptedException e) {
		e.printStackTrace();
	}
```
