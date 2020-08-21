# meiot-message

消息服务  meiot-message-test

### 2020/5/6日数据库增加字段
在系统消息表添加项目ID 并且赋值0

alter table system_message add project_id int(1);

update system_message set project_id=0;

alter table system_message modify project_id int(1) default 0;