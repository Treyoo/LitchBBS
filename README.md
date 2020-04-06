# LitchBBS

网页论坛，使用SpringBoot,MyBatis,Thymeleaf

##开发环境依赖
以下示例为Ubuntu系统下
### 安装MySql
sudo apt-get install mysql-server
1.设置root用户密码为root
2.新建数据库bbs:mysql命令行执行CREATE DATABASE bbs;

### 安装Redis
sudo apt-get install redis-server
不设置密码

### 安装Maven
下载压缩包，下载地址：https://maven.apache.org/
解压，配置/bin目录到环境变量(/etc/profile)中

### 安装Kafka
下载压缩包，下载地址：http://kafka.apache.org/
解压、启动
先启动ZooKeeper:> bin/zookeeper-server-start.sh config/zookeeper.properties
再启动Kafka-Server:> bin/kafka-server-start.sh config/server.properties





## 开发遇到的一些问题
### 1.ONLY_FULL_GROUP_BY导致获取会话列表时mysql报错
Error Code: 1055.Expression #1 of SELECT list is not in GROUP BY clause and contains nonaggregated column 'id'
因为MySQL5.7版本后默认设置了sql_model=only_full_group_by，限制了select的字段必须全部出现在gruop by中。
mysql命令行执行以下命令即可（该命令去除了全局的only_full_group_by设置）。
set global sql_mode='STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';
参考：https://www.cnblogs.com/gjmhome/p/11337329.html

发现上面解决办法重启MySql后会失效，需要重新执行命令进行设置，比较麻烦。修改配置文件即可解决重启失效问题。
(1)修改配置文件。/etc/mysql/mysql.conf.d/mysqld.cnf，添加一行
  sql_mode=STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION
  (必须是mysqld.cnf配置文件，在my.cnf中添加会出现无法启动mysql服务！！)
(2)重启mysql。sudo service mysql restart
(3)验证是否生效。mysql命令行执行select @@sql_mode;查看结果还有无ONLY_FULL_GROUP_BY

### 2.启动报错Topic(s) [comment, follow, like] is/are not present and missingTopicsFatal is true
报错原因： 消费监听接口监听的主题不存在时，默认会报错
解决办法：办法1：在application.properties配置文件中将listener的属性missingTopicsFatal设置为false
spring.kafka.listener.missing-topics-fatal=false。
办法2：在终端执行命令手动创建缺失的Topic再启动应用。
e.g. bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic comment

### 3.启动Elasticsearch报错
启动应用ES就报错：NoNodeAvailableException[None of the configured nodes are available: [{#transport#-1}{KrOFibneQ76ycUjBC0Y3Qw}{127.0.0.1}{127.0.0.1:9300}] 
通过搜索引擎知道报这个错的常见原因：
(1)ES没启动.
(2)安装的ES版本与Spring-data-elasticsearch jar包版本不兼容.
(3)cluster.name不匹配.
鼓捣了半天以为是ES没配置好和版本装错了，然而还是报错！
最后看到ElasticsearchProperties.java中有个属性名为clusterName默认值为"elasricsearch",
而我的application.properties文件中设置的是spring.data.elasticsearch.cluster=litchi-bbs！
之前比对了半天没发现集群名字写错，想不到是属性名写错了！！
所以最后看到ElasticsearchProperties使用了默认的clusterName，我配置的属性名根本没起作用！！
修改spring.data.elasticsearch.cluster=litchi-bbs为spring.data.elasticsearch.cluster-name=litchi-bbsyanwenz

