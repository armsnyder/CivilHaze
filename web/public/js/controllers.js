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
    .controller("StartController", function($scope, $q, GameConnectionService, MainService) {
        GameConnectionService.connect().then(function() {
            $scope.status = "Connected";
            GameConnectionService.registerDisconnectCallback(onDisconnect);
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
    .controller("GameController", function($scope, ControllerService, GameConnectionService) {
        GameConnectionService.registerDisconnectCallback(onDisconnect);
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
    })
    .controller("VotingController", function($scope) {

    });