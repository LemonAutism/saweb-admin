package com.tmy.sys.controller;

import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tmy.common.vo.Result;
import com.tmy.sys.entity.Activities;
import com.tmy.sys.entity.User;
import com.tmy.sys.entity.UserActivities;
import com.tmy.sys.mapper.ActivitiesMapper;
import com.tmy.sys.mapper.UserActivitiesMapper;
import com.tmy.sys.mapper.UserMapper;
import com.tmy.sys.service.IUserActivitiesService;
import com.tmy.sys.service.IUserService;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author tmy
 * @since 2024-02-20
 */
@RestController
@RequestMapping("/userAct")
public class UserActivitiesController {


    @Resource
    private UserMapper userMapper;

    @Resource
    private UserActivitiesMapper userActivitiesMapper;

    @Resource
    private ActivitiesMapper activitiesMapper;

    @Autowired
    private IUserActivitiesService userActivitiesService;

    @GetMapping("/{activityId}")
    public Result<List<User>> getUserActivitiesList(@PathVariable("activityId") Integer activityId) {
        // 通过活动ID获取所有参与该活动的用户ID
        List<Integer> userIds = userActivitiesMapper.selectList(
                new QueryWrapper<UserActivities>().eq("activity_id", activityId).eq("participation_status",1)
        ).stream().map(UserActivities::getUserid).collect(Collectors.toList());
        if (userIds.isEmpty()) {
            return Result.fail("该活动暂无报名人员");
        }
        // 通过用户ID列表查询所有用户信息
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.in("id", userIds);
        List<User> users = userMapper.selectList(wrapper);

        return Result.success(users);
    }

    /**
     * 导出接口
     */
    @GetMapping("/export/{activityId}")
    public void export(HttpServletResponse response,@PathVariable("activityId") Integer activityId) throws Exception {
        // 从数据库查询出所有的数据
        List<Integer> userIds = userActivitiesMapper.selectList(
                new QueryWrapper<UserActivities>().eq("activity_id", activityId)
        ).stream().map(UserActivities::getUserid).collect(Collectors.toList());
        // 通过用户ID列表查询所有用户信息
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.in("id", userIds);
        List<User> users = userMapper.selectList(wrapper);
        System.out.println("打印数据");
        System.out.println(users);


        try (ServletOutputStream out = response.getOutputStream()) {
            // 在内存操作，写出到浏览器
            ExcelWriter writer = ExcelUtil.getWriter(true);

            // 自定义标题别名
            writer.addHeaderAlias("id", "学号");
            writer.addHeaderAlias("username", "用户名");

            System.out.println(writer);

            // 一次性写出list内的对象到excel，使用默认样式，强制输出标题
            writer.write(users, true);

            // 设置浏览器响应的格式
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
            String fileName = URLEncoder.encode("报名表", "UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");

            // 写入并刷新输出流
            writer.flush(out, true);
        } catch (Exception e) {
            // 如果出现异常，在响应未提交前进行处理，如重定向至错误页面或发送错误状态码
            response.reset();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "导出失败，请稍后重试！");
        }
    }

    @GetMapping("/count/{activityId}")
    public Result<Integer> countUserActivities(@PathVariable("activityId") Integer activityId) {
        Integer count = Math.toIntExact(userActivitiesMapper.selectCount(
                new QueryWrapper<UserActivities>().eq("activity_id", activityId)
        ));
        return Result.success(count);
    }

    @GetMapping("/join")
    public Result<String> join(@RequestParam(value = "id") Integer id,
                               @RequestParam(value = "activityId") Integer activityId) {
        int count = Integer.parseInt(activitiesMapper.selectOne(new QueryWrapper<Activities>().eq("activity_id", activityId)).getCount());
        int joinCount = Math.toIntExact(userActivitiesMapper.selectCount(new QueryWrapper<UserActivities>().eq("activity_id", activityId)));
        if (userActivitiesMapper.selectCount(
                new QueryWrapper<UserActivities>().eq("activity_id", activityId).eq("userid", id)
        ) > 0) {
            return Result.fail("您已报名，请勿重复报名");
        }
        if (count <= joinCount) {
            return Result.fail("报名人数已满");
        }

        //获取当前时间戳
        long timestamp = Instant.now().toEpochMilli();
        UserActivities userActivities = new UserActivities();
        userActivities.setUserid(id);
        userActivities.setActivityId(activityId);
        userActivities.setParticipationStatus(1);
        userActivities.setTime(String.valueOf(timestamp));
        userActivitiesService.save(userActivities);
        return Result.success("报名成功");
    }

    @DeleteMapping("/{id}")
    public Result<?> deleteUserActivities(@PathVariable("id") Integer id,
                                          @RequestParam(value = "activityId") Integer activityId) {
        userActivitiesMapper.delete(new QueryWrapper<UserActivities>().eq("activity_id", activityId).eq("userid", id));
        return Result.success("取消报名成功");
    }

    @GetMapping("/isJoin")
    public Result isJoin(@RequestParam(value = "id") Integer id,
                               @RequestParam(value = "activityId") Integer activityId) {
        boolean isJoin = userActivitiesMapper.selectCount(
                new QueryWrapper<UserActivities>().eq("activity_id", activityId).eq("userid", id)
        ) > 0;
        return Result.success(isJoin);
    }

    @GetMapping("/userJoinAllAct")
    public Result userJoinAllAct(@RequestParam(value = "id") Integer id) {
        List<UserActivities> userActivities = userActivitiesMapper.selectList(
                new QueryWrapper<UserActivities>().eq("userid", id).eq("participation_status",1)
        );
        List<Activities> activities = userActivities.stream().map(userActivities1 -> {
            Activities activity = activitiesMapper.selectById(userActivities1.getActivityId());
            return activity;
        }).collect(Collectors.toList());
        return Result.success(activities);
    }


//    @GetMapping("/reservation")
//    public Result<String> reservation(@RequestParam(value = "id") Integer id,
//                                      @RequestParam(value = "activityId") Integer activityId) {
//
//    }
}
