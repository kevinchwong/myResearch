(function() {
	'use strict';

	angular.module('myApp.directives')
		.directive('myTextarea', function() {
			return {
				restrict: 'E',
				template: '<textarea/>',
				replace: true,
				scope: {
					stat: '='
				},
				link: function(scope, element, attrs) {
					if (!scope.stat){
						scope.stat = {};
						scope.stat.textAreaRect={};
				    	scope.stat.fontSizeIdx=5;
				    	scope.stat.lineSpace=6;
					}
					var tb=element[0];
					tb.style["height"] = attrs['height'];
					tb.style["width"] = attrs['width'];
					var paddingSize=10;

					var rect=tb.getBoundingClientRect();
					var textareaInnerHeight=tb.scrollHeight;
					scope.stat.lineHeight=Math.round((textareaInnerHeight-paddingSize)/scope.stat.fontSizeIdx);
					scope.stat.fontSize=scope.stat.lineHeight-scope.stat.lineSpace;
					tb.style["font-size"]=scope.stat.fontSize+"px";
					tb.style["line-height"]=scope.stat.lineHeight+"px";

					element.on('keydown', function(e) {
					    if (e.keyCode == 38 && e.altKey) {
							e.preventDefault();
							if(scope.stat.fontSizeIdx>3)
								scope.stat.fontSizeIdx--;
							scope.stat.lineHeight=Math.round(scope.stat.textAreaRect.height/scope.stat.fontSizeIdx);
							scope.stat.fontSize=scope.stat.lineHeight-scope.stat.lineSpace;
							tb.style["font-size"]=scope.stat.fontSize+"px";
							tb.style["line-height"]=scope.stat.lineHeight+"px";
				        }
				        else if (e.keyCode == 40 && e.altKey) {
							e.preventDefault();
							var limit=10;
							if(scope.stat.maxRow>8)
								limit=scope.stat.maxRow-1;							
							if(scope.stat.fontSizeIdx<limit)
								scope.stat.fontSizeIdx++;
							scope.stat.lineHeight=Math.round(scope.stat.textAreaRect.height/scope.stat.fontSizeIdx);
							scope.stat.fontSize=scope.stat.lineHeight-scope.stat.lineSpace;
							tb.style["font-size"]=scope.stat.fontSize+"px";
							tb.style["line-height"]=scope.stat.lineHeight+"px";
				        }
				        else if (e.keyCode == 9 || e.which == 9) {
							e.preventDefault();
							var s = tb.selectionStart;
							tb.value = tb.value.substring(0, tb.selectionStart)
									+ "\t" + tb.value.substring(tb.selectionEnd);
							tb.selectionEnd = s + 1;
						}
					});
					
					element.on('keydown keyup click', function(e) {
						scope.$apply(function() {
							scope.stat.getPos = getPos(tb);
							scope.stat.value = tb.value;
							scope.stat.scrollHeight = tb.scrollHeight;
							scope.stat.scrollTop = tb.scrollTop;
							scope.stat.lines = tb.value.split("\n");
							scope.stat.maxRow = scope.stat.lines.length;
							for(var k in rect)
								scope.stat.textAreaRect[k.toString()]=rect[k.toString()];
							var c = 0,
								r = 0;
							for (var i = 0; i < scope.stat.maxRow; i++) {
								var a = scope.stat.lines[i];
								c += a.length + 1;
								if (scope.stat.getPos < c) {
									r = i;
									break;
								}
							}
							scope.stat.getRow = r;
						});
					});
					scope.$watch('stat.setPos', function(newVal) {
						if (typeof newVal === 'undefined') return;
						setPos(tb, newVal);
					});

					window.onresize = function() {
						return scope.$apply();
					};
					scope.$watch(function() {
						return angular.element(window)[0].innerWidth;
					}, function() {
						scope.stat.screenWidth = angular.element(window)[0].innerWidth;
					});
					scope.$watch(function() {
						return angular.element(window)[0].innerHeight;
					}, function() {
						scope.stat.screenHeight = angular.element(window)[0].innerHeight;
					});
				}
			};
		});

	function getPos(element) {
		if ('selectionStart' in element) {
			return element.selectionStart;
		} else if (document.selection) {
			element.focus();
			var sel = document.selection.createRange();
			var selLen = document.selection.createRange().text.length;
			sel.moveStart('character', -element.value.length);
			return sel.text.length - selLen;
		}
	}

	function setPos(element, caretPos) {
		if (element.createTextRange) {
			var range = element.createTextRange();
			range.move('character', caretPos);
			range.select();
		} else {
			element.focus();
			if (element.selectionStart !== undefined) {
				element.setSelectionRange(caretPos, caretPos);
			}
		}
	}

}());
