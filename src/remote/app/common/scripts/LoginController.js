angular
    .module('common')
    .controller('LoginController', function($scope, supersonic) {
        $scope.playerName = '';
        $scope.serverIP = '';
        $scope.submit = function() {
            supersonic.ui.initialView.dismiss()
        }
    });