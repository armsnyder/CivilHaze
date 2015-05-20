angular
    .module('common')
    .directive('ngTouchstart', [function() {
        return function(scope, element, attr) {
            element.on('touchstart', function(event) {
                scope.$apply(function() {
                    scope.$eval(attr.ngTouchstart);
                });
            });
        };
    }]);