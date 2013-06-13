/* put your angular controllers here */

function uploadController($scope, $http) {
    $scope.mappingUrl = "../commcare-mrs-mapper/mapping";
    $scope.mappingsUrl = "../commcare-mrs-mapper/mappings";

    $http({method: "GET", url: $scope.mappingsUrl}).
        success(function (data, status) {
            $scope.status = status;
            $scope.mappings = data;

        }).
        error(function (data, status) {
            $scope.error = data || "Request failed";
            $scope.status = status;
        });

    $scope.deleteMapping = function (id) {

        $http({method: 'DELETE', url: $scope.mappingUrl + "/" + id}).
            success(function (data, status) {
                $scope.status = status;
                $scope.message = data;
                $scope.mappings = $scope.mappings.filter(function (mapping) {
                    return mapping._id != id;
                });
            }).
            error(function (data, status) {
                $scope.error = data || "Request failed";
                $scope.status = status;

            });
    };

    $scope.deleteAllMappings = function () {
        $http({method: 'DELETE', url: $scope.mappingsUrl}).
            success(function (data, status) {
                $scope.status = status;
                $scope.message = data;
                $scope.mappings = [];
            }).
            error(function (data, status) {
                $scope.error = data || "Request failed";
                $scope.status = status;
            });
    }

}
