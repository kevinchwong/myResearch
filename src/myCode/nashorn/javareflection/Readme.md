# Use Javascript Engine with Java Reflection

By Kevin Wong [@kevinchwong](https://github.com/kevinchwong)

[Code](NashornWithReflection.java)

## Goal
- API interface of the Javascript evaluaton engine should be more generic so that various clients can access this engine without altering the core.

## Solution
- New API interface using List\<Object\> as the input parameters type, this design is more generic than using particular class types.
- With the new API interface, any number of input are allowed to be loaded to the engine.
- With our new engine design, clients can just use variable name without specifying the instance prefix in the script. For example: using "firstName" instead of "customer.firstName".

## Idea
- We use java reflection to analyze the input instances after they had passed to the engine. Then, we use the Field[] information to fill the object bindings mapping.

## Input
- M scripts
- N objects where the variable names of which may appear in the javascripts. 

## Output
- M messages where each line is the response of the execution of each script. 

## Sample classes we used:
```Java
	public static class Apple {

		public final String appleOnly = "Valid";
		public String name = "Apple";
		public Double price = 3.0;
	}

	public static class Walmart {

		public final String walmartOnly = "Valid";
		public String name = "Walmart";
	}
```

## Usage
- 1. Initiate the Engine:
```Java
		NashornWithReflection engine=new NashornWithReflection();
```

- 2. Create the bindings by adding the list of object(s)
```Java
		Apple a = new Apple();		
		Walmart w = new Walmart();		
		Bindings appleWalmartBindings = engine.createBindings(Arrays.asList(new Object[]{a,w}));
```

- 3. Load the javascripts:
```Java		
		engine.loadScript("(price > 2.5)?\"EXPENSIVE\":\"CHEAP\";");
		engine.loadScript("\"name = \\\"\"+name+\"\\\"\";");
		engine.loadScript("\"appleOnly = \"+appleOnly;");
		engine.loadScript("\"walmartOnly = \"+walmartOnly;");
```

- 4. Execute and print the result.
```Java
		List<String> result=engine.execute(appleWalmartBindings);
		for(String s:result){
			System.out.println(s);
		}
```

## Results
```
>> Rule 0 : EXPENSIVE
>> Rule 1 : name = "WALMART"
>> Rule 2 : appleOnly = Valid
>> Rule 3 : walmartOnly = Valid
```
##Java Reflection
We use the following code to build the generic bindings mapping with Java Reflection technique:
```Java
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
	}
```
