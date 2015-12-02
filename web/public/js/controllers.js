/*
 * Copyright (c) 2015 Adam Snyder. All rights reserved.
 */

angular.module('comeAgain')
    .controller("MainController", function($scope, MainService) {
        function pageChange(page) {
            $scope.page = page;
        }
        MainService.registerObserverCallback(pageChange);
        MainService.setPage('start');
    })
    .controller("StartController", function($scope, $q, GameConnectionService, MainService, Interceptor) {
        GameConnectionService.connect().then(function() {
            $scope.status = "Connected";
            Interceptor.registerDisconnectCallback(onDisconnect);
            MainService.setPage('game');
        }, function(error) {
            $scope.status = "Fatal error";
            console.error(error);
        }, function(message) {
            $scope.status = message;
        });
        function onDisconnect(error) {
            $scope.status = error;
        }

    })
    .controller("GameController", function($scope, ControllerService, Interceptor) {
        $scope.color = "#000000";
        $scope.desiredRotation = 'horizontal';
        $scope.buttonOn = function(button) {
            ControllerService.buttonOn(button);
        };
        $scope.buttonOff = function(button) {
            ControllerService.buttonOff(button);
        };
        function onDisconnect(error) {
            $scope.status = error;
        }
        function onMessage(message) {
            if (message.hasOwnProperty('color')) {
                applyColor(message.color);
            }
        }
        function applyColor(color) {
            function dec2hex(dec) {
                var result = String(Number(parseInt(dec, 10)).toString(16));
                while (result.length < 2) {
                    result = '0' + result;
                }
                return result;
            }
            $scope.color = "#" + dec2hex(255 * color[0]) + dec2hex(255 * color[1]) + dec2hex(255 * color[2]);
        }
        function init() {
            Interceptor.registerDisconnectCallback(onDisconnect);
            Interceptor.registerMessageCallback(onMessage);
            var pastMessages = Interceptor.getMessageLog();
            for (var i=pastMessages.length-1; i>=0; i--) {
                if (pastMessages[i].hasOwnProperty('color')) {
                    applyColor(pastMessages[i].color);
                    break;
                }
            }
        }
        init();
    })
    .controller("VotingController", function($scope) {

    });