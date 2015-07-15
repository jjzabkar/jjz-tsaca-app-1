'use strict';

angular.module('jjztsacaapp1App')
    .controller('ArrivalController', function ($scope, Arrival) {
        $scope.arrivals = [];
        $scope.loadAll = function() {
            Arrival.query(function(result) {
               $scope.arrivals = result;
            });
        };
        $scope.loadAll();

        $scope.delete = function (id) {
            Arrival.get({id: id}, function(result) {
                $scope.arrival = result;
                $('#deleteArrivalConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Arrival.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteArrivalConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.arrival = {foo: null, bar: null, id: null};
        };
    });
