angular
    .module('common')
    .controller('GamepadController', function($scope, supersonic, $http) {
        $scope.serverIP = window.localStorage.getItem('serverIP');
        $scope.serverPort = window.localStorage.getItem('serverPort');
        $scope.playerName = window.localStorage.getItem('playerName');
        $scope.selected = null;
        $scope.buttonDown = function(buttonNum) {
            sendButton(buttonNum, true).error(function(data, status, headers, config) {
                supersonic.logger.log('ERROR '+status+' '+data+' '+headers+' '+config);
            });
            var num = null;
            if (buttonNum=='arrowUp') num = 1;
            if (buttonNum=='arrowLeft') num = 2;
            if (buttonNum=='arrowRight') num = 3;
            if (buttonNum=='arrowDown') num = 4;
            if (buttonNum=='a') num = 5;
            if (buttonNum=='b') num = 6;
            $scope.selected = num;
        };
        $scope.buttonUp = function(buttonNum) {
            sendButton(buttonNum, false).error(function(data, status, headers, config) {
                supersonic.logger.log('ERROR '+status+' '+data+' '+headers+' '+config);
            });
            $scope.selected = null;
        };
        function sendButton(buttonNum, status) {
            return $http.post('http://'+$scope.serverIP+':'+$scope.serverPort+'/button', {button: buttonNum, status: status});
        }
        function ping() {
            $http.post('http://' + $scope.serverIP + ':' + $scope.serverPort + '/gamePing', {})
                .success(function (data, status, headers, config) {
                    if ('ready' in data && 'players' in data && data['ready']) {
                        window.localStorage.setItem('players', JSON.stringify(data['players']));
                        supersonic.ui.layers.replace('voting');
                    } else {
                        setTimeout(ping, 1000);
                    }
                })
                .error(function (data, status, headers, config) {
                    supersonic.logger.log('ERROR ' + status + ' ' + data + ' ' + headers + ' ' + config);
                }
            );
        }
        setTimeout(ping, 2000);
    });