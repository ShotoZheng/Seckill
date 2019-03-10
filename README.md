# Seckill
Java高并发秒杀系统 基于 SSM + Redis + bootstrap + Js 实现

## 项目使用技术
Spring + SpringMVC + MyBatis + Redis + bootstarp + Js

## 系统实现过程：

### 数据库：
创建两个数据库表，秒杀库存表和秒杀成功明细表。

### DAO层：
1. 编写MyBatis全局配置文件，并设计DAO接口和实现对应的映射文件；
2. 编写Spring的dao层配置文件，配置C3P0连接池和整合MyBatis等。

### Service层：
1. 设计Service层接口并进行实现。存在如下两个重点方法：

   * exportSeckillUrl：该方法用于暴露一些时间信息和接口地址，暴露接口地址是防止有用户提前模拟秒杀url地址。接口地址即为Spring的DigestUtils.md5DigestAsHex方法利用盐值（无规则字符串）+ 秒杀商品ID所生成MD5值；
   
   * executeSeckill：该方法用于执行秒杀操作。包括校验MD5值、插入秒杀成功明细信息到对应表中和执行减库存操作等。
   
2. 编写Spring的Service层配置文件，配置事务管理器和注解的声明式事务等。

### DTO层：
1. Exposer：封装了exportSeckillUrl方法执行的结果，即一些暴露信息，以方便前端页面进行交互逻辑处理。比如根据Exposer对象的值来执行页面的倒计时操作、显示秒杀结束和显示执行秒杀按钮；
2. SeckillExecution：封装了executeSeckill方法执行的结果，包含了秒杀成功或重复秒杀等信息；
3. SeckillResult：对SeckillExecution的进一步封装，以方便前端页面进行交互逻辑处理。

### WEB层：
1. 处理前端链接的控制跳转和视图解析渲染等，Service层方法更上一层的抽取；
2. 编写Spring的web层配置文件，也就是SpringMVC的配置文件。配置注解驱动和视图解析等；
3. 配置web.xml配置文件，配置DispatcherServlet加载Spring相关的配置。

### 前端页面：
1. 使用bootstrap编写页面，包括秒杀商品列表页面和秒杀商品详情页面。后者包含一个隐藏的弹出层，用于用户输入手机号码；
2. JS处理交互逻辑，如根据DTO数据传输对象信息来显示不能的页面效果。浏览器是否有正确的手机号码cookie值来决定是否显示弹出层、校验输入的手机号码、根据Exposer对象决定是否显示倒计时或秒杀操作或显示秒杀结束信息等、根据SeckillResult来决定显示是否重复秒杀。

### 高并发优化：
1. 将秒杀商品详情页等页面和静态资源如bootsrap,JQuery,css部署到CDN（内容分发网络，用于用户加速获取数据的系统）节点上，那么用户访问页面时是不会访问我们的服务器的，这样可以减轻服务器压力。
2. Redis缓存：

   * 在Service层执行暴露秒杀接口地址的方法时是会根据秒杀商品ID去从数据库获取秒杀商品对象。因为秒杀操作可能会非常的频繁，这里我们可以将秒杀商品对象存储在Redis缓存中以减少数据库访问，从而优化查询；
  
   * 进行以上秒杀对象的Redis缓存时需要进行对象的序列化与反序列化操作。其使用到了第三方的框架protostuff，这个序列化操作更快而且数据大小可以达到原来的1/5 - 1/10左右。
3. Spring声明式事务方法优化，在Service层的秒杀方法时，需要将秒杀成功信息插入到秒杀成功明细表中，然后再进行秒杀商品表的减库存操作。因为插入操作不具有索引条件，故在进行减库存的更新操作时才会获取行级锁，从而减少锁持有的时间，加快并发执行速度；
4. 使用存储过程（不太推荐），将秒杀成功信息插入到秒杀成功明细表和减库存的操作写成存储过程，从而也就避免了使用Spring声明式事务方法所带来的并发执行速度慢的缺点。

## 项目页面
### 秒杀列表
![](https://ws1.sinaimg.cn/large/005HAhlegy1g0y4jiiircj30vu0aogm1.jpg)
### 显示弹出层
![](https://ws1.sinaimg.cn/large/005HAhlegy1g0y4jnz33xj30uj075t8p.jpg)
### 开始秒杀
![](https://ws1.sinaimg.cn/large/005HAhlegy1g0y4to7lccj30vu05faa3.jpg)
### 秒杀成功
![](https://ws1.sinaimg.cn/large/005HAhlegy1g0y4jq6wpbj30vw05c74d.jpg)
### 重复秒杀
![](https://ws1.sinaimg.cn/large/005HAhlegy1g0y4jsgazbj30vy0580st.jpg)
### 秒杀结束
![](https://ws1.sinaimg.cn/large/005HAhlegy1g0y4kc31zfj30vy05haa7.jpg)
### 倒计时等待秒杀
![](https://ws1.sinaimg.cn/large/005HAhlegy1g0y4kjhevoj30w10583yu.jpg)
