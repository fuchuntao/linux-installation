```shell
tar -zxvf java.tar.gz
tar xvf FileName.tar
启动tomcat
./startup.sh
./catalina.sh run
停止tomcat
./shutdown.sh
查看tomcat进程
ps -ef|grep tomcat
强制结束进程
kill -9 进程号
查看log日志
tail -200f catalina.out

win10 的查看端口的命令
netstat -ano
查看指定端口的占用情况
netstat -aon|findstr "49157"
解除占用的pid
task kill /pid 4136 -t -f

```

![1564019681001](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1564019681001.png)

![1564045648849](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1564045648849.png)

安装netatat

```java
yum install net-tools
```







安装redis 

![1566711592672](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1566711592672.png)

```java

tar -zxvf redis-4.0.10.tar.gz 
cd redis-4.0.10/ 
make
find / -name redis-cli
cd 路径
//后台启动
./redis-server  ../redis.conf &
```



修改中文乱码问题

docker exec -it <contrainerId> env LANG=C.UTF-8 /bin/bash

导出数据备份

docker exec -it <contrainerId>mysqldump -uroot -proot h3cdb> /home/bak/demo.sql

修改 docker 时间

cp /usr/share/zoneinfo/Asia/Shanghai    /etc/localtime