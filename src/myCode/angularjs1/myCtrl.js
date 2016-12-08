(function () {
  'use strict';

  angular.module('myApp.controllers')
    .controller('myCtrl', ['$scope', function($scope){    	
    	$scope.myDataIn
    	="ShareTree\n" +
    		"\tYou type,\n" +
			"\tI draw,\n" +
			"\tAnd we share!";
    	$scope.myDataOut="";
    }]);
}());
