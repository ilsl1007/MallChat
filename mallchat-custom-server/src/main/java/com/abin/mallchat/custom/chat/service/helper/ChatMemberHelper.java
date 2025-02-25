package com.abin.mallchat.custom.chat.service.helper;

import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.StrUtil;
import com.abin.mallchat.common.user.domain.enums.ChatActiveStatusEnum;

/**
 * <p>
 * 成员列表工具类
 * </p>
 *
 * @author <a href="https://github.com/zongzibinbin">abin</a>
 * @since 2023-03-28
 */
public class ChatMemberHelper {

    public static Pair<ChatActiveStatusEnum, String> getCursorPair(String cursor) {
        ChatActiveStatusEnum activeStatusEnum = ChatActiveStatusEnum.ONLINE;
        String timeCursor = null;
        if (StrUtil.isNotBlank(cursor)) {
            String activeStr = cursor.split(StrUtil.UNDERLINE)[0];
            String timeStr = cursor.split(StrUtil.UNDERLINE)[1];
            activeStatusEnum = ChatActiveStatusEnum.of(Integer.parseInt(activeStr));
            timeCursor = timeStr;
        }
        return Pair.of(activeStatusEnum, timeCursor);
    }

    public static String generateCursor(ChatActiveStatusEnum activeStatusEnum, String timeCursor) {
        return activeStatusEnum.getStatus() + StrUtil.UNDERLINE + timeCursor;
    }

}
