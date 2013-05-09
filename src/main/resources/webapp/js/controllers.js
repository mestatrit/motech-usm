/* put your angular controllers here */

function uploadController($scope, $http) {
    $scope.method = 'GET';
    $scope.url = '../commcare-mrs-mapper/getAllMappings';

    $http({method: $scope.method, url: $scope.url}).
        success(function (data, status) {
            $scope.status = status;
            $scope.mappings = data;

        }).
        error(function (data, status) {
            $scope.data = data || "Request failed";
            $scope.status = status;
        });
}
