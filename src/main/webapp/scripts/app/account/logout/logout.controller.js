'use strict';

angular.module('jjztsacaapp1App')
    .controller('LogoutController', function (Auth) {
        Auth.logout();
    });
