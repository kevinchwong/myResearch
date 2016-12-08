(function () {
  'use strict';

  angular.module('myApp.controllers')
    .controller('myCtrl', ['$scope', function($scope){    	
    	$scope.myDataIn
    	="[\n" +
    		"\t{\"name\": \"Top Level\",\"children\": [\n" +
    			"\t\t{\"name\": \"Level 2: A\",\"children\": [\n" +
					"\t\t\t{\"name\": \"Son of A\"}\n" +
					"\t\t\t,{\"name\": \"Daughter of A\"}\n" +
    			"\t\t]}\n" +
        		"\t\t,{\"name\": \"Level 2: B\"}\n"+
    		"\t]}\n" +
    	"]";
    	$scope.myDataOut="";
    }]);
}());
