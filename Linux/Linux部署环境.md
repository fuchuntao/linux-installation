## Linux服务器搭建环境

### 常用命令：

```shell
#linux常用命令：
#查看服务
ps -ef|grep redis
lsof -i :6379
netstat -tnlp
#查询历史记录命令
history | grep redis
#查找文件
find / -name redis-cli
#解压tar,和tar.gz
tar -zxvf java.tar.gz
tar xvf FileName.tar
#查看进程
ps -ef|grep tomcat
#结束进程
kill -9 pid
#查看log日志
tail -300f info.log
#启动tomcat
./startup.sh
#停止tomcat
./shtdown.sh

#防火墙开放数据库端口
firewall-cmd --zone=public --add-port=8080/tcp --permanent
#查看已经开放的端口
firewall-cmd --list-ports
#查看防火墙状态
systemctl status firewalld
#开启防火墙
systemctl start firewalld
#关闭防火墙
systemctl stop firewalld
#查看当前firewall状态
firewall-cmd --state
#重启firewall
firewall-cmd --reload



```



关于centos 7刚安装后无法联网解决：<https://blog.csdn.net/weixin_34890916/article/details/80390365>

关于centos 7时间设置问题：<https://blog.csdn.net/robertsong2004/article/details/42268701>

### 1、JDK

#### 1.1下载jdk的rpm包

##### 1.下载地址：

<https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html>

##### 2.将jdk压缩包放到 /usr/local目录下

##### 3.进入压缩目录输入

```shell
rpm -ivh jdk-8u101-linux-x64.rpm
```

##### 4.查看jdk版本（jdk默认安装到 /usr/java 下）

```shell
java -version
```

##### 5.配置环境变量

```shell
vi /etc/profile
```

按 i 键编辑，编辑之后 按 Esc 键退出编辑状态，接着分别按下 ：w q ! 键保存退出

```tex
JAVA_HOME=/usr/java/jdk1.8.0_201
JRE_HOME=/usr/java/jdk1.8.0_201/jre
PATH=$PATH:$JAVA_HOME/bin:$JRE_HOME/bin
CLASSPATH=.:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar:$JRE_HOME/lib
export JAVA_HOME JRE_HOME PATH CLASSPATH
```

##### 6.刷新配置

```shell
source /etc/profile   //使修改立即生效 
```

##### 7.查看配置是否生效

```shell
echo $PATH
```

### 2、Maven

安装步骤：

```xml
<!--配置maven路径-->
<localRepository>F:/Maven/repository</localRepository>
<!--阿里云镜像-->	
<mirror>
      <id>alimaven</id>
      <name>aliyun maven</name>
      <url>http://maven.aliyun.com/nexus/content/groups/public/</url>
      <mirrorOf>central</mirrorOf>        
    </mirror>
  </mirrors>
```







### 3、Tomcat

##### 1.下载网站：<http://tomcat.apache.org/>下载  tar.gz  的版本 

![](C:\Users\Administrator\Desktop\安装\linux-installation\图片\1.png)

##### 2.创建tomcat文件夹

```shell
mkdir/usr/local/tomcat
```

##### 3.上传解压下载好的安装包至 tomcat 文件夹中,并解压

```shell
tar -zxv -f 刚刚上传的tomcat文件名称
```

##### 4.启动测试 tomcat 是否安装成功

```shell
./startup.sh
```

##### 5.防火墙中配置开放 8080端口

```shell
防火墙开放数据库端口
firewall-cmd --zone=public --add-port=8080/tcp --permanent
查看已经开放的端口
firewall-cmd --list-ports
查看防火墙状态
systemctl status firewalld
开启防火墙
systemctl start firewalld
关闭防火墙
systemctl stop firewalld
查看当前firewall状态
firewall-cmd --state
重启firewall
firewall-cmd --reload
禁止开机启动
systemctl disable firewalld.service

firewall-cmd --state                           ##查看防火墙状态，是否是running
firewall-cmd --reload                          ##重新载入配置，比如添加规则之后，需要执行此命令
firewall-cmd --get-zones                       ##列出支持的zone
firewall-cmd --get-services                    ##列出支持的服务，在列表中的服务是放行的
firewall-cmd --query-service ftp               ##查看ftp服务是否支持，返回yes或者no
firewall-cmd --add-service=ftp                 ##临时开放ftp服务
firewall-cmd --add-service=ftp --permanent     ##永久开放ftp服务
firewall-cmd --remove-service=ftp --permanent  ##永久移除ftp服务
firewall-cmd --add-port=80/tcp --permanent     ##永久添加80端口 
firewall-cmd --remove-port=80/tcp --permanent  ##永久移除80端口
firewall-cmd --list-ports                      ##查看已经开放的端口
iptables -L -n                                 ##查看规则，这个命令是和iptables的相同的
man firewall-cmd                               ##查看帮助
```

**命令解析：**

--zone #作用域

--add-port=8080/tcp #添加端口，格式为：端口/通讯协议

--permanent #永久生效，没有此参数设置，重启后会失效。

##### 6.重启防火墙，输入命令

```shell
firewall-cmd --reload
```

### 4、Mysql

##### 命令：

