package com.stylefeng.guns.rest.common.util;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.*;

@Slf4j
@Data
@Configuration
@ConfigurationProperties(prefix = "ftp")
public class FTPUtil {

    // 地址 端口 用户名 密码
    private String hostName;
    private Integer port;
    private String userName;
    private String password;
    private String uploadPath;

    private FTPClient ftpClient = null;

    private void initFTPClient(){
        try{
            ftpClient = new FTPClient();
            ftpClient.setControlEncoding("utf-8");
            ftpClient.connect(hostName,port);
            ftpClient.login(userName,password);
        }catch (Exception e){
            log.error("初始化FTP失败",e);
        }
    }

    //输入一个路径，然后将路径里的文件转换为字符串返回
    public String getFileStrByAddress(String fileAddress){
        BufferedReader bufferedReader = null;
        initFTPClient();
        try {
            bufferedReader = new BufferedReader(
                    new InputStreamReader(ftpClient.retrieveFileStream(fileAddress))
            );

            StringBuffer stringBuffer = new StringBuffer();
            while (true){
                String lineStr = bufferedReader.readLine();
                if (lineStr==null) {
                    break;
                }
                stringBuffer.append(lineStr);
            }
            ftpClient.logout();
            return stringBuffer.toString();
        }catch (Exception e){
            log.error("获取文件信息失败",e);
        }finally {
            try {
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    /**
     * 上传文件
     * @param fileName
     * @param file
     * @return
     */
    public boolean uploadFile(String fileName, File file){
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);

            //FTP相关内容
            initFTPClient();
            //设置相关参数
            ftpClient.setControlEncoding("utf-8");
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            ftpClient.enterLocalPassiveMode();
            //将ftpClient的工作空间修改
            ftpClient.changeWorkingDirectory(this.getUploadPath());

            //上传文件
            ftpClient.storeFile(fileName,fileInputStream);
            return true;

        }catch (Exception e){
            log.error("上传失败",e);
            return false;
        }finally {
            try {
                fileInputStream.close();
                ftpClient.logout();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
