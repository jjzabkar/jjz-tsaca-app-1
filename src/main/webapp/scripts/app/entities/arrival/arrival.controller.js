'use strict';

angular.module('jjztsacaapp1App').controller('ArrivalController', function($scope, Arrival) {
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
		$scope.clear();
	};

	$scope.clear = function() {
		$scope.arrival = {
			foo : null,
			bar : null,
			id : null
		};
	};
});
