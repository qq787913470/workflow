package com.test.workflow.controller;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Controller
public class WorkFlowController {
    @Resource
    private RuntimeService runtimeService;
    @Resource
    private TaskService taskService;//任务服务
    //启动流程
    @RequestMapping("start")
    public String start(Model model){
        //启动流程
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("qj");
        model.addAttribute("pid",processInstance.getId());
        return "fill";
    }
    //填写请假单
    @RequestMapping("doFill")
    @ResponseBody
    public String doFill(@RequestParam Map<String,String> args){
        //指定流程实例的所有任务
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(args.get("pid")).list();
        Task task = tasks.get(0);
        //写入请假相关数据
        taskService.setVariables(task.getId(),args);
        //完成填写请假单任务
        taskService.complete(task.getId());
        return "填写完毕！";
    }
    //查看请假数据
    @RequestMapping("toAudit")
    public String toAudit(Model model){
        //获取李四需要处理的所有任务
        List<Task> tasks = taskService.createTaskQuery().taskAssignee("李四").list();
        Task task = tasks.get(0);
        //请假单数据
        Map<String, Object> args = taskService.getVariables(task.getId());
        model.addAllAttributes(args);
        return "audit";
    }
    @RequestMapping("doAudit")
    @ResponseBody
    public String doAudit(@RequestParam Map<String,String> args){
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(args.get("pid")).list();
        Task task = tasks.get(0);
        //写入请假相关数据
        taskService.setVariables(task.getId(),args);
        //完成填写请假单任务
        taskService.complete(task.getId());
        return "审批完毕！";
    }
}
