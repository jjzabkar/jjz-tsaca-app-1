'use strict';

angular.module('jjztsacaapp1App').controller('ArrivalController', [ '$scope', 'Arrival', function ArrivalControllerFn($scope, Arrival) {
	$scope.arrivals = {};
	$scope.maxOutputSlots = 2;
	$scope.loadAll = function loadAllFn() {
		Arrival.query(function ArrivalQueryFn(result) {
			$scope.arrivals = result;
			if (result && result.stations) {
				angular.forEach(result.stations, function forStationFn(station) {
					$scope.maxOutputSlots = Math.max(station.outputSlots, $scope.maxOutputSlots);
				});
			}
		});
	};
	$scope.loadAll();

	$scope.refresh = function() {
		$scope.loadAll();
	};

} ]);
