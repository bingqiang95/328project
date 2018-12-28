package cn.itcast.core.service;

import cn.itcast.core.mapper.user.UserDao;
import cn.itcast.core.pojo.user.User;
import cn.itcast.core.pojo.user.UserQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import entity.PageResult;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.transaction.annotation.Transactional;

import javax.jms.*;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 用户管理
 */
@Service
@Transactional
public class UserServiceImpl implements  UserService {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
   // private JmsMessagingTemplate jmsMessagingTemplate;
    private JmsTemplate jmsTemplate;
    @Autowired
    private Destination smsDestination;
    @Autowired
    private UserDao userDao;
    //发送短信验证码
    @Override
    public void sendCode(String phone) {
        //1:生成6位验证码
        String randomNumeric = RandomStringUtils.randomNumeric(6);
        //2:
        redisTemplate.boundValueOps(phone).set(randomNumeric);
        redisTemplate.boundValueOps(phone).expire(5, TimeUnit.DAYS);
        //3:发消息

        jmsTemplate.send(smsDestination, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {

                MapMessage mapMessage = session.createMapMessage();
                mapMessage.setString("phone",phone);
                mapMessage.setString("signName","品优购商城");
                mapMessage.setString("templateCode","SMS_126462276");
                mapMessage.setString("templateParam","{\"number\":\""+randomNumeric+"\"}");
                return mapMessage;
            }
        });
    }

    @Override
    public void add(User user, String smscode) {
        //1:判断验证码是否正确
        String code = (String) redisTemplate.boundValueOps(user.getPhone()).get();
        if(null != code && code.equals(smscode)){
            //验证码是正确的
            //保存
            user.setCreated(new Date());
            user.setUpdated(new Date());
            userDao.insertSelective(user);
        }else{
            //错误
            throw new RuntimeException("验证码不正确");
        }
    }

    /**
     * 查询所有用户
     * @return
     */
    @Override
    public List<User> findUser() {
        return userDao.selectByExample(null);
    }

    /**
     * 分页查询
     * @param page
     * @param rows
     * @param user
     * @return
     */
    @Override
    public PageResult search(Integer page, Integer rows, User user) {
        PageHelper.startPage(page,rows);
        //排序
        PageHelper.orderBy("id Desc");
         //创建用户条件查询
        UserQuery userQuery = new UserQuery();
        UserQuery.Criteria criteria = userQuery.createCriteria();
        if(null!=user.getName() && !"".equals(user.getName().trim())){
            criteria.andNameLike("%"+user.getName().trim()+"%");
        }
        //执行查询
        Page<User> u = (Page<User>) userDao.selectByExample(userQuery);
        return new PageResult(u.getTotal(),u.getResult());
    }

    /**
     * 显示用户个数
     * @return
     */
    @Override
    public Integer showUserCount() {
        return userDao.countByExample(null);
    }

    /**
     * 冻结用户
     * @param ids
     */
    @Override
    public void freeze(Long[] ids) {
        User user = new User();
        user.setPassword("freeze");
        //遍历选择id
        for (Long id : ids) {
            user.setId(id);
            //修改操作
            userDao.updateByPrimaryKeySelective(user);
        }
    }

}
