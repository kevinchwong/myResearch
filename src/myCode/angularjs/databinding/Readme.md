# Data binding AngularJS

## Learn by studying code:

```javascript
<!doctype html>

<meta charset="utf-8">
<title>NG-MODEL STUDY</title>
<meta name="description" content="R and D">
<link rel="stylesheet" href="bootstrap.min.css">
<body ng-app="myApp" ng-controller="myCtrl">
	<h1>NG-MODEL STUDY</h1>

	<h3>Models</h3>
	Data: &lt;input type=&quot;text&quot; ng-model=&quot;data&quot; class=&quot;form-control&quot;&gt;
	<input type="text" ng-model="data" class="form-control">
	<br>
	Output: &lt;input type=&quot;text&quot; ng-model=&quot;output&quot; class=&quot;form-control&quot;&gt;
	<input type="text" ng-model="output" class="form-control">
	<br>

    <h3>One-way data binding</h3>
	Data (div) (read-only): &lt;div&gt; { { data } } &lt;\div&gt;
	<div class="form-control">{{data}}</div>
	<br>

    <h3>Two-way data binding</h3>
	Data: &lt;input type=&quot;text&quot; ng-model=&quot;data&quot; &gt;
	<input type="text" ng-model="data" class="form-control">
	<br>
	Data (textarea): &lt;textarea ng-model=&quot;data&quot; &gt;&lt;\textarea&gt;
	<textarea ng-model="data" class="form-control"></textarea>
	<br>
	Output: &lt;input type=&quot;text&quot; ng-model=&quot;output&quot; class=&quot;form-control&quot;&gt;
	<input type="text" ng-model="output" class="form-control">
	<br>

	<h3>Directives</h3>
	Converter: &lt;converter my-data=&quot;data&quot; my-output=&quot;output&quot;&gt;
	<converter my-data="data" my-output="output"></converter>

	localScope: &lt;local-scope my-data=&quot;data&quot; &gt;
	<br>ng-model='object' defined in template is just local element, it didn't affect global ng-model:output
	<local-scope my-data="data"></local-scope>

	localLabel: &lt;local-label my-data=&quot;data&quot; &gt;
	<local-label my-data="data"></local-label>

	<script src="angular.min.js"></script>
	<script>
	(function () {
		'use strict';	
		angular.module('myApp',[])
			.controller('myCtrl', ['$scope', function($scope){
				$scope.data = 'fabio';
				$scope.output = "what?";
			}])
 			.directive('converter', function(){
 			    return {
 			        restrict: 'E',
  			        scope: {
  			        	myData: '=',
  			        	myOutput: '='
  			        }, 
 			        template: '<textarea ng-model="myOutput" class="form-control"></textarea>'
 		        	,
 			        link: function(scope, element, attrs) {
 						scope.$watch(function(){
 							return scope.myData;
 						}, function(){
 							scope.myOutput=scope.myData+" (converted)!!!";
 						});
 					}
 		        };
			})
 			.directive('localScope', function(){
 			    return {
 			        restrict: 'E',
  			        scope: {
  			        	myData: '='
  			        }, 
 			        template: '<textarea ng-model="output" class="form-control"></textarea>'
 		        	,
 			        link: function(scope, element, attrs) {
 						scope.$watch(function(){
 							return scope.myData;
 						}, function(){
 							scope.output=scope.myData+" localScope!!";
 						});
 					}
 		        };
			})			
 			.directive('localLabel', function(){
 			    return {
 			        restrict: 'E',
  			        scope: {
  			        	myData: '=',
  			        	myOutput: '@'
  			        }, 
 			        template: '<div>{{myOutput|json}}</div>'
 		        	,
 			        link: function(scope, element, attrs) {
 						scope.$watch(function(){
 							return scope.myData;
 						}, function(){
 							scope.myOutput=scope.myData+" (local label)!!!";
 						});
 					}
 		        };
			})
			;
	}());
	</script>
</body>
</html>
```

	