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

angular.module('comeAgain')
    .directive('ngTouchend', [function() {
        return function(scope, element, attr) {
            element.on('touchend', function(event) {
                scope.$apply(function() {
                    scope.$eval(attr.ngTouchend);
                });
            });
        };
    }])
    .directive('ngTouchstart', [function() {
        return function(scope, element, attr) {
            element.on('touchstart', function(event) {
                scope.$apply(function() {
                    scope.$eval(attr.ngTouchstart);
                });
            });
        };
    }])
    .directive('ngCancelEvents', function() {
        return function(scope, element, attr) {
            element.on('touchstart', function(event) {
                event.preventDefault && event.preventDefault();
                event.stopPropagation && event.stopPropagation();
                event.cancelBubble = true;
                event.returnValue = false;
            })
        }
    });