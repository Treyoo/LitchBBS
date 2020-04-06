package com.litchi.bbs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class BbsApplication {
	@PostConstruct//构造方法执行完执行此方法
	public void init(){
		//解决Elasticsearch的Netty启动bug,See Netty4Utils.setAvailableProcessors()
		System.setProperty("es.set.netty.runtime.available.processors","false");
	}

	public static void main(String[] args) {
		SpringApplication.run(BbsApplication.class, args);
	}

}
