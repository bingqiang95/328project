//控制层
app.controller('userController' ,function($scope,$controller   ,userService){

    $controller('baseController',{$scope:$scope});//继承

    //读取列表数据绑定到表单中
    $scope.findUser=function(){
        userService.findUser().success(
            function(response){
                $scope.list=response;
            }
        );
    }

    $scope.searchEntity={};

    // 假设定义一个查询的实体：searchEntity
    $scope.search = function(page,rows){
        // 向后台发送请求获取数据:
        userService.search(page,rows,$scope.searchEntity).success(function(response){
            $scope.paginationConf.totalItems = response.total;
            $scope.list = response.rows;
        });
    }

    //冻结用户 冻结后无法登陆下单
    $scope.freeze = function(){
        userService.freeze($scope.selectIds).success(function(response){
            // 判断冻结是否成功:
            if(response.flag==true){
                // 冻结成功
                alert(response.message);
                $scope.reloadList();
                $scope.selectIds = [];
            }else{
                // 冻结失败
                alert(response.message);
            }
        });
    }
    });