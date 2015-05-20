angular
    .module('common')
    .controller('LoginController', function($scope, supersonic, $http) {
        $scope.playerName = 'Adas';
        $scope.serverIP = '10.0.0.100';
        $scope.status = '';
        $scope.loginEnabled = true;

        var serverPort = '8001';

        $scope.submit = function() {
            supersonic.logger.log('jasdhsajkd');
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
            supersonic.logger.log('abc');
            $http.post('http://' + $scope.serverIP + ':' + serverPort + '/ask_ready', {})
                .success(function (data, status, headers, config) {
                    supersonic.logger.log(data);
                    if ('ready' in data && data['ready']) {
                        saveVariables();
                        supersonic.ui.initialView.dismiss();
                    } else {
                        supersonic.logger.log('wow');
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