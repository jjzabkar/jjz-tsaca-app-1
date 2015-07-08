'use strict';

angular.module('jjztsacaapp1App')
    .config(function ($stateProvider) {
        $stateProvider
            .state('route', {
                parent: 'entity',
                url: '/routes',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'Routes'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/route/routes.html',
                        controller: 'RouteController'
                    }
                },
                resolve: {
                }
            })
            .state('route.detail', {
                parent: 'entity',
                url: '/route/{id}',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'Route'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/route/route-detail.html',
                        controller: 'RouteDetailController'
                    }
                },
                resolve: {
                    entity: ['$stateParams', 'Route', function($stateParams, Route) {
                        return Route.get({id : $stateParams.id});
                    }]
                }
            })
            .state('route.new', {
                parent: 'route',
                url: '/new',
                data: {
                    roles: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$modal', function($stateParams, $state, $modal) {
                    $modal.open({
                        templateUrl: 'scripts/app/entities/route/route-dialog.html',
                        controller: 'RouteDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {routeId: null, longName: null, shortName: null, id: null};
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('route', null, { reload: true });
                    }, function() {
                        $state.go('route');
                    })
                }]
            })
            .state('route.edit', {
                parent: 'route',
                url: '/{id}/edit',
                data: {
                    roles: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$modal', function($stateParams, $state, $modal) {
                    $modal.open({
                        templateUrl: 'scripts/app/entities/route/route-dialog.html',
                        controller: 'RouteDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['Route', function(Route) {
                                return Route.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('route', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            });
    });
