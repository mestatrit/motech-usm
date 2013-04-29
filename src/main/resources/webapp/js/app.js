'use strict';

/* put your routes here */

angular.module('commcare-mrs-mapper', ['motech-dashboard', 'ngCookies', 'bootstrap'])
    .config(['$routeProvider', function ($routeProvider) {
        $routeProvider
            .when('/upload', { templateUrl: '../commcare-mrs-mapper/resources/partials/upload.html', controller: uploadController })
            .otherwise({redirectTo: '/upload'});
    }]);
