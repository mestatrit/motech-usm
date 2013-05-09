/* put your angular controllers here */

function uploadController($scope, $http) {
    $scope.allMappingsUrl = "../commcare-mrs-mapper/getAllMappings";
    $scope.deleteMappingUrl = "../commcare-mrs-mapper/deleteMapping?xmlns=";
    $scope.deleteAllMappingsUrl = "../commcare-mrs-mapper/deleteAllMappings";

    $http({method: "GET", url: $scope.allMappingsUrl}).
        success(function (data, status) {
            $scope.status = status;
            $scope.mappings = data;

        }).
        error(function (data, status) {
            $scope.error = data || "Request failed";
            $scope.status = status;
        });

    $scope.deleteMapping = function (xmlns) {

        $http({method: 'DELETE', url: $scope.deleteMappingUrl + xmlns}).
            success(function (data, status) {
                $scope.status = status;
                $scope.message = data;
                $scope.mappings = $scope.mappings.filter(function (mapping) {
                    return mapping.xmlns != xmlns;
                });
            }).
            error(function (data, status) {
                $scope.error = data || "Request failed";
                $scope.status = status;

            });
    };

    $scope.deleteAllMappings = function () {
        $http({method: 'DELETE', url: $scope.deleteAllMappingsUrl}).
            success(function (data, status) {
                $scope.status = status;
                $scope.message = data;
                $scope.mappings = {};
            }).
            error(function (data, status) {
                $scope.error = data || "Request failed";
                $scope.status = status;
            });
    }

}
