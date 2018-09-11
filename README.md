# validatorDemo
对valdate校验器的基本实现,Spring MVC校验器的扩展,可进行对方法参数单个校验 实例

对valdate校验器的基本实现,Spring MVC校验器的扩展（这里只实现json的处理）,可进行对方法参数单个校验 下面是简单的实现：

```java

    public Map<String, Object> add(@Valid User user) {
        System.out.println(user.getName());
        System.out.println(user.getQq());
        System.out.println(user.getIdCard());
        return successData();
    }


    @Validated
    public class User1Controller extends SupportController {

        @RequestMapping("set")
        public Map<String, Object> set(@MsgCode(100101) @QQ String qq) {
            System.out.println(qq);
            return successData();
        }

    }


```




在mvc-servlet-context.xml里面加入一下支持
```xml
  <!-- 注解式AOP -->
     <!--<aop:aspectj-autoproxy />-->

     <!-- 代理模式用CGLIB，针对类的代理 -->
     <!--<aop:aspectj-autoproxy proxy-target-class="true" />-->

     <!--<bean class="net.zz.validator.ZZValidate"/>-->
    <!--注册方法验证的后处理器-->
    <bean class="com.egzosn.validator.validator.handler.MethodValidationPostProcessor"/>

```
