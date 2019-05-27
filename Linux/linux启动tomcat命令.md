```shell
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

