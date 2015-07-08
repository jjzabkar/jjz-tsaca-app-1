'use strict';

angular.module('jjztsacaapp1App')
    .controller('StationController', function ($scope, Station) {
        $scope.stations = [];
        $scope.loadAll = function() {
            Station.query(function(result) {
               $scope.stations = result;
            });
        };
        $scope.loadAll();

        $scope.delete = function (id) {
            Station.get({id: id}, function(result) {
                $scope.station = result;
                $('#deleteStationConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Station.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteStationConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.station = {name: null, outputSlots: null, stopId: null, travelTimeFromHomeToStationInSeconds: null, id: null};
        };
    });
