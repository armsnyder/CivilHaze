angular
    .module('common')
    .controller('LoginController', function($scope, supersonic, $http) {
        $scope.playerName = '';
        $scope.serverIP = '';
        $scope.status = '';
        $scope.loginEnabled = true;

        $scope.submit = function() {
            $scope.loginEnabled = false;
            $scope.status = 'Connecting';
            $http.get('http://atomicriot.com/ia')
                .success(function(newIP) {
                    $scope.serverIP = newIP;
                    $http.get(newIP + '/ask_ready?' + JSON.stringify({playerName: $scope.playerName}))
                        .success(function () {
                            $scope.status = 'Connected. Waiting for other players.';
                            ping();
                        })
                        .error(function (data, status, headers, config) {
                            supersonic.logger.log('ERROR ' + status + ' ' + data + ' ' + headers + ' ' + config);
                            $scope.loginEnabled = true;
                            $scope.status = 'Connection error';
                        });
                })
                .error(function (data, status, headers, config) {
                    supersonic.logger.log('ERROR Atomic' + status + ' ' + data + ' ' + headers + ' ' + config);
                    $scope.loginEnabled = true;
                    $scope.status = 'Connection error';
                });
        };

        function ping() {
            $http.get($scope.serverIP + '/ask_ready')
                .success(function (data) {
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
            window.localStorage.setItem('serverIP', $scope.serverIP);
            window.localStorage.setItem('playerName', $scope.playerName);
        }
    }
);