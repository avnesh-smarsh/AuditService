package com.smarsh.auditService;

import org.junit.jupiter.api.Test;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Suite
@SelectPackages("com.smarsh.auditService")
class AuditServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
