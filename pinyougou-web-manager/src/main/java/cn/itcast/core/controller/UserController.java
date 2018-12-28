package cn.itcast.core.controller;

import cn.itcast.core.pojo.specification.Specification;
import cn.itcast.core.pojo.user.User;
import cn.itcast.core.service.UserService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.PageResult;
import entity.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Reference
    private UserService userService;

    /**
     * 查询用户
     * @return
     */
    @RequestMapping("/findUser")
    public List<User> findUser(){
      return   userService.findUser();
    }

    //查询分页 条件
    @RequestMapping("/search")
    public PageResult search(Integer page, Integer rows, @RequestBody User user){
        return userService.search(page,rows,user);
    }

    /**
     * 查询用户数量
     * @return
     */
    @RequestMapping("/showUserCount")
    public Integer showUserCount(){
        return userService.showUserCount();
    }

    /**
     * 用户冻结
     * @param ids
     * @return
     */
    @RequestMapping("/freeze")
    public Result freeze(Long[] ids){
        try {
            userService.freeze(ids);
            return  new Result(true,"冻结成功");
        } catch (Exception e) {
            e.printStackTrace();
            return  new Result(false,"冻结失败");
        }
    }
}
