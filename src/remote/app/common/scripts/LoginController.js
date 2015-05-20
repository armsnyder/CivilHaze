angular
    .module('common')
    .controller('LoginController', function($scope, supersonic, $http) {
        var serverPort = '8001';
        $scope.loginEnabled = true;
        $scope.playerName = '';
        $scope.serverIP = '';
        $scope.submit = function() {
            $scope.loginEnabled = false;
            $http.post('http://'+$scope.serverIP+':'+serverPort+'/ping', {playerName: $scope.playerName});
            supersonic.ui.initialView.dismiss();
        }
    });