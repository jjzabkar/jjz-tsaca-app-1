'use strict';

angular.module('jjztsacaapp1App')
    .controller('ArrivalDetailController', function ($scope, $rootScope, $stateParams, entity, Arrival) {
        $scope.arrival = entity;
        $scope.load = function (id) {
            Arrival.get({id: id}, function(result) {
                $scope.arrival = result;
            });
        };
        $rootScope.$on('jjztsacaapp1App:arrivalUpdate', function(event, result) {
            $scope.arrival = result;
        });
    });
