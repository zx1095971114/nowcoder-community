package com.nowcoder.community.service.joint;

import com.nowcoder.community.service.vo.UploadVo;
import org.springframework.stereotype.Service;

/**
 * @author : Zhou Xiang
 * @date : 2024/2/22  16:40
 * @description : 上传文件相关服务
 **/
@Service
public interface UploadService {
    UploadVo upload2Qiniu();
}
