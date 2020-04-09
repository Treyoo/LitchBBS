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

###安装Elasticsearch v6.8.6
下载地址：https://www.elastic.co/cn/downloads/past-releases/elasticsearch-6-8-6
解压，设置%es安装目录%/bin到环境变量
编辑%es安装目录%/config/elasticsearch.yml文件，设置cluster.name='litchi-bbs'
终端执行`elasticsearch`命令启动




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



[下载地址是]: https://www.elastic.co/cn/downloads/past-releases/elasticsearch-6-8-6

[]: https://www.elastic.co/cn/downloads/past-releases/elasticsearch-6-8-6

### 4.发布帖子并更新到Elasticsearch，下一次启动应用报错
Error creating bean with name 'eventConsumer': Unsatisfied dependency expressed through field 'elasticsearchService';
…………
Caused by: java.lang.IllegalArgumentException: mapper [createTime] of different type, current_type [long], merged_type [date]
看字面意思是类型不匹配，可以看出是从eventConsumer里报出来的，里面调用elasticsearchService出错。
可能和我上一次粗暴地Ctrl+C关闭es和kafka有关？
知识盲区，不知道怎么优雅地解决。
粗暴解决：既然是从eventConsumer里报出来的，那就是说Kafka的publich_discuss里还残留数据没被消费，去手动把它删了！
手动删除kafka topic
1.修改配置文件config/server.properties，添加
`auto.create.topics.enable=false`
`delete.topic.enable=true`
2.重启kafka
3.执行`bin/kafka-topics.sh --delete --zookeeper 127.0.0.1:2181 --topic publish_discuss`命令删除topic
4.删除kafka存储目录（server.properties文件log.dirs配置，这里是"/tmp/kafka-logs"）相关topic的数据目录
5.执行`bin/kafka-topics.sh --list --bootstrap-server localhost:9092`看删除topic成功没有
6.恢复配置文件config/server.properties并重启kafka(最好重启一下电脑！！)
参考:https://www.jianshu.com/p/423e92e1e1fd



