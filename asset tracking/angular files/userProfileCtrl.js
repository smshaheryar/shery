
app.controller('userProfileCtrl', function($rootScope, $scope, UserService, LocationService)
{
    $scope.initializeUserInfo = function()
    {
        LocationService.fillAllUsers("fillUserInfoTbl");
    };

    $scope.resetUserInfo = function()
    {
        LocationService.resetUserInfoDAO();
    };

    $scope.editUserInfo = function () {

        UserService.editUserInfoDAO();
    };

    $scope.saveUserInfo = function()
    {
        UserService.saveUserInfoDAO();
    };

    $scope.changeCountry = function(country)
    {
        LocationService.fillAllStates(country);
    };

    $scope.changeState = function(country, state)
    {
        LocationService.fillAllCities(country, state);
    };

    $scope.closeUserProfilePopup = function()
    {
        $scope.closePopupAndToggleLeftPanel();
    };
});