package com.stylefeng.guns;

import com.stylefeng.guns.rest.AlipayApplication;
import com.stylefeng.guns.rest.common.util.FTPUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AlipayApplication.class)
public class GunsRestApplicationTests {

	@Autowired
	private FTPUtil ftpUtil;

	@Test
	public void contextLoads() {
		//String fileStrByAddress = ftpUtil.getFileStrByAddress("cgs.json");
		//System.out.println(fileStrByAddress);

		File file = new File("C:\\Users\\NPC\\Desktop\\qrcode\\qr-3f96923547004e04823792c0600e356c.png");
		boolean  b = ftpUtil.uploadFile("aa.png", file);
		System.out.println("上传是否成功"+b);
	}

}
