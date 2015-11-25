/*
 * Copyright (c) 2015 Adam Snyder. All rights reserved.
 */

'use strict';

angular.module('comeAgain')
    .factory('MainService', function() {
        var observerCallbacks = [];
        var page;
        function notifyObservers() {
            angular.forEach(observerCallbacks, function(callback) {
                callback(page);
            })
        }
        return {
            registerObserverCallback: function(callback) {
                observerCallbacks.push(callback);
            },
            setPage: function(newPage) {
                page = newPage;
                notifyObservers();
            }
        }
    })
    .factory('ControllerService', function($q, GameResource, GameConnectionService) {
        var MAX_FAILED_CONNECTIONS = 3;
        var badConnectionCount = 0;
        var retry;
        var openRequests = 0;
        var lastJoystickInput;
        function conSuccess() {
            openRequests--;
            //if (openRequests > 0 && lastJoystickInput.magnitude==0) {
            //    factory.joystick(lastJoystickInput);
            //}
            badConnectionCount = 0;
        }
        function conError(error) {
            console.error(error);
            badConnectionCount++;
            if (badConnectionCount >= MAX_FAILED_CONNECTIONS) {
                GameConnectionService.disconnect();
            } else {
                retry.fn(retry.arg);
            }
        }
        var factory = {
            buttonOn: function(button) {
                retry = {fn: factory.buttonOn, arg: button};
                GameResource.resource().buttonOn({button: button}).$promise.then(conSuccess, conError);
            },
            buttonOff: function(button) {
                retry = {fn: factory.buttonOff, arg: button};
                GameResource.resource().buttonOff({button: button}).$promise.then(conSuccess, conError);
            },
            joystick: function(input) {
                retry = {fn: factory.joystick, arg: input};
                lastJoystickInput = input;
                GameResource.resource().joystick(input).$promise.then(conSuccess, conError);
                openRequests++;
            },
            vote: function(participants) {
                retry = {fn: factory.vote, arg: participants};
                GameResource.resource()({participants: participants}).vote().$promise.then(conSuccess, conError);
            },
            getParticipants: function(deferred) {
                if (!deferred) deferred = $q.defer();
                retry = {fn: factory.getParticipants, arg: deferred};
                GameResource.resource().getParticipants().$promise.then(function (response) {
                    conSuccess();
                    deferred.resolve(response);
                }, function (error) {
                    if (badConnectionCount+1 >= MAX_FAILED_CONNECTIONS) {
                        deferred.reject(response);
                    } else {
                        conError(error);
                    }
                });
                return deferred.promise;
            }
        };
        return factory;
    })
    .factory('GameConnectionService', function($q, RouteResource, GameResource) {
        var cachedIP;
        var AUTO_RESTART_DELAY = 3000;
        var PING_INTERVAL = 5000;
        var connection;
        var pingInterval;
        var observerCallbacks = [];
        function notifyObservers(errObj) {
            angular.forEach(observerCallbacks, function(callback) {
                callback(errObj);
            })
        }
        function ping() {
            GameResource.resource(cachedIP).ping().$promise.then(function(response) {
                if (!response || response.result == 'false') {
                    notifyObservers('disconnected');
                    window.clearInterval(pingInterval);
                }
            }, function() {
                notifyObservers('disconnected');
                window.clearInterval(pingInterval);
            })
        }
        return {
            connect: function() {
                var deferred = $q.defer();
                function tryConnection() {
                    function tryAgain(message) {
                        deferred.notify(message);
                        window.setTimeout(tryConnection, AUTO_RESTART_DELAY);
                    }
                    deferred.notify('Establishing connection.');
                    RouteResource.query().$promise.then(function (ipResponse) {
                        GameResource.setRoot(ipResponse.result);
                        GameResource.resource().connect().$promise.then(function(conResponse) {
                            if (conResponse.result) {
                                cachedIP = ipResponse.result;
                                pingInterval = window.setInterval(ping, PING_INTERVAL);
                                deferred.resolve(conResponse);
                            } else {
                                tryAgain('Failed to establish connection. Retrying in '+AUTO_RESTART_DELAY+
                                    ' milliseconds.');
                            }
                        }, function() {
                            tryAgain('Failed to establish connection. Retrying in '+AUTO_RESTART_DELAY+
                                ' milliseconds.');
                        });
                    }, function (error) {
                        if (error.status == 409) {
                            tryAgain('No route found. Retrying in '+AUTO_RESTART_DELAY+' milliseconds.');
                        } else {
                            deferred.reject(error);
                        }
                    })
                }
                if (cachedIP) {
                    deferred.resolve('already connected');
                } else {
                    tryConnection();
                }
                return deferred.promise;
            },
            disconnect: function() {
                cachedIP = null;
                notifyObservers('disconnected');
                if (pingInterval) window.clearInterval(pingInterval);
            },
            registerDisconnectCallback: function(callback) {
                observerCallbacks.push(callback);
            }
        };
    })
    .factory('RouteResource', function($resource) {
        var ROOT = "http://come-again.net";
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
                    joystick: {method: 'POST', params: {a: 'input', b: 'joystick'}},
                    vote: {method: 'POST', params: {a: 'input', b: 'vote'}},
                    getParticipants: {method: 'GET', params: {a: 'participants'}}
                })
            }
        }
    });