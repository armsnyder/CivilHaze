/*
 * Come Again is an interactive art piece in which participants perform a series of reckless prison breaks.
 * Copyright (C) 2015  Adam Snyder
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version. Any redistribution must give proper attribution to the original author.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
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