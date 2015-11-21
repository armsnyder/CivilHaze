angular.module('comeAgain')
    .controller("MainController", function($scope) {
        $scope.page = "start";
    })
    .controller("StartController", function($scope, GameRouteService) {
        $scope.foo = "bars";
        GameRouteService.getGameIP().then(function(response) {
            $scope.foo = response;
            console.log('success: '+response);
        }, function(error) {
            console.log(error);  //TODO: Handle Error
        });
    })
    .controller("GameController", function($scope) {

    })
    .controller("VotingController", function($scope) {

    });