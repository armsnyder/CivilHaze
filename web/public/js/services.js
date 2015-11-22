angular.module('comeAgain')
    .factory('ControllerService', function($http) {
        var factory = {};
        factory.getGameIP = function() {

        };
        return factory;
    })
    .factory('GameConnectionService', function($q, RouteResource, GameResource) {
        var cachedIP;
        var AUTO_RESTART_DELAY = 3000;
        return {
            connect: function() {
                var deferred = $q.defer();
                if (cachedIP) {
                    deferred.resolve('already connected');
                } else {
                    function tryConnection(deferred) {
                        deferred.notify('Establishing connection.');
                        RouteResource.query().$promise.then(function (ipResponse) {
                            GameResource.setRoot(ipResponse.result);
                            GameResource.resource().connect().$promise.then(function(conResponse) {
                                cachedIP = ipResponse.result;
                                deferred.resolve(conResponse);
                            }, function(error) {
                                deferred.reject(error);
                            });
                        }, function (error) {
                            if (error.status == 409) {
                                deferred.notify('No route found. Retrying in '+AUTO_RESTART_DELAY+' milliseconds.');
                                window.setTimeout(function(){tryConnection(deferred)}, AUTO_RESTART_DELAY);
                            } else {
                                deferred.reject(error);
                            }
                        })
                    }
                    tryConnection(deferred);
                }
                return deferred.promise;
            },
            disconnect: function() {
                cachedIP = null;
            }
        };
    })
    .factory('RouteResource', function($resource) {
        var ROOT = "http://www.come-again.net";
        //var ROOT = "http://localhost:3000";
        return $resource(ROOT+'/api/ip/private', {}, {query: {method: 'GET'}});
    })
    .factory('GameResource', function($resource) {
        var root;
        var PORT = 8000;
        return {
            setRoot: function(ip) {
                root = ip;
            },
            resource: function(ip) {
                if (!ip && !root) return;
                if (!ip) ip = root;
                return $resource('http://'+ip+':'+PORT+'/:a/:b/:c/:d', {}, {
                    ping: {method: 'GET', params: {a: 'ping'}},
                    connect: {method: 'GET', params: {a: 'connect'}},
                    disconnect: {method: 'GET', params: {a: 'disconnect'}},
                    buttonOn: {method: 'POST', params: {a: 'input', b: 'button', c: '@button', d: 'on'}},
                    buttonOff: {method: 'POST', params: {a: 'input', b: 'button', c: '@button', d: 'off'}},
                    vote: {method: 'POST', params: {a: 'input', b: 'vote'}},
                    getPlayers: {method: 'GET', params: {a: 'players'}}
                })
            }
        }
    });