package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        //1.将页面提交的明文密码进行md5加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2.根据页面提交的用户名查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        //3.如果没有查到返回登陆失败
        if (emp == null){
            return R.error("用户名不存在");
        }
        //4.密码对比
        if (!emp.getPassword().equals(password)){
            return R.error("密码错误");
        }
        //5.查看员工状态，已禁用则登录失败
        if (emp.getStatus() == 0){
            return R.error("账号已禁用");
        }
        //6.登录成功,将员工id存入Session并返回登录成功结果。
        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);
    }

    /**
     * 新增员工
     * @param employee
     * @return
     */
    @PostMapping
    //返回的code，泛型用String类型。发送json格式用@RequestBody注解
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee){
        log.info("新增员工,员工信息:{}",employee);
        //设置密码123456,md5加密处理
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        //时间
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());

        //获取当前用户id
        Long empId = (Long) request.getSession().getAttribute("employee");
        employee.setCreateUser(empId);
        employee.setUpdateUser(empId);
        employeeService.save(employee);
        return R.success("新增员工成功");
    }
}
