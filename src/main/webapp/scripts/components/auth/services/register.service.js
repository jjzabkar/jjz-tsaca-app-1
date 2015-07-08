'use strict';

angular.module('jjztsacaapp1App')
    .factory('Register', function ($resource) {
        return $resource('api/register', {}, {
        });
    });


