'use strict';

angular.module('jjztsacaapp1App').controller('StationDialogController',
    ['$scope', '$stateParams', '$modalInstance', 'entity', 'Station', 'Route',
        function($scope, $stateParams, $modalInstance, entity, Station, Route) {

        $scope.station = entity;
        $scope.routes = Route.query();
        $scope.load = function(id) {
            Station.get({id : id}, function(result) {
                $scope.station = result;
            });
        };

        var onSaveFinished = function (result) {
            $scope.$emit('jjztsacaapp1App:stationUpdate', result);
            $modalInstance.close(result);
        };

        $scope.save = function () {
            if ($scope.station.id != null) {
                Station.update($scope.station, onSaveFinished);
            } else {
                Station.save($scope.station, onSaveFinished);
            }
        };

        $scope.clear = function() {
            $modalInstance.dismiss('cancel');
        };
}]);
