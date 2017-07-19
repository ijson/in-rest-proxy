package com.ijson.rest;

import com.ijson.rest.proxy.annotation.CDATA;
import com.ijson.rest.proxy.annotation.INField;
import com.ijson.rest.proxy.exception.RestProxyINFieldException;
import com.ijson.rest.proxy.util.XmlUtil;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.UUID;

/**
 * Created by cuiyongxu on 17/7/15.
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-rest-proxy.xml")
public class BaseTest {

    @Test
    public void xmlUtil() {
        User user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setAge("11");
        user.setName("张三");
        String str = XmlUtil.toXml(user);
        System.out.println(str);
    }

    @Test
    public void xmlUtil2() {
        User user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setAge("11");
        user.setName("张三");
        user.setMc_id("333333333");
        String str = XmlUtil.toXml(user);
        System.out.println(str);

        User user2 = XmlUtil.toBean(str, User.class);

        System.out.println(user2.getId() + ":" + user2.getName() + ":" + user2.getAge());

    }


    @Test
    public void xmlUtil3() {
        User user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setName("张三");
        user.setMc_id("333333333");
        try {
            Field[] fields = user.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                INField notNull = field.getAnnotation(INField.class);
                if (notNull != null) {
                    boolean required = notNull.required();
                    Object value = field.get(user);
                    //是否必填
                    if (required && value == null) {
                        throw new RestProxyINFieldException(-1, MessageFormat.format(notNull.requiredMessage(), field.getName()));
                    }
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Data
    @XStreamAlias("xml")
    static class User {
        @INField(name = "id", desc = "主键", required = true, remark = "这是一个比较长的描述信息")
        private String id;
        @CDATA
        @INField(name = "name", desc = "名称", required = true, remark = "这是名称,可以写用户的中文名字,支持中文和英文,但是长度有限制")
        private String name;
        @INField(name = "age", desc = "年龄", required = true, remark = "这是年龄")
        private String age;
        @CDATA
        @INField(name = "mc_id", desc = "mcid")
        private String mc_id;
    }
}
