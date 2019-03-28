## Linux服务器搭建环境

关于centos 7刚安装后无法联网解决：<https://blog.csdn.net/weixin_34890916/article/details/80390365>

关于centos 7时间设置问题：<https://blog.csdn.net/robertsong2004/article/details/42268701>

### 1、linux安装jdk

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

### 2、安装Tomcat

##### 1.下载网站：<http://tomcat.apache.org/>下载  tar.gz  的版本 

![](G:\Linux服务器搭建环境\图片\1.png)

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
```

**命令解析：**

--zone #作用域

--add-port=8080/tcp #添加端口，格式为：端口/通讯协议

--permanent #永久生效，没有此参数设置，重启后会失效。

##### 6.重启防火墙，输入命令

```shell
firewall-cmd --reload
```

### 3、安装Mysql

##### 1.安装链接：

<https://blog.csdn.net/z13615480737/article/details/78906598>

```shell
---------mysql的基本操作-----------------------------
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

-------------配置文件位置--------------------------------
配置文件：/etc/my.cnf 
日志文件：/var/log/var/log/mysqld.log 
服务启动脚本：/usr/lib/systemd/system/mysqld.service 
socket文件：/var/run/mysqld/mysqld.pid
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

### 4、redis安装

Redis下载地址： https://redis.io/download



