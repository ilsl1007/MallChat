package com.abin.mallchat.custom.user.service;

import com.abin.mallchat.custom.user.domain.vo.request.user.ModifyNameReq;
import com.abin.mallchat.custom.user.domain.vo.request.user.WearingBadgeReq;
import com.abin.mallchat.custom.user.domain.vo.response.user.BadgeResp;
import com.abin.mallchat.custom.user.domain.vo.response.user.UserInfoResp;

import java.util.List;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author <a href="https://github.com/zongzibinbin">abin</a>
 * @since 2023-03-19
 */
public interface UserService {

    /**
     * 获取前端展示信息
     *
     * @param uid
     * @return
     */
    UserInfoResp getUserInfo(Long uid);

    /**
     * 修改用户名
     *
     * @param uid
     * @param req
     */
    void modifyName(Long uid, ModifyNameReq req);

    /**
     * 用户徽章列表
     *
     * @param uid
     */
    List<BadgeResp> badges(Long uid);

    /**
     * 佩戴徽章
     *
     * @param uid
     * @param req
     */
    void wearingBadge(Long uid, WearingBadgeReq req);

    /**
     * 用户注册
     *
     * @param openId
     */
    void register(String openId);

}
