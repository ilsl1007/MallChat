package com.abin.mallchat.common.chat.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Description: 消息状态
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-03-19
 */
@AllArgsConstructor
@Getter
public enum RoomTypeEnum {

    GROUP(1, "大群聊"),
    BOILING(2, "沸点"),
    ;

    private final Integer status;
    private final String desc;

    private static Map<Integer, RoomTypeEnum> cache;

    static {
        cache = Arrays.stream(RoomTypeEnum.values()).collect(Collectors.toMap(RoomTypeEnum::getStatus, Function.identity()));
    }

    public static RoomTypeEnum of(Integer type) {
        return cache.get(type);
    }

}
