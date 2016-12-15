(function () {
  'use strict';

  angular.module('myApp.controllers')
    .controller('myCtrl', ['$scope', function($scope){    	
    	$scope.myDataIn
    	="<My-TextArea>\n\n"+
    		"- Alt-up/down = zoom in/out.\n"+
    		"- Tab = advance the cursor to the next tab stop."
    }]);
}());
