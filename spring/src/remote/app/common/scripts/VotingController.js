angular
    .module('common')
    .controller('VotingController', function($scope, supersonic, $http) {
        window.setTimeout(function() {
            $scope.serverIP = window.localStorage.getItem('serverIP');
            $scope.serverPort = window.localStorage.getItem('serverPort');
            $scope.playerName = window.localStorage.getItem('playerName');
            $scope.players = JSON.parse(window.localStorage.getItem('players'));
            supersonic.logger.log($scope.players);
            $scope.$apply();
        }, 2000);

        function bla() {
            $scope.players = JSON.parse(window.localStorage.getItem('players'));
            setTimeout(bla, 1000);
        }

        $scope.selection = {};
        //for (var i=0; i<$scope.players.length; i++) {
        //    $scope.selection[players[i].id] = false;
        //}
        $scope.votingEnabled = true;
        $scope.status = 'Would you like to leave anyone behind?';
        $scope.setSelection = function(i) {
            if($scope.votingEnabled) {
                $scope.selection[i] = !$scope.selection[i];
                supersonic.logger.log($scope.selection);
            }
            $scope.apply();
        };
        $scope.vote = function() {
            if($scope.votingEnabled) {
                $scope.status = 'Casting your vote...';
                $scope.votingEnabled = false;
                var vote = [];
                for(var i in $scope.selection.length) {
                    vote.push(i);
                }
                $http.get($scope.serverIP + '/vote?' + JSON.stringify({vote: vote}))
                    .success(function (datat) {
                        $scope.status = 'Waiting for other players...';
                        ping();
                    })
                    .error(function (data) {
                        supersonic.logger.log('Error sending vote');
                    });
            }
        };
        function ping() {
            $http.get($scope.serverIP + '/ask_ready')
                .success(function (data, status, headers, config) {
                    if ('ready' in data && data['ready']) {
                        supersonic.ui.layers.replace('gamepad');
                    } else {
                        setTimeout(ping, 1000);
                    }
                })
                .error(function (data, status, headers, config) {
                    supersonic.logger.log('ERROR ' + status + ' ' + data + ' ' + headers + ' ' + config);
                    $scope.status = 'Connection error';
                }
            );
        }
        bla();
    });