'use strict';

angular.module('jjztsacaapp1App').controller('RouteDialogController',
    ['$scope', '$stateParams', '$modalInstance', 'entity', 'Route',
        function($scope, $stateParams, $modalInstance, entity, Route) {

        $scope.route = entity;
        $scope.load = function(id) {
            Route.get({id : id}, function(result) {
                $scope.route = result;
            });
        };

        var onSaveFinished = function (result) {
            $scope.$emit('jjztsacaapp1App:routeUpdate', result);
            $modalInstance.close(result);
        };

        $scope.save = function () {
            if ($scope.route.id != null) {
                Route.update($scope.route, onSaveFinished);
            } else {
                Route.save($scope.route, onSaveFinished);
            }
        };

        $scope.clear = function() {
            $modalInstance.dismiss('cancel');
        };
}]);
