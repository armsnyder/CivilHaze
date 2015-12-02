/*
 * Copyright (c) 2015 Adam Snyder. All rights reserved.
 */

angular.module('comeAgain', ['ngResource', 'ngTouch'])
    .config(function($httpProvider) {
        $httpProvider.interceptors.push('Interceptor');
    });
    //.config(['$routeProvider', '$locationProvider', function($routeProvider, $locationProvider) {
    //    $routeProvider
    //        .when('/', {
    //            templateUrl: 'partials/index',
    //            controller: IndexController
    //        })
    //        .otherwise({
    //            redirectTo: '/'
    //        });
    //    $locationProvider.html5Mode(true);
    //}])
    //.controller("MainController", function($scope) {
    //    $scope.page = "start";
    //});