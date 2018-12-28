//服务层
app.service('userService',function($http) {
    //分页查询所有用户
    this.findUser = function () {
        return $http.get('../user/findUser.do');
    }
    //冻结用户 无法登陆 下单
    this.freeze = function(ids){
        return $http.get("../user/freeze.do?ids="+ids);
    }

    this.search= function(page,rows,searchEntity){
        return $http.post("../user/search.do?pageNum="+page+"&pageSize="+rows,searchEntity);
    }

});