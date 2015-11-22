angular.module('comeAgain')
    .controller("MainController", function($scope) {
        $scope.page = "start";
    })
    .controller("StartController", function($scope, $q, GameConnectionService) {
        GameConnectionService.connect().then(function(response) {
            console.log(response);
            $scope.status = "Connected to "+response;
        }, function(error) {
            $scope.status = "Fatal error";
            console.error(error);
        }, function(message) {
            $scope.status = message;
        });

    })
    .controller("GameController", function($scope) {

    })
    .controller("VotingController", function($scope) {

    });