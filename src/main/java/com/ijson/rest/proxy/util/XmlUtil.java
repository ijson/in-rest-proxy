package com.ijson.rest.proxy.util;

import com.ijson.rest.proxy.annotation.CDATA;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.core.util.QuickWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.*;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.lang.reflect.Field;

/**
 * 输出xml和解析xml的工具类
 */
@Slf4j
public class XmlUtil {


    protected static String PREFIX_CDATA = "<![CDATA[";
    protected static String SUFFIX_CDATA = "]]>";


    /**
     * 初始化XStream
     * 可支持某一字段可以加入CDATA标签
     * 如果需要某一字段使用原文
     * <code> 就需要在String类型的text的头加上&quot;&lt;![CDATA[&quot;和结尾处加上&quot;]]&gt;&quot;标签， </code>
     * 以供XStream输出时进行识别
     *
     * @param isAddCdata 是否支持CDATA标签
     * @return XStream instance
     */

    public static XStream initXstream(boolean isAddCdata) {
        XStream xstream;
        if (isAddCdata) {
            xstream = new XStream(
                    new XppDriver() {
                        @Override
                        public HierarchicalStreamWriter createWriter(Writer out) {
                            return new PrettyPrintWriter(out) {
                                //修复__问题
                                @Override
                                public String encodeNode(String name) {
                                    return name;
                                }

                                @Override
                                protected void writeText(QuickWriter writer, String text) {
                                    if (text.startsWith(PREFIX_CDATA) && text.endsWith(SUFFIX_CDATA)) {
                                        writer.write(text);
                                    } else {
                                        super.writeText(writer, text);
                                    }
                                }
                            };
                        }
                    }
            );
        } else {
            xstream = new XStream(new XppDriver(new XmlFriendlyNameCoder("-_", "_")));
        }
        return xstream;
    }


    /**
     * java 转换成xml
     *
     * @param obj 对象实例
     * @return String xml字符串
     */
    public static String toXml(Object obj) {
        XStream xstream = initXstream(true);// new XStream(new XppDriver(new XmlFriendlyNameCoder("-_", "_")));

        // XStream xstream = new XStream(new XppDriver(new XmlFriendlyReplacer("-_", "_")));
        // XStream xstream=new XStream(new DomDriver()); //直接用jaxp dom来解释
        // XStream xstream=new XStream(new DomDriver("utf-8")); //指定编码解析器,直接用jaxp dom来解释
        ////如果没有这句，xml中的根元素会是<包.类名>；或者说：注解根本就没生效，所以的元素名就是类的属性

        Class tClass = obj.getClass();
        Field[] fields = tClass.getDeclaredFields();
        for (Field field : fields) {
            if (field.getAnnotation(CDATA.class) != null) {
                field.setAccessible(true);
                try {
                    if (field.get(obj) != null) {
                        field.set(obj, "<![CDATA[" + field.get(obj) + "]]>");
                    }
                } catch (IllegalAccessException e) {
                    log.error("CDATA {}", e);
                }
            }
        }

        xstream.processAnnotations(obj.getClass()); //通过注解方式的，一定要有这句话
        return xstream.toXML(obj);
    }


    /**
     * 调用的方法实例：PersonBean person=XmlUtil.toBean(xmlStr, PersonBean.class);
     * 将传入xml文本转换成Java对象
     *
     * @param xmlStr 要转成对象的xml
     * @param cls    xml对应的class类
     * @return T   xml对应的class类的实例对象
     */


    public static <T> T toBean(String xmlStr, Class<T> cls) {
        //注意：不是new Xstream(); 否则报错：java.lang.NoClassDefFoundError: org/xmlpull/v1/XmlPullParserFactory
        XStream xstream = initXstream(true);
        xstream.processAnnotations(cls);
        T obj = (T) xstream.fromXML(xmlStr);
        return obj;
    }

    /**
     * 写到xml文件中去
     *
     * @param obj      对象
     * @param absPath  绝对路径
     * @param fileName 文件名
     * @return boolean
     */
    public static boolean toXmlFile(Object obj, String absPath, String fileName) {
        String strXml = toXml(obj);
        String filePath = absPath + fileName;
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                log.error("error:{}", e);
                return false;
            }
        }// end if
        OutputStream ous = null;
        try {
            ous = new FileOutputStream(file);
            ous.write(strXml.getBytes());
            ous.flush();
        } catch (Exception e1) {
            log.error("error:{}", e1);
            return false;
        } finally {
            if (ous != null) {
                try {
                    ous.close();
                } catch (IOException e) {
                    log.error("error:{}", e);
                }
            }
        }
        return true;
    }


    /**
     *  从xml文件读取报文
     * @param absPath 绝对路径
     * @param fileName 文件名
     * @param cls 类
     * @param <T> 对象
     * @return 转换后的对象
     * @throws Exception
     */
    public static <T> T toBeanFromFile(String absPath, String fileName, Class<T> cls) throws Exception {
        String filePath = absPath + fileName;
        InputStream ins = null;
        try {
            ins = new FileInputStream(new File(filePath));
        } catch (Exception e) {
            throw new Exception("读{" + filePath + "}文件失败！", e);
        }

        XStream xstream = initXstream(true);
        xstream.processAnnotations(cls);
        T obj = null;
        try {
            obj = (T) xstream.fromXML(ins);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            throw new Exception("解析{" + filePath + "}文件失败！", e);
        }
        ins.close();
        return obj;
    }

}
