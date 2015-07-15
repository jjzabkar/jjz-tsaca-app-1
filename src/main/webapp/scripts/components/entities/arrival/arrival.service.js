'use strict';

angular.module('jjztsacaapp1App')
    .factory('Arrival', function ($resource, DateUtils) {
        return $resource('/arrivals/json', {}, {
            'query': { method: 'GET' } //, isArray: true},
//            'get': {
//                method: 'GET',
//                transformResponse: function (data) {
//                    data = angular.fromJson(data);
//                    return data;
//                }
//            },
//            'update': { method:'PUT' }
        });
    });
