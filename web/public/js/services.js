angular.module('comeAgain')
    .factory('ControllerService', function($http) {
        var factory = {};
        factory.getGameIP = function() {

        };
        return factory;
    })
    .factory('GameRouteService', function($q, RouteResource) {
        var cachedIP;
        return {
            getGameIP: function(force) {
                var deferred = $q.defer();
                if (force || !cachedIP) {
                    RouteResource.query().$promise.then(function(response) {
                        console.log(response);
                        cachedIP = response.result;
                        deferred.resolve(cachedIP);
                    }, function(error) {
                        console.log(error);
                        deferred.reject(error);
                    })
                } else {
                    deferred.resolve(cachedIP);
                }
                return deferred.promise;
            }
        };
    })
    .factory('RouteResource', function($resource) {
        var ROOT = "http://come-again.net";
        //var ROOT = "http://localhost:3000";
        return $resource(ROOT+'/api/ip/private', {}, {query: {method: 'GET'}});
    })
    .factory('GameResource', function($resource) {
        return $resource('http://:ip/:a/:b/:c/:d', {ip: '@ip'}, {
            ping: {method: 'GET', params: {a: 'ping'}},
            connect: {method: 'GET', params: {a: 'connect'}},
            disconnect: {method: 'GET', params: {a: 'disconnect'}},
            buttonOn: {method: 'POST', params: {a: 'input', b: 'button', c: '@button', d: 'on'}},
            buttonOff: {method: 'POST', params: {a: 'input', b: 'button', c: '@button', d: 'off'}},
            vote: {method: 'POST', params: {a: 'input', b: 'vote'}},
            getPlayers: {method: 'GET', params: {a: 'players'}}
        })
    });