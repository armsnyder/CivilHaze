angular.module('comeAgain')
    .directive('ngTouchend', [function() {
        return function(scope, element, attr) {
            element.on('touchend', function(event) {
                scope.$apply(function() {
                    scope.$eval(attr.ngTouchend);
                });
            });
        };
    }])
    .directive('ngTouchstart', [function() {
        return function(scope, element, attr) {
            element.on('touchstart', function(event) {
                scope.$apply(function() {
                    scope.$eval(attr.ngTouchstart);
                });
            });
        };
    }])
    .directive('ngCancelEvents', function() {
        return function(scope, element, attr) {
            element.on('touchstart', function(event) {
                event.preventDefault && event.preventDefault();
                event.stopPropagation && event.stopPropagation();
                event.cancelBubble = true;
                event.returnValue = false;
            })
        }
    });