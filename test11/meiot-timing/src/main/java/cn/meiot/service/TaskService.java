package cn.meiot.service;

import cn.meiot.entity.vo.TaskInfo;
import org.quartz.SchedulerException;

import java.util.List;

public interface TaskService {

    List<TaskInfo> list();

    void addJob(TaskInfo info);

    void edit(TaskInfo info);

    void delete(String jobName, String jobGroup);

    void pause(String jobName, String jobGroup);

    void resume(String jobName, String jobGroup);

    boolean checkExists(String jobName, String jobGroup)throws SchedulerException;
}