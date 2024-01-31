package com.example.demo.testSubType;

import com.alibaba.nacos.api.naming.pojo.healthcheck.impl.Mysql;

/**
 * ClassName:MysqlChecker
 * Package:com.example.demo.testSubType
 * Description:
 *
 * @Date:2024/1/31 14:38
 * @Author:qs@1.com
 */
public class MysqlChecker extends AbstractChecker {
    public static final String TYPE = "MYSQL";

    private String user;

    private String pwd;

    private String cmd;

    public MysqlChecker() {
        super(Mysql.TYPE);
    }

    public String getCmd() {
        return this.cmd;
    }

    public String getPwd() {
        return this.pwd;
    }

    public String getUser() {
        return this.user;
    }

    public void setUser(final String user) {
        this.user = user;
    }

    public void setCmd(final String cmd) {
        this.cmd = cmd;
    }

    public void setPwd(final String pwd) {
        this.pwd = pwd;
    }
}
