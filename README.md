# LitchBBS

网页论坛，使用SpringBoot,MyBatis,Thymeleaf

##环境依赖
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
-获取会话列表时mysql报错
Error Code: 1055.Expression #1 of SELECT list is not in GROUP BY clause and contains nonaggregated column 'id'
是因为MySQL5.7版本后默认设置了sql_model=only_full_group_by，限制了select的字段必须全部出现在gruop by中。
mysql命令行执行以下命令即可（该命令去除了全局的only_full_group_by设置）。
set global sql_mode='STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';
参考：https://www.cnblogs.com/gjmhome/p/11337329.html
