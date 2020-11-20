package com.udeam.edu.factory;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 工厂Bean
 */
public class BeanFactorys {

    private final static Map<String, Object> iocMap = new HashMap<>();

    static {
        // 1 读取解析beans.xml  通过反射技术,生产bean对象,并将其存在map中

        InputStream resourceAsStream = BeanFactorys.class.getClassLoader().getResourceAsStream("beans.xml");

        //得到一个 文档对象
        try {
            Document read = new SAXReader().read(resourceAsStream);
            //获取跟对象
            Element rootElement = read.getRootElement();

            /**
             * xpath表达式 用法
             *   // 从匹配选择的当前节点选择文档中的节点,而不考虑他们的位置
             *   / 从根节点获取
             *  . 选取当前节点
             *  .. 选取当前节点的父节点
             *  @ 选取属性
             *
             */
            // //表示读取任意位置的bean标签
            List<Element> list = rootElement.selectNodes("//bean");

            if (Objects.isNull(list) || list.size() == 0) {
                throw new RuntimeException("无此bean标签");
            }

            list.forEach(x -> {
                //获取Id
                String id = x.attributeValue("id"); //accountDao
                //获取权限定命名
                String clasz = x.attributeValue("class"); //com.udeam.edu.dao.impl.JdbcAccountDaoImpl
                System.out.println(id + " ---> " + clasz);
                //通过反射创建对象
                try {
                    Object o = Class.forName(clasz).newInstance();
                    //存入ioc容器
                    iocMap.put(id, o);

                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }


            });

            //获取所有properties 属性 并且set设置值
            List<Element> prList = rootElement.selectNodes("//property");

            prList.forEach(y -> {
                //获取 property 属性name值
                String name = y.attributeValue("name"); //   <property name="setAccountDao" ref = "accountDao"></property>
                String ref = y.attributeValue("ref");
                //获取父节点id
                Element parent = y.getParent();
                //获取父节点id
                String id = parent.attributeValue("id");
                //维护对象依赖关系
                Object o = iocMap.get(id);
                //找到所有方法
                Method[] methods = o.getClass().getMethods();
                for (int i = 0; i < methods.length; i++) {
                    //方法就是set属性反方
                    if (methods[i].getName().equalsIgnoreCase("set" + name)) {
                        try {
                            //set设置对象
                            methods[i].invoke(o, iocMap.get(ref));
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }

                        //set之后重新赋值
                        iocMap.put(id,o);
                    }


                }


            });

        } catch (DocumentException e) {
            e.printStackTrace();
        }


        // 2 对外提供获取示例对象接口


    }

    /**
     * 对外提供获取bean接口
     *
     * @param id
     * @return
     */
    public static Object getBean(String id) {
        //System.out.println("icoMap === " + iocMap);
        //System.out.println("icoMap = " + iocMap.get(id));
        return iocMap.get(id);
    }
}
