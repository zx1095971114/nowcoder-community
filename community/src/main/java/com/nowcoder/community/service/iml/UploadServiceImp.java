package com.nowcoder.community.service.iml;

import com.nowcoder.community.service.joint.UploadService;
import com.nowcoder.community.service.vo.UploadVo;
import com.nowcoder.community.util.CommonUtils;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author : Zhou Xiang
 * @date : 2024/2/22  16:51
 * @description : 上传文件服务的实现类
 **/
@Service
public class UploadServiceImp implements UploadService {
    @Value("${qiniu.accessKey}")
    private String qiniuAccessKey;

    @Value("${qiniu.secretKey}")
    private String qiniuSecretKey;

    @Value("${qiniu.bucketName}")
    private String qiniuBucketName;

    @Value("${qiniu.upload.address}")
    private String qiniuUploadAddress;

    private static final String FOLDER = "headers/";

    @Override
    public UploadVo upload2Qiniu() {
        String filename = FOLDER + CommonUtils.generateUUID() + ".jpg";

        Auth auth = Auth.create(qiniuAccessKey, qiniuSecretKey);
        StringMap putPolicy = new StringMap();

        putPolicy.put("returnBody", CommonUtils.getJSONString(200, "上传成功"));
        //单位是s
        long expireSeconds = 60 * 5;

        String upToken = auth.uploadToken(qiniuBucketName, filename, expireSeconds, putPolicy);


        UploadVo uploadVo = new UploadVo();
        uploadVo.setUploadAddress(qiniuUploadAddress)
                .setFilename(filename)
                .setUploadToken(upToken);
        return uploadVo;
    }
}
