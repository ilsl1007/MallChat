package com.abin.mallchat.custom.chat.service.adapter;

import cn.hutool.core.bean.BeanUtil;
import com.abin.mallchat.common.chat.domain.entity.Message;
import com.abin.mallchat.common.chat.domain.entity.MessageExtra;
import com.abin.mallchat.common.chat.domain.entity.MessageMark;
import com.abin.mallchat.common.chat.domain.enums.MessageMarkTypeEnum;
import com.abin.mallchat.common.chat.domain.enums.MessageStatusEnum;
import com.abin.mallchat.common.common.domain.enums.YesOrNoEnum;
import com.abin.mallchat.common.common.utils.discover.PrioritizedUrlTitleDiscover;
import com.abin.mallchat.common.user.domain.entity.IpDetail;
import com.abin.mallchat.common.user.domain.entity.IpInfo;
import com.abin.mallchat.common.user.domain.entity.ItemConfig;
import com.abin.mallchat.common.user.domain.entity.User;
import com.abin.mallchat.custom.chat.domain.vo.request.ChatMessageReq;
import com.abin.mallchat.custom.chat.domain.vo.response.ChatMessageResp;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 消息适配器
 * </p>
 *
 * @author <a href="https://github.com/zongzibinbin">abin</a>
 * @since 2023-03-26
 */
public class MessageAdapter {

    public static final int CAN_CALLBACK_GAP_COUNT = 50;
    private static final PrioritizedUrlTitleDiscover URL_TITLE_DISCOVER = new PrioritizedUrlTitleDiscover();

    public static Message buildMsgSave(ChatMessageReq request, Long uid) {

        return Message.builder()
                .replyMsgId(request.getReplyMsgId())
                .content(request.getContent())
                .fromUid(uid)
                .roomId(request.getRoomId())
                .status(MessageStatusEnum.NORMAL.getStatus())
                .extra(buildExtra(request))
                .build();

    }

    private static MessageExtra buildExtra(ChatMessageReq request) {
        Map<String, String> contentTitleMap = URL_TITLE_DISCOVER.getContentTitleMap(request.getContent());
        return MessageExtra.builder().urlTitleMap(contentTitleMap).build();
    }

    public static List<ChatMessageResp> buildMsgResp(List<Message> messages, Map<Long, Message> replyMap, Map<Long, User> userMap, List<MessageMark> msgMark, Long receiveUid, Map<Long, ItemConfig> itemMap) {
        Map<Long, List<MessageMark>> markMap = msgMark.stream().collect(Collectors.groupingBy(MessageMark::getMsgId));
        return messages.stream().map(a -> {
                    ChatMessageResp resp = new ChatMessageResp();
                    resp.setFromUser(buildFromUser(userMap.get(a.getFromUid()), itemMap));
                    resp.setMessage(buildMessage(a, replyMap, userMap, markMap.getOrDefault(a.getId(), new ArrayList<>()), receiveUid));
                    return resp;
                })
                //帮前端排好序，更方便它展示
                .sorted(Comparator.comparing(a -> a.getMessage().getSendTime()))
                .collect(Collectors.toList());
    }

    private static ChatMessageResp.Message buildMessage(Message message, Map<Long, Message> replyMap, Map<Long, User> userMap, List<MessageMark> marks, Long receiveUid) {
        ChatMessageResp.Message messageVO = new ChatMessageResp.Message();
        BeanUtil.copyProperties(message, messageVO);
        messageVO.setSendTime(message.getCreateTime());
        messageVO.setUrlTitleMap(Optional.ofNullable(message.getExtra()).map(MessageExtra::getUrlTitleMap).orElse(null));
        Message replyMessage = replyMap.get(message.getReplyMsgId());
        //回复消息
        if (Objects.nonNull(replyMessage)) {
            ChatMessageResp.ReplyMsg replyMsgVO = new ChatMessageResp.ReplyMsg();
            replyMsgVO.setId(replyMessage.getId());
            replyMsgVO.setContent(replyMessage.getContent());
            User replyUser = userMap.get(replyMessage.getFromUid());
            replyMsgVO.setUsername(replyUser.getName());
            replyMsgVO.setCanCallback(YesOrNoEnum.toStatus(Objects.nonNull(message.getGapCount()) && message.getGapCount() <= CAN_CALLBACK_GAP_COUNT));
            replyMsgVO.setGapCount(message.getGapCount());
            messageVO.setReply(replyMsgVO);
        }
        //消息标记
        messageVO.setMessageMark(buildMsgMark(marks, receiveUid));
        return messageVO;
    }

    private static ChatMessageResp.MessageMark buildMsgMark(List<MessageMark> marks, Long receiveUid) {
        Map<Integer, List<MessageMark>> typeMap = marks.stream().collect(Collectors.groupingBy(MessageMark::getType));
        List<MessageMark> likeMarks = typeMap.getOrDefault(MessageMarkTypeEnum.LIKE.getType(), new ArrayList<>());
        List<MessageMark> dislikeMarks = typeMap.getOrDefault(MessageMarkTypeEnum.DISLIKE.getType(), new ArrayList<>());
        ChatMessageResp.MessageMark mark = new ChatMessageResp.MessageMark();
        mark.setLikeCount(likeMarks.size());
        mark.setUserLike(Optional.ofNullable(receiveUid).filter(uid -> likeMarks.stream().anyMatch(a -> a.getUid().equals(uid))).map(a -> YesOrNoEnum.YES.getStatus()).orElse(YesOrNoEnum.NO.getStatus()));
        mark.setDislikeCount(dislikeMarks.size());
        mark.setUserDislike(Optional.ofNullable(receiveUid).filter(uid -> dislikeMarks.stream().anyMatch(a -> a.getUid().equals(uid))).map(a -> YesOrNoEnum.YES.getStatus()).orElse(YesOrNoEnum.NO.getStatus()));
        return mark;
    }

    private static ChatMessageResp.UserInfo buildFromUser(User fromUser, Map<Long, ItemConfig> itemMap) {
        ChatMessageResp.UserInfo userInfo = new ChatMessageResp.UserInfo();
        userInfo.setUsername(fromUser.getName());
        userInfo.setAvatar(fromUser.getAvatar());
        userInfo.setLocPlace(Optional.ofNullable(fromUser.getIpInfo()).map(IpInfo::getUpdateIpDetail).map(IpDetail::getCity).orElse(null));
        userInfo.setUid(fromUser.getId());
        if (Objects.nonNull(fromUser.getItemId())) {
            ChatMessageResp.Badge badge = new ChatMessageResp.Badge();
            ItemConfig itemConfig = itemMap.get(fromUser.getItemId());
            badge.setImg(itemConfig.getImg());
            badge.setDescribe(itemConfig.getDescribe());
            userInfo.setBadge(badge);
        }
        return userInfo;
    }

}
