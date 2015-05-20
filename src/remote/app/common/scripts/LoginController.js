angular
    .module('common')
    .controller('LoginController', function($scope, supersonic, $http) {
        $scope.playerName = '';
        $scope.serverIP = '';
        $scope.status = '';
        $scope.loginEnabled = true;

        var serverPort = '8001';

        $scope.submit = function() {
            supersonic.logger.log('jasdhsajkd');
            $scope.loginEnabled = false;
            $scope.status = 'Connecting';
            $http.post('http://' + $scope.serverIP + ':' + serverPort + '/ping', {playerName: $scope.playerName})
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
            supersonic.logger.log('abc');
            $http.post('http://' + $scope.serverIP + ':' + serverPort + '/ping', {}, timeout=1000)
                .success(function (data, status, headers, config) {
                    if ('ready' in data && data['ready']) {
                        saveVariables();
                        supersonic.ui.initialView.dismiss();
                    } else {
                        setTimeout(function() {
                            supersonic.logger.log('pinging');
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