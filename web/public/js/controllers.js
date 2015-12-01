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
        GameConnectionService.registerColorCallback(onColor);
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
        function onColor(color) {
            function dec2hex(dec) {
                var result = String(Number(parseInt(dec , 10)).toString(16));
                while (result.length < 2) {
                    result = '0'+result;
                }
                return result;
            }
            var color_part_hex_0 = dec2hex(255 * color[0]);
            var color_part_hex_1 = dec2hex(255 * color[1]);
            var color_part_hex_2 = dec2hex(255 * color[2]);
            $scope.color = "#" + color_part_hex_0 + color_part_hex_1 + color_part_hex_2;
        }
    })
    .controller("VotingController", function($scope) {

    });