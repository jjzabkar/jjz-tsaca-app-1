'use strict';

angular.module('jjztsacaapp1App').controller('MainController',
		[ '$scope', 'Arrival', 'Principal', function MainControllerFn($scope, Arrival, Principal) {
			Principal.identity().then(function(account) {
				$scope.account = account;
				$scope.isAuthenticated = Principal.isAuthenticated;
			});

			$scope.arrivals = {};
			$scope.maxOutputSlots = 8;
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

			$scope.hasArrivals = function hasArrivalsFn() {
				return $scope.arrivals && $scope.arrivals.stations && true;
			}

			$scope.outputSlotsArray = [ 1, 2, 3, 4, 5, 6, 7, 8 ];

		} ]);
