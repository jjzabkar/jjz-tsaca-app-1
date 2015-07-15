'use strict';

angular.module('jjztsacaapp1App').config(function($stateProvider) {
	$stateProvider.state('arrival', {
		parent : 'entity',
		url : '/arrivals',
		data : {
			roles : [ 'ROLE_USER' ],
			pageTitle : 'Arrival'
		},
		views : {
			'content@' : {
				templateUrl : 'scripts/app/entities/arrival/arrivals.html',
				controller : 'ArrivalController'
			}
		},

		resolve : {
		// entity : function resolveEntityFn($stateParams, Arrival) {
		// // $scope.foo = 'bar';
		// console.info('$stateParams', $stateParams);
		// console.info('Arrival', Arrival);
		// return {
		// foo : 'foo',
		// bar : 'bar',
		// id : null
		// };
		// }
		}
	})
});
