'use strict';

angular.module('jjztsacaapp1App')
    .factory('Station', function ($resource, DateUtils) {
        return $resource('api/stations/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    });
