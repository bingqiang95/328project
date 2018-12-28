app.controller("UserCountController",function($scope,ShowUserCountService){

	//显示当前登陆人
	$scope.showUserCount = function(){
		ShowUserCountService.showUserCount().success(function(response){
			$scope.number = response;//Map  POJO
		});
	}
	
});