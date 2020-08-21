package cn.meiot.config;


import cn.meiot.utils.QueueConstantUtil;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
public class MqConfig {

    /**
     * 发送消息
     * @return
     */
    @Bean
    public Queue saveMsg(){

        return new Queue(QueueConstantUtil.SEND_SMS_MSG);
    }

    /**
     * 删除验证码
     * @return
     */
    @Bean
    public Queue delCode(){

        return new Queue(QueueConstantUtil.DEL_SMS_CODE);
    }

//
//    /**
//     * 接收硬件上传的信息
//     * @return
//     */
//    @Bean
//    public Queue device_event(){
//        return new Queue(QueueConstantUtil.MQTT_DEVICE_QUEUE);
//    }


    // 报警
    @Bean
    public DirectExchange alarmSwitch2(){
        return new DirectExchange(QueueConstantUtil.MQTT_DEVICE_215);
    }

    @Bean
    public Queue alarmQueue2(){
        return new Queue(QueueConstantUtil.ALARM_QUEUE_TWO);
    }

    @Bean
    public Binding alarmQueueBindingalarmSwitch2(){
        return BindingBuilder.bind(alarmQueue2()).to(alarmSwitch2()).with(QueueConstantUtil.MQTT_DEVICE_STATUS_215);
    }

//    @Bean
//    public Binding switchStatusBinding() {
//        //链式写法: 用指定的路由键将队列绑定到交换机
//        return BindingBuilder.bind(DEVICE104()).to(switchStatus()).with(QueueConstantUtil.MQTT_DEVICE_STATUS_KEY);
//    }


    /**
     * 配置交换机实例
     *
     * @return
     */
    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(QueueConstantUtil.MQTT_DEVICE_EVNET);
    }

//    @Bean
//    public Binding binding() {
//        //链式写法: 用指定的路由键将队列绑定到交换机
//        return BindingBuilder.bind(device_event()).to(directExchange()).with(QueueConstantUtil.MQTT_DEVICE_KEY);
//    }

    /**
     * 接收硬件上传的信息
     * @return
     */
    @Bean
    public Queue sys_msg_queue(){
        return new Queue(QueueConstantUtil.SYS_MSG_QUEUE,true,false,false);
    }


    /**
     * 系统公告
     * @return
     */
    @Bean
    public Queue publishSystemBulletin(){
        return new Queue(QueueConstantUtil.PUBLISH_SYSTEM_BULLETIN);
    }


    /**
     * 主账号设备解绑
     * @return
     */
    @Bean
    public Queue unbindDevice(){
        return new Queue(QueueConstantUtil.UNBIND_DEVICE_NOTIFICATION);
    }

    /**
     * 删除验证码 Data change
     * @return
     */
    @Bean
    public Queue sendMail(){

        return new Queue(QueueConstantUtil.SEND_EMAIL_QUEUE);
    }

//    @Bean
//    public Queue DEVICE104() {
//        return new Queue(QueueConstantUtil.SWITCH_STATUS, true); // true表示持久化该队列
//    }


    /**
     * 配置交换机
     * @return
     */
    @Bean
    public DirectExchange switchStatus() {
        return new DirectExchange( QueueConstantUtil.MQTT_DEVICE_STATUS);
    }

    // 报警
    @Bean
    public DirectExchange alarmSwitch(){
        return new DirectExchange(QueueConstantUtil.MQTT_DEVICE_110);
    }
    @Bean
    public Queue alarmQueue(){
        return new Queue(QueueConstantUtil.ALARM_QUEUE,true);
    }
    @Bean
    public Binding alarmQueueBindingalarmSwitch(){
        return BindingBuilder.bind(alarmQueue()).to(alarmSwitch()).with(QueueConstantUtil.ALARM_MQTT_110);
    }

//    @Bean
//    public Binding switchStatusBinding() {
//        //链式写法: 用指定的路由键将队列绑定到交换机
//        return BindingBuilder.bind(DEVICE104()).to(switchStatus()).with(QueueConstantUtil.MQTT_DEVICE_STATUS_KEY);
//    }


    /**
     *1、 配置交换机实例
     *
     * @return
     */
    @Bean
    public DirectExchange push() {
        return new DirectExchange(QueueConstantUtil.WSS_CMD_21);
    }

    /**
     *
     */

//    //-----------------------------------------------------------------------------
//    @Bean
//    public Queue test() {
//        return new Queue("test0001", true); // true表示持久化该队列
//    }
//
//
//    /**
//     * 配置交换机
//     * @return
//     */
//    @Bean
//    public DirectExchange test01() {
//        return new DirectExchange( QueueConstantUtil.MQTT_DEVICE_STATUS);
//    }
//
//    @Bean
//    public Binding test02() {
//        //链式写法: 用指定的路由键将队列绑定到交换机
//        return BindingBuilder.bind(test()).to(test01()).with(QueueConstantUtil.MQTT_DEVICE_STATUS_KEY);
//    }


    /**
     * 故障工单队列
     * @return
     */
    @Bean
    public Queue troubleTicket(){
        return new Queue(QueueConstantUtil.TROUBLE_TICKET,true,false,false);
    }

    /**
     * 开关控制队列
     */
    @Bean
    public Queue switchControl(){
        return new Queue(QueueConstantUtil.ProjectMessage.SWITCH_CONTROL,true,false,false);
    }

    /**
     * 漏电自检
     * @return
     */
    @Bean
    public Queue EXAMINATION(){
        return new Queue(QueueConstantUtil.ProjectMessage.EXAMINATION,true,false,false);
    }

    /**
     * 功率限定
     * @return
     */
    @Bean
    public Queue LOADMAX_ONE(){
        return new Queue(QueueConstantUtil.ProjectMessage.LOADMAX_ONE,true,false,false);
    }

    /**
     * 功率限定统一设置消息
     * @return
     */
    @Bean
    public Queue LOADMAX_ALL(){
        return new Queue(QueueConstantUtil.ProjectMessage.LOADMAX_ALL,true,false,false);
    }
}
