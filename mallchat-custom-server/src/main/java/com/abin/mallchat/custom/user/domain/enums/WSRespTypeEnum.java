package com.abin.mallchat.custom.user.domain.enums;

import com.abin.mallchat.custom.user.domain.vo.response.ws.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * ws前端请求类型枚举
 * </p>
 *
 * @author <a href="https://github.com/zongzibinbin">abin</a>
 * @since 2023-03-19
 */
@AllArgsConstructor
@Getter
public enum WSRespTypeEnum {

    LOGIN_URL(1, "登录二维码返回", WSLoginUrl.class),
    LOGIN_SCAN_SUCCESS(2, "用户扫描成功等待授权", null),
    LOGIN_SUCCESS(3, "用户登录成功返回用户信息", WSLoginSuccess.class),
    MESSAGE(4, "新消息", WSMessage.class),
    ONLINE_OFFLINE_NOTIFY(5, "上下线通知", WSOnlineOfflineNotify.class),
    INVALIDATE_TOKEN(6, "使前端的token失效，意味着前端需要重新登录", null),
    BLACK(7, "拉黑用户", WSBlack.class),
    ;

    private final Integer type;
    private final String desc;
    private final Class dataClass;

    private static Map<Integer, WSRespTypeEnum> cache;

    static {
        cache = Arrays.stream(WSRespTypeEnum.values()).collect(Collectors.toMap(WSRespTypeEnum::getType, Function.identity()));
    }

    public static WSRespTypeEnum of(Integer type) {
        return cache.get(type);
    }

}
