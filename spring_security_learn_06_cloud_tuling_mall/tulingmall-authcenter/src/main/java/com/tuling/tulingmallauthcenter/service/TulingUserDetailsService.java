package com.tuling.tulingmallauthcenter.service;

import com.tuling.tulingmall.common.api.CommonResult;
import com.tuling.tulingmallauthcenter.domain.MemberDetails;
import com.tuling.tulingmallauthcenter.feign.UmsMemberFeignService;
import com.tuling.tulingmallauthcenter.model.UmsMember;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @Author Fox
 */
@Slf4j
@Component
public class TulingUserDetailsService implements UserDetailsService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 加载用户信息
        if (StringUtils.isEmpty(username)) {
            log.warn("用户登陆用户名为空:{}", username);
            throw new UsernameNotFoundException("用户名不能为空");
        }

        UmsMember umsMember = getByUsername(username);
//        UmsMember umsMember = jdbcTemplate.queryForObject(
//                "select * from ums_member where username = '" + username + "' ", (rs, rowNum) -> {
//                    UmsMember member = new UmsMember();
//                    member.setId(rs.getLong("id"));
//                    member.setUsername(rs.getString("username"));
//                    member.setPassword(rs.getString("password"));
//                    member.setStatus(rs.getInt("status"));
//                    return member;
//                });

        if (null == umsMember) {
            log.warn("根据用户名没有查询到对应的用户信息:{}", username);
        }

        log.info("根据用户名:{}获取用户登陆信息:{}", username, umsMember);

        // 会员信息的封装 implements UserDetails
        MemberDetails memberDetails = new MemberDetails(umsMember);

        return memberDetails;
    }

    @Autowired
    private UmsMemberFeignService umsMemberFeignService;

    public UmsMember getByUsername(String username) {
        // fegin获取会员信息
        CommonResult<UmsMember> umsMemberCommonResult = umsMemberFeignService.loadUserByUsername(username);

        return umsMemberCommonResult.getData();
    }
}
