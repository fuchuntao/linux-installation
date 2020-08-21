package cn.meiot.controller;

import cn.meiot.service.TaskService;
import cn.meiot.entity.vo.Result;
import cn.meiot.entity.vo.TaskInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 任务管理
 */
@Controller
@Slf4j
@RequestMapping("/task")
public class TaskManageController {

    @Autowired(required=false)
    private TaskService taskService;

    private String value;


    @RequestMapping(value={"", "/", "index"})
    public String info(){
        return "index.html";
    }


    /**
     * 任务列表
     * @return
     */
    @ResponseBody
    @RequestMapping(value="list",method = RequestMethod.GET)
    public Result list(){
        log.info("进入查询方法=====>"+value);
        Map<String, Object> map = new HashMap<>();
        List<TaskInfo> infos = taskService.list();
        map.put("rows", infos);
        map.put("total", infos.size());
        Result result = new Result();
        result.setMsg("成功");
        //result.setCode("0000");
        result.setData(map);
        return result;
    }

    /**
     * 保存定时任务
     * @param info
     * http://localhost:21000/qy/api/task/save?jobName=com.quartz.quartz.quartz.CtripScenicJob&jobGroup=group3&jobDescription=job描述&cronExpression=0 13 9 ? * *
     * jobName  是要执行的类（包名+类名）   jobGroup是组 每次执行的组名不能一样  jobDescription 是描述  cronExpression是定时时间
     */
    @ResponseBody
    @RequestMapping(value="save", produces = "application/json; charset=UTF-8")
    public Result save(@RequestBody TaskInfo info){
        Result result = new Result();
        try {
            if(info.getId() == 0) {
                taskService.addJob(info);
            }else{
                taskService.edit(info);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setMsg("失败");
            return result;
        }

        result.setMsg("成功");
        return result;
    }
    @ResponseBody
    @RequestMapping(value = "update",method = RequestMethod.POST)
    public Result update(@RequestBody TaskInfo taskInfo){
        taskService.edit(taskInfo);
        Result result = new Result();
        result.setMsg("成功");
        return result;
    }

    /**
     * 删除定时任务
     * @param jobName
     * @param jobGroup
     */
    @ResponseBody
    @RequestMapping(value="delete/{jobName}/{jobGroup}", method = RequestMethod.PUT)
    public Result delete(@PathVariable String jobName, @PathVariable String jobGroup){
        try {
            taskService.delete(jobName, jobGroup);
        } catch (Exception e) {
           e.printStackTrace();
        }
        Result result = new Result();
        result.setMsg("成功");
        return result;
    }

    /**
     * 暂停定时任务
     * @param jobName
     * @param jobGroup
     */
    @ResponseBody
    @RequestMapping(value="pause/{jobName}/{jobGroup}", produces = "application/json; charset=UTF-8")
    public String pause(@PathVariable String jobName, @PathVariable String jobGroup){
        try {
            taskService.pause(jobName, jobGroup);
        } catch (Exception e) {
            return e.getMessage();
        }
        return "成功";
    }

    /**
     * 重新开始定时任务
     * @param jobName
     * @param jobGroup
     */
    @ResponseBody
    @RequestMapping(value="resume/{jobName}/{jobGroup}", produces = "application/json; charset=UTF-8")
    public String resume(@PathVariable String jobName, @PathVariable String jobGroup){
        try {
            taskService.resume(jobName, jobGroup);
        } catch (Exception e) {
            return e.getMessage();
        }
        return "成功";
    }
}
