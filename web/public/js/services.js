angular.module('comeAgain')
    .factory('ControllerService', function($http) {
        var factory = {};
        factory.getGameIP = function() {

        };
        return factory;
    })
    .factory('GameRouteService', function($http) {
        var MASTER_URL = "http://come-again.net";
        var MY_IP_URL = "http://checkip.amazonaws.com";  //TODO: Don't rely on external source
        return {
            getGameIP: function() {
                //TODO: Make sure error on first call is reflected in promise
                return $http.get(MY_IP_URL).then(function(result) {
                    return $http.get(MASTER_URL+"/api/private/"+result);
                });
            }
        };
    });