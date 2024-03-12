package com.nowcoder.community.controller.vo;

import com.nowcoder.community.entity.Message;
import com.nowcoder.community.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : Zhou Xiang
 * @date : 2023/12/22  16:03
 * @description :私信详情页面的message视图
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageVo {
    private Message message;
    private User from;
}
