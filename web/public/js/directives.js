/*
 * Come Again is an interactive art piece in which participants perform a series of reckless prison breaks.
 * Copyright (C) 2015  Adam Snyder
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version. Any redistribution must give proper attribution to the original author.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */

'use strict';

angular.module('comeAgain')
    //.directive('ngTouchend', [function() {
    //    return function(scope, element, attr) {
    //        element.on('touchend', function(event) {
    //            scope.$apply(function() {
    //                scope.$eval(attr.ngTouchend);
    //            });
    //        });
    //    };
    //}])
    //.directive('ngTouchstart', [function() {
    //    return function(scope, element, attr) {
    //        element.on('touchstart', function(event) {
    //            scope.$apply(function() {
    //                scope.$eval(attr.ngTouchstart);
    //            });
    //        });
    //    };
    //}])
    .directive('ngCancelEvents', function() {
        return function(scope, element, attr) {
            element.on('touchstart', function(event) {
                event.preventDefault && event.preventDefault();
                event.stopPropagation && event.stopPropagation();
                event.cancelBubble = true;
                event.returnValue = false;
            })
        }
    })
    .directive('ngJoystick', function(ControllerService) {
        return {
            //templateUrl: 'partials/joystick',
            //scope: {
            //    joystick: '='
            //},
            link: function(scope, element, attrs) {
                var lastSent = {angle: 0, magnitude: 0};
                var start = {x: 0, y: 0};
                var end = {x: 0, y: 0};
                var on = false;
                function getCoords(event) {
                    return {x: event.touches[0].clientX, y: event.touches[0].clientY};
                }
                function maybeSend() {
                    var magnitude = Math.sqrt(Math.pow(end.x-start.x, 2)+Math.pow(end.y-start.y, 2));
                    var angle = Math.atan2(end.y-start.y, end.x-start.x);
                    if (Math.abs(magnitude-lastSent.magnitude) > 10 || Math.abs(angle-lastSent.angle) > 0.2) {
                        send({angle: angle, magnitude: magnitude});
                    }
                }
                function send(input) {
                    ControllerService.joystick(input);
                    lastSent = input;
                }
                element.on('touchstart', function(event) {
                    on = true;
                    start = getCoords(event);
                    end = getCoords(event);
                });
                element.on('touchmove', function(event) {
                    end = getCoords(event);
                    maybeSend();
                });
                element.on('touchend', function(event) {
                    on = false;
                    send({angle: 0, magnitude: 0});
                });

            }
            //controller: function($scope) {
            //
            //}
        }
    });