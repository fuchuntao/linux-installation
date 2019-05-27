## win 10 64bit安装开发软件

### 1.win 10 64bit 安装redis 及Redis desktop manager

链接：<https://www.cnblogs.com/cxxjohnson/p/8945920.html>

#### 1.1win 10 64bit 安装redis命令

```java
1.安装目录下打开dos窗口，启动服务端
redis-server.exe redis.windows.conf
2.这时候另启一个cmd窗口，原来的不要关闭，不然就无法访问服务端了。启动客户端
redis-cli.exe -h 127.0.0.1 -p 6379
3.将 redis 服务器启动放入 windows 服务中，使其开机自动启动
redis-server --service-install redis.windows.conf --loglevel verbose
/**
* 开启服务：redis-server --service-start
* 停止服务：redis-server --service-stop
* 卸载服务：redis-server --service-uninstall
**/
4. 清除缓存：【https://blog.csdn.net/luuvyjune/article/details/81016295】
	4.1 本地清除，直接打开redis-cli.exe （win窗口操作redis的set和get）
		1.查看所有键值的缓存
		keys * 
        2.清除所有缓存
        flushall
	4.2 本地远程连接清除缓存
	redis-cli.exe -h 192.168.10.10 -p 6379
```

#### 2.win 10 64bit安装RabbitMQ

链接：<https://blog.csdn.net/weixin_39735923/article/details/79288578>

1.启动rabbitmq-server服务

```java
./rabbitmq-server.bat
```

2.查看状态

```java
./rabbitmqctl.bat status
```

启动服务链接：<https://blog.csdn.net/zxl646801924/article/details/80435231>