```shell
#查看数据库
show databases
#选择数据库
use 数据库名
#显示表
show tables
#设置表的id变回从1开始自增了
truncate table 表名

#mysql查看表结构命令
desc 表名
#mysql的DDL详细信息
show create table 表名 \G
#查询所有用户
select * from mysql.user;
#删除用户
drop user zhangsan@'%';

#mysql修改表字段
ALTER TABLE feedback MODIFY COLUMN title varchar(256) DEFAULT NULL COMMENT '标题',
#mysql添加表字段
ALTER TABLE `scheme_dimension` add `lang_type` tinyint(3) DEFAULT '0' COMMENT '0:中文  1:英文';
#改文件权限
chmod 644 my.cnf

```



##### 1.安装链接：

<https://blog.csdn.net/z13615480737/article/details/78906598>

```shell
---------mysql的基本操作-----------------------------
mysql连接
mysql -h192.168.40.150 -umysql_master -p --port=3307

停掉mysql进程：pkill -9 mysql
进入目录：cd /usr/local/src/
下载mysql的rpm包：wget http://repo.mysql.com/mysql57-community-release-el7-8.noarch.rpm  解压mysql的压缩包： rpm -ivh mysql57-community-release-el7-8.noarch.rpm 
安装mysql： yum -y install mysql-server
进入my.cnf配置文件：vim /etc/my.cnf
启动mysql: service mysqld restart
重置密码：grep "password" /var/log/mysqld.log
登录mysql: mysql -u root -p
退出： quit
修改密码：
SET PASSWORD = PASSWORD('YYBrhr_2018');
ALTER USER 'root'@'localhost' PASSWORD EXPIRE NEVER;
flush privileges;
远程连接授权： 
GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' IDENTIFIED BY 'YYBrhr_2018' WITH GRANT OPTION;

--------------给数据库单独创建用户授予权限-------------
1.先进入mysql容器，
mysql -uroot -p123456

2.进入要分配用户的数据库，或者创建
create database test;
use test;

3.进入数据库后创建用户
create user 'testuser'@'localhost' identified by '123456';
其中username表示用户名，password表示密码。localhost表示只能本地访问，可以通过修改localhost进行访问控制，包括输入指定IP,修改成%达到不限IP访问
例如：
create user 'testuser'@'%' identified by '123456';
create user 'testuser'@'192.168.40.170' identified by '123456';

4.分配权限
授予所有权限
grant all privileges on test.* to 'testuser'@'%';
分配部分权限
grant select,insert on test to 'testuser'@'%';
5.刷新权限
flush privileges;






-------------配置文件位置--------------------------------
配置文件：/etc/my.cnf 
日志文件：/var/log/var/log/mysqld.log 
服务启动脚本：/usr/lib/systemd/system/mysqld.service 
socket文件：/var/run/mysqld/mysqld.pid


==============================================================
创建用户
CREATE USER 'mysql_master'@'%' IDENTIFIED WITH mysql_native_password BY 'root';


https://blog.csdn.net/qq_26462567/article/details/86713638
更改远程链接授权
grant all privileges on *.* to 'root'@'%';
刷新权限
flush privileges; 
alter user 'root'@'%' identified with mysql_native_password by 'root';
```



设置编码格式为 UTF-8 

链接：<http://www.cnblogs.com/yugb/p/9789830.html>

```shell
登录mysql:mysql>show variables like 'character_set_%';
进入配置文件修改配置内容，执行命令：vi /etc/my.cnf
修改配置文件的内容，在[mysqld]结束位置添加：character_set_server=utf8
停止命令：systemctl stop mysqld.service
启动命令：systemctl start mysqld.service
```

### 5、redis安装

Redis下载地址： https://redis.io/download

启动redis  ./redis-server ../redis.conf &



### 6、Docker

##### 命令：

```shell
#docker重启容器服务自启
docker update --restart=always 容器id

#检索
docker search mysql
#拉取
docker pull mysql:5.5
#重启docker
systemctl restart docker
#启动docker
systemctl start docker
#停止docker
systemctl stop docker
#查看docker状态
systemctl status docker

#删除docker容器
docker rm id|name

#删除docker镜像
docker rmi id|name

#查看镜像
docker images
#查看启动的镜像
docker ps -a

#启动mysql镜像
docker run --name mysql8.0 -p 3307:3306 -e MYSQL_ROOT_PASSWORD=root -d mysql:8.0
docker start redis
docker stop redis
       
#启动redis镜像   
docker run -d  --name redis1  -p 6379:6379  redis --requirepass "redis"
    
#启动mongodb镜像
docker run --name mongo -p 27017:27017 -v ~/docker-data/mongo:/data/db -e MONGO_INITDB_ROOT_USERNAME=admin -e MONGO_INITDB_ROOT_PASSWORD=admin -d mongo

#将容器 f47fc5168843 的/etc/mysql/my.cnf目录拷贝到主机的/opt/目录中
docker cp f47fc5168843:/etc/mysql/my.cnf /opt/

#将主机my.cnf目录拷贝到容器 f47fc5168843 的/etc/mysql目录下
docker cp my.cnf f47fc5168843:/etc/mysql

#修改中文乱码问题
docker exec -it <contrainerId> env LANG=C.UTF-8 /bin/bash

#导出数据备份
docker exec -it <contrainerId>mysqldump -uroot -proot h3cdb> /home/bak/demo.sql

#导入sql文件
source /home/SQL/mmall.sql

#修改 docker 时间
cp /usr/share/zoneinfo/Asia/Shanghai    /etc/localtime
```



## Windows部署环境

1、JDK

```shell
#环境变量
path=%JAVA_HOME%\bin;
JAVA_HOME=C:\Program Files\Java\jdk1.8.0_60
M2_HOME=C:\Program Files\Java\apache-maven-3.5.4
```











