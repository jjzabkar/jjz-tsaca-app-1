'use strict';

angular.module('jjztsacaapp1App').controller('ArrivalDialogController',
    ['$scope', '$stateParams', '$modalInstance', 'entity', 'Arrival',
        function($scope, $stateParams, $modalInstance, entity, Arrival) {

        $scope.arrival = entity;
        $scope.load = function(id) {
            Arrival.get({id : id}, function(result) {
                $scope.arrival = result;
            });
        };

        var onSaveFinished = function (result) {
            $scope.$emit('jjztsacaapp1App:arrivalUpdate', result);
            $modalInstance.close(result);
        };

        $scope.save = function () {
            if ($scope.arrival.id != null) {
                Arrival.update($scope.arrival, onSaveFinished);
            } else {
                Arrival.save($scope.arrival, onSaveFinished);
            }
        };

        $scope.clear = function() {
            $modalInstance.dismiss('cancel');
        };
}]);
