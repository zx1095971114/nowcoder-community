package com.nowcoder.community;

import com.nowcoder.community.config.AlphaConfig;
import com.nowcoder.community.controller.AlphaController;
import com.nowcoder.community.dao.AlphaDao;
import com.nowcoder.community.service.AlphaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationContextFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;

import java.text.SimpleDateFormat;
import java.util.Date;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
class CommunityApplicationTests implements ApplicationContextAware {
	private ApplicationContext applicationContext;
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Test
	public void testBean(){
		System.out.println(applicationContext);
	}

	@Test
	public void testDao(){
		AlphaDao alphaDao = applicationContext.getBean("alphaDaoImpl2", AlphaDao.class);

		System.out.println(alphaDao.alphaDao());
	}

	@Test
	public void testService(){
		AlphaService alphaService = applicationContext.getBean(AlphaService.class);
		System.out.println(alphaService.service());
	}

	@Test
	public void testController(){
		AlphaController alphaController = applicationContext.getBean(AlphaController.class);

		System.out.println(alphaController.alphaController());
	}

	@Test
	public void testLifeCycle(){
		AlphaService alphaService = applicationContext.getBean(AlphaService.class);
		System.out.println(alphaService);
		alphaService = applicationContext.getBean(AlphaService.class);
		System.out.println(alphaService);
	}

	@Autowired
	@Qualifier("getSimpleDateFormat")
	private SimpleDateFormat simpleDateFormat;
	@Test
	public void testBean2(){
//		SimpleDateFormat simpleDateFormat = applicationContext.getBean(SimpleDateFormat.class);
		System.out.println(simpleDateFormat.format(new Date()));
	}
}
