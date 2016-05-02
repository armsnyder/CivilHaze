/*
 * Copyright (c) 2015 Adam Snyder. All rights reserved.
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
            scope: {
                numAngles: '=',
                numMagnitude: '='
            },
            link: function(scope, element) {
                var ctx = element[0].getContext('2d');
                var lastSent = {angle: 0, magnitude: 0};
                var origin;
                var finger;
                var scale;
                var outerCircleRadius;
                var innerCircleRadius;
                var radius;
                updateRadii();
                draw({x: 0, y: 0});

                function updateRadii() {
                    origin = {x: element[0].offsetWidth/2, y: element[0].offsetHeight/2};
                    scale = {x: element[0].width/element[0].offsetWidth, y: element[0].width/element[0].offsetWidth};
                    outerCircleRadius = 0.7 * origin.x;
                    innerCircleRadius = 0.45 * origin.x;
                    radius = origin.x - innerCircleRadius;
                }
                function getCoords(event) {
                    return {x: event.touches[0].clientX, y: event.touches[0].clientY};
                }
                function maybeSend() {
                    var magnitude = Math.sqrt(Math.pow(finger.x-origin.x, 2)+Math.pow(finger.y-origin.y, 2));
                    if (magnitude > radius) magnitude = radius;
                    magnitude /= radius;
                    var snapMagnitude = Math.round(magnitude * scope.numMagnitude) / scope.numMagnitude;
                    var angle = Math.atan2(finger.y-origin.y, finger.x-origin.x);
                    var snapAngle = Math.round(angle / Math.PI * scope.numAngles / 2) / scope.numAngles * Math.PI * 2;
                    if (lastSent.angle !== snapAngle || lastSent.magnitude !== snapMagnitude) {
                        send({angle: snapAngle, magnitude: snapMagnitude});
                    }
                    draw({
                        x: magnitude * radius * Math.cos(angle),
                        y: magnitude * radius * Math.sin(angle)
                    });
                }
                function send(input) {
                    ControllerService.joystick(input);
                    lastSent = input;
                }
                function draw(offset) {
                    element[0].width = element[0].width;
                    ctx.beginPath();
                    ctx.arc(origin.x*scale.x, origin.y*scale.y, outerCircleRadius*scale.x, 0, 2 * Math.PI, false);
                    ctx.fillStyle = '#444';
                    ctx.fill();
                    ctx.beginPath();
                    ctx.arc(origin.x*scale.x, origin.y*scale.y, innerCircleRadius*scale.x, 0, 2 * Math.PI, false);
                    ctx.fillStyle = '#555';
                    ctx.fill();
                    ctx.beginPath();
                    ctx.arc((origin.x+offset.x)*scale.x, (origin.y+offset.y)*scale.y, innerCircleRadius*scale.x, 0, 2 * Math.PI, false);
                    ctx.fillStyle = '#777';
                    ctx.fill();
                    ctx.lineWidth = 2;
                    ctx.strokeStyle = 'black';
                    ctx.stroke();
                }
                element.on('touchstart', function(event) {
                    updateRadii();
                    finger = getCoords(event);
                    maybeSend();
                });
                element.on('touchmove', function(event) {
                    finger = getCoords(event);
                    maybeSend();
                });
                element.on('touchend', function() {
                    draw({x: 0, y: 0});
                    send({angle: lastSent.angle, magnitude: 0});
                });

            }
        }
    })
    .directive('ngButton', function(ControllerService) {
        return {
            link: function (scope, element) {
                element.on('touchstart', function() {
                    element.css('background', '#FFF');
                    ControllerService.buttonOn("spin");
                });
                element.on('touchend', function() {
                    element.css('background', '#777');
                    ControllerService.buttonOff("spin");
                });
            }
        }
    })
    .directive('ngCorrectRotation', function() {
        return {
            transclude: true,
            templateUrl: 'partials/correctRotation',
            link: function(scope, element, attrs) {
                var desiredRotation = 'vertical';
                var actualRotation = 'vertical';
                function evaluateRotation() {
                    scope.desiredRotation = desiredRotation;
                    scope.showTransclude = desiredRotation === actualRotation;
                    if(!scope.$$phase) {
                        scope.$apply();
                    }
                }
                function applyOrientation() {
                    //console.log('apply');
                    switch(window.orientation) {
                        case 90:
                        case -90:
                            actualRotation = 'horizontal';
                            break;
                        case 180:
                        case 0:
                        default:
                            actualRotation = 'vertical';
                            break;
                    }
                }
                element.bind('orientationchange', function () {
                    //console.log('change');
                    applyOrientation();
                    evaluateRotation();
                });
                scope.showTransclude = true;
                scope.$watch(attrs.ngCorrectRotation, function(value) {
                    desiredRotation = value;
                    evaluateRotation();
                });
                applyOrientation();
            }
        }
    });