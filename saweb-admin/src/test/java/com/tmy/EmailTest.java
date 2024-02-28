package com.tmy;


import com.tmy.sys.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.annotation.Resource;

@SpringBootTest
public class EmailTest {
    @Resource
    private static MailService mailService;

    @Resource
    private static TemplateEngine templateEngine;
    public static void main(String[] args) {
        String to = "lemonautism@qq.com";
        // 邮件消息内容
        String tagName = "测试标签";
        // 随机生成验证码
        // 设置邮件内容
        Context context = new Context();
        context.setVariable("tagName", tagName);
        String mail = templateEngine.process("mailtemplate.html", context);
        // 发送邮件
        mailService.sendHtmlMail(to, "活动预约通知", mail);
        System.out.println("发送成功");
    }
}
