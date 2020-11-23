# 功能说明
+ 适用采用mina做web应用服务器，解决session共享的问题
+ 是基于mina框架基础之上实现session共享
+ 会话共享缓存采用的是memcached，你也可以调整为redis或者本地缓存都可以
+ 依赖包lib目录下，自行导入即可,主要依赖包：
mina-core-2.0.9.jar
xmemcached-1.3.5.jar
servlet-api.jar
+ HttpServletRequest 和 HttpServletResponse可以替换成你自己的实现，只要满足servelt规范即可


---------------
# 使用方式

是基于filter拦截器原理，配置filter拦截器，拦截终端请求，实现会话共享

    <bean id="sessionManager" class="org.mina.web.session.manager.MemcachedSessionManager">
          <property name="memcachedClient" ref="memcachedClient" />
   	</bean>
   
	<bean id="sessionFilter" class="org.mina.web.session.manager.MemcachedSessionFilter">
       <property name="sessionManager" ref="sessionManager"/>
    </bean>

