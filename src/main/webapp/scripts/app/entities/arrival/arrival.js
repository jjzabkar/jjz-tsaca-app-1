'use strict';

angular.module('jjztsacaapp1App')
    .config(function ($stateProvider) {
        $stateProvider
            .state('arrival', {
                parent: 'entity',
                url: '/arrivals',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'Arrivals'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/arrival/arrivals.html',
                        controller: 'ArrivalController'
                    }
                },
                resolve: {
                }
            })
            .state('arrival.detail', {
                parent: 'entity',
                url: '/arrival/{id}',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'Arrival'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/arrival/arrival-detail.html',
                        controller: 'ArrivalDetailController'
                    }
                },
                resolve: {
                    entity: ['$stateParams', 'Arrival', function($stateParams, Arrival) {
                        return Arrival.get({id : $stateParams.id});
                    }]
                }
            })
            .state('arrival.new', {
                parent: 'arrival',
                url: '/new',
                data: {
                    roles: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$modal', function($stateParams, $state, $modal) {
                    $modal.open({
                        templateUrl: 'scripts/app/entities/arrival/arrival-dialog.html',
                        controller: 'ArrivalDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {foo: null, bar: null, id: null};
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('arrival', null, { reload: true });
                    }, function() {
                        $state.go('arrival');
                    })
                }]
            })
            .state('arrival.edit', {
                parent: 'arrival',
                url: '/{id}/edit',
                data: {
                    roles: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$modal', function($stateParams, $state, $modal) {
                    $modal.open({
                        templateUrl: 'scripts/app/entities/arrival/arrival-dialog.html',
                        controller: 'ArrivalDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['Arrival', function(Arrival) {
                                return Arrival.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('arrival', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            });
    });
