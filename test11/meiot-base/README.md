# meiot-base

基础服务

--
-- 2020/4/24日 因为版本号要根据10进制排序 增加排序字段
--

`ALTER TABLE firmware ADD COLUMN sort INT COMMENT '排序号';` 