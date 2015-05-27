angular
    .module('common')
    .controller('LoginController', function($scope, supersonic, $http) {
        $scope.playerName = '';
        $scope.serverIP = '';
        $scope.status = '';
        $scope.loginEnabled = true;

        var serverPort = '8001';

        $scope.submit = function() {
            $scope.loginEnabled = false;
            $scope.status = 'Connecting';
            $http.post('http://' + $scope.serverIP + ':' + serverPort + '/ask_ready', {playerName: $scope.playerName})
                .success(function (data, status, headers, config) {
                    $scope.status = 'Connected. Waiting for other players.';
                    ping();
                })
                .error(function (data, status, headers, config) {
                    supersonic.logger.log('ERROR ' + status + ' ' + data + ' ' + headers + ' ' + config);
                    $scope.loginEnabled = true;
                    $scope.status = 'Connection error';
                });
        };

        function ping() {
            $http.post('http://' + $scope.serverIP + ':' + serverPort + '/ask_ready', {})
                .success(function (data, status, headers, config) {
                    if ('ready' in data && data['ready']) {
                        saveVariables();
                        supersonic.ui.initialView.dismiss();
                    } else {
                        setTimeout(function() {
                            ping();
                        }, 1000);
                    }
                })
                .error(function (data, status, headers, config) {
                    supersonic.logger.log('ERROR ' + status + ' ' + data + ' ' + headers + ' ' + config);
                    $scope.loginEnabled = true;
                    $scope.status = 'Connection error';
                }
            );
        }
        
        function saveVariables() {
            window.localStorage.setItem('serverPort', serverPort);
            window.localStorage.setItem('serverIP', $scope.serverIP);
            window.localStorage.setItem('playerName', $scope.playerName);
        }
    }
);