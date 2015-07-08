'use strict';

angular.module('jjztsacaapp1App')
    .controller('StationDetailController', function ($scope, $rootScope, $stateParams, entity, Station, Route) {
        $scope.station = entity;
        $scope.load = function (id) {
            Station.get({id: id}, function(result) {
                $scope.station = result;
            });
        };
        $rootScope.$on('jjztsacaapp1App:stationUpdate', function(event, result) {
            $scope.station = result;
        });
    });
