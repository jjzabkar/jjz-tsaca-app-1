'use strict';

angular.module('jjztsacaapp1App')
    .controller('RouteController', function ($scope, Route) {
        $scope.routes = [];
        $scope.loadAll = function() {
            Route.query(function(result) {
               $scope.routes = result;
            });
        };
        $scope.loadAll();

        $scope.delete = function (id) {
            Route.get({id: id}, function(result) {
                $scope.route = result;
                $('#deleteRouteConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Route.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteRouteConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.route = {routeId: null, longName: null, shortName: null, id: null};
        };
    });
