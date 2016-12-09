# Use Javascript Engine with Java Reflection

## Reason
- API interface of the Javascript evaluaton engine should be more generic so that many clients can access this without altering the engine.

## Solution
- Using List\<Object\> as the input parameters type is much better than using
a particular class type.

## Idea
- We use java reflection to analyze the input instances after they had passed
to the engine. 
Then, we use the Field[] information to fill the object bindings 
mapping.

## Input
- M scripts
- N objects where the variable names of which may appear in the javascripts. 

## Output
- M messages where each line is the response of the execution of each script. 

## Usage
1. Initiate the Engine:
```Java
		NashornWithReflection engine=new NashornWithReflection();
```

2. Load the javascripts:
```Java		
		engine.loadScript("" + "price > 5.0;" + "");
		engine.loadScript("" + "name;" + "");
		engine.loadScript("" + "appleOnly;" + "");
		engine.loadScript("" + "walmartOnly;" + "");
```

3. Create the bindings by adding the list of object(s)
```Java
		Apple a = new Apple();
		a.name = "Apple IIE";
		a.price = 12.0;
		
		Bindings appleBindings = engine.createBindings(Arrays.asList(new Object[]{a}));
```

4. Execute and print the result.
```Java
		List<String> result=engine.execute(appleBindings);
		for(String s:result){
			System.out.println(s);
		}
```

##Java Reflection
We use the following code to build the generic bindings mapping:
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