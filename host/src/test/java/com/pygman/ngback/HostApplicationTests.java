package com.pygman.ngback;

import com.pygman.ngback.host.service.IndexService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class HostApplicationTests {

	@Autowired
    IndexService indexService;

	@Test
	public void contextLoads() {

		String index = indexService.index("hello ");


	}

}
