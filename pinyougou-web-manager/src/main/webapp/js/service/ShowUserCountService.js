app.service("ShowUserCountService",function($http){
	
	this.showUserCount= function(){
		return $http.get("../user/showUserCount.do");
	}
	
});