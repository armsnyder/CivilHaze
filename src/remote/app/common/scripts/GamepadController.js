angular
    .module('common')
    .controller('GamepadController', function($scope, supersonic, $http) {
        $scope.serverIP = window.localStorage.getItem('serverIP');
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
            return $http.get($scope.serverIP+'/button?' + JSON.stringify({button: buttonNum, status: status}));
        }
        function ping() {
            window.localStorage.removeItem('players');
            $http.get($scope.serverIP + '/gamePing')
                .success(function (data, status, headers, config) {
                    if ('ready' in data && 'players' in data && data['ready']) {
                        window.localStorage.setItem('players', JSON.stringify(data['players']));
                        window.setTimeout(function() {
                            supersonic.ui.layers.replace('voting');
                        }, 2000);
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