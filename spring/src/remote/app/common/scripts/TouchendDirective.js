angular
    .module('common')
    .directive('ngTouchend', [function() {
        return function(scope, element, attr) {
            element.on('touchend', function(event) {
                scope.$apply(function() {
                    scope.$eval(attr.ngTouchend);
                });
            });
        };
    }]);