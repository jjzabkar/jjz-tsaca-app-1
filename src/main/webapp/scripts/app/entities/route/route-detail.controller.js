'use strict';

angular.module('jjztsacaapp1App')
    .controller('RouteDetailController', function ($scope, $rootScope, $stateParams, entity, Route) {
        $scope.route = entity;
        $scope.load = function (id) {
            Route.get({id: id}, function(result) {
                $scope.route = result;
            });
        };
        $rootScope.$on('jjztsacaapp1App:routeUpdate', function(event, result) {
            $scope.route = result;
        });
    });
