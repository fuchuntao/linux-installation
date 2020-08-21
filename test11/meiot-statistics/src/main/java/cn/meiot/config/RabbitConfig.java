package cn.meiot.config;


import cn.meiot.utils.QueueConstantUtil;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class RabbitConfig {

    /**
     *统计个人
     *  参数1 name ：队列名
     *  参数2 durable ：是否持久化
     *  参数3 exclusive ：仅创建者可以使用的私有队列，断开后自动删除
     *  参数4 autoDelete : 当所有消费客户端连接断开后，是否自动删除队列
     * @return
     */
    @Bean
    public Queue statisticsMeterApp(){

        return new Queue(QueueConstantUtil.STATISTICS_METER_APP,true,false,false);
    }

    /**
     * 配置交换机实例
     *参数1 name ：交互器名
     * 参数2 durable ：是否持久化
     * 参数3 autoDelete ：当所有消费客户端连接断开后，是否自动删除队列
     * @return
     */
    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(QueueConstantUtil.MQTT_DEVICE_STATUS,true,false);
    }

    @Bean
    public Binding binding() {
        //链式写法: 用指定的路由键将队列绑定到交换机
        return BindingBuilder.bind(statisticsMeterApp()).to(directExchange()).with(QueueConstantUtil.MQTT_DEVICE_STATUS_KEY);
    }

    @Bean
    public Queue statisticsMonthMeterApp(){

        return new Queue(QueueConstantUtil.STATISTICS_DAY_QUEUE,true,false,false);
    }
    @Bean
    public Queue userStatistics(){

        return new Queue(QueueConstantUtil.MODIFY_USER_NOTIFICATION,true,false,false);
    }

    @Bean
    public Queue statisticsMonthMeterPc(){

        return new Queue(QueueConstantUtil.STATISTICS_DAY_QUEUE_PC,true,false,false);
    }



    /**
     * 交换机（fanout）
     * @return
     */
    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange(QueueConstantUtil.CHANGE_SWTICH_SN,true,false);
    }


    /**
     *替换开关改变电量
     */
    @Bean
    public Queue updateMeter(){

        return new Queue(QueueConstantUtil.UPTATE_METER,true,false,false);
    }

    @Bean
    public Binding updateMeterBinding() {
        return BindingBuilder.bind(updateMeter()).to(fanoutExchange());
    }




    /**
     *替换开关改变电流
     */
    @Bean
    public Queue updateLeakage(){

        return new Queue(QueueConstantUtil.UPTATE_LEAKAGE,true,false,false);
    }

    @Bean
    public Binding updateLeakageBinding() {
        return BindingBuilder.bind(updateLeakage()).to(fanoutExchange());
    }


    /**
     *替换开关改变温度
     */
    @Bean
    public Queue updateTemp(){

        return new Queue(QueueConstantUtil.UPTATE_TEMP,true,false,false);
    }

    @Bean
    public Binding updateTempBinding() {
        return BindingBuilder.bind(updateTemp()).to(fanoutExchange());
    }


    /**
     *替换开关改变负载
     */
    @Bean
    public Queue updatePower(){

        return new Queue(QueueConstantUtil.UPTATE_POWER,true,false,false);
    }

    @Bean
    public Binding updatePowerBinding() {
        return BindingBuilder.bind(updatePower()).to(fanoutExchange());
    }

    /**
     *
     * @Title: selectLoseMeter
     * @Description: 电量丢失
     * @param
     * @return: org.springframework.amqp.core.Queue
     */
    @Bean
    public Queue selectLoseMeter(){

        return new Queue(QueueConstantUtil.LOSE_METER,true,false,false);
    }



    /**
     *水表抄表记录
     */
    @Bean
    public Queue waterRecord(){

        return new Queue(QueueConstantUtil.WATER_RECORD,true,false,false);
    }


    /**
     *水表统计修改记录
     */
    @Bean
    public Queue updateWaterRecord(){

        return new Queue(QueueConstantUtil.UPDATE_WATER_RECORD,true,false,false);
    }



    /**
     *
     * 手动拉取水表数据队列
     */
    @Bean
    public Queue handUpdateWaterRecord(){

        return new Queue(QueueConstantUtil.HAND_WATER_RECORD,true,false,false);
    }




    /**
     *
     * 水表月表修改对列数据
     */
    @Bean
    public Queue monthWaterRecord(){

        return new Queue(QueueConstantUtil.YEAR_WATER_RECORD,true,false,false);
    }


    //电流

    /**
     *
     */
    @Bean
    public Queue tempStatics(){
        return new Queue(QueueConstantUtil.TEMP_STATISTICS_V2,true,false,false);
    }

    @Bean
    public DirectExchange tempDirectExchange() {
        return new DirectExchange(QueueConstantUtil.MQTT_DEVICE_213,true,false);
    }

    /**
     * 绑定二代协议
     */
    @Bean
    public Binding bindingTempStatics() {
        //链式写法: 用指定的路由键将队列绑定到交换机
        return BindingBuilder.bind(tempStatics()).to(tempDirectExchange()).with(QueueConstantUtil.MQTT_DEVICE_STATUS_213);
    }



}
