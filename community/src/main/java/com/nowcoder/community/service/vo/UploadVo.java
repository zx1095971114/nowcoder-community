package com.nowcoder.community.service.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Map;

/**
 * @author : Zhou Xiang
 * @date : 2024/2/22  16:45
 * @description : 上传需要的参数
 **/
@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(chain = true)
public class UploadVo {
    private String filename;
    private String uploadToken;
    private String uploadAddress;
    //其他信息
    private Map<String, String> otherInformation;
}
