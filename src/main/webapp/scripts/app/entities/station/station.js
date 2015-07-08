'use strict';

angular.module('jjztsacaapp1App')
    .config(function ($stateProvider) {
        $stateProvider
            .state('station', {
                parent: 'entity',
                url: '/stations',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'Stations'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/station/stations.html',
                        controller: 'StationController'
                    }
                },
                resolve: {
                }
            })
            .state('station.detail', {
                parent: 'entity',
                url: '/station/{id}',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'Station'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/station/station-detail.html',
                        controller: 'StationDetailController'
                    }
                },
                resolve: {
                    entity: ['$stateParams', 'Station', function($stateParams, Station) {
                        return Station.get({id : $stateParams.id});
                    }]
                }
            })
            .state('station.new', {
                parent: 'station',
                url: '/new',
                data: {
                    roles: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$modal', function($stateParams, $state, $modal) {
                    $modal.open({
                        templateUrl: 'scripts/app/entities/station/station-dialog.html',
                        controller: 'StationDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {name: null, outputSlots: null, stopId: null, travelTimeFromHomeToStationInSeconds: null, id: null};
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('station', null, { reload: true });
                    }, function() {
                        $state.go('station');
                    })
                }]
            })
            .state('station.edit', {
                parent: 'station',
                url: '/{id}/edit',
                data: {
                    roles: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$modal', function($stateParams, $state, $modal) {
                    $modal.open({
                        templateUrl: 'scripts/app/entities/station/station-dialog.html',
                        controller: 'StationDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['Station', function(Station) {
                                return Station.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('station', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            });
    });
