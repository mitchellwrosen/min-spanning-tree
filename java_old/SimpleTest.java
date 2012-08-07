package graphutil;

import org.testng.annotations.*;

public class SimpleTest {

	@BeforeClass
	public void beforeClassMethod() {
		// code that will be invoked when this test is instantiated
		System.out.println("beforeClassMethod() called BEFORE all the tests...");
	}

	@Test(groups = { "fast" })
	public void aFastTest() {
		System.out.println("aFastTest test...");
	}

	@Test(groups = { "slow" })
	public void aSlowTest() {
		System.out.println("aSlowTest test...");
	}
	
	@AfterClass
	public void afterClassMethod() {
		// code that will be invoked AFTER this test is run...
		System.out.println("afterClassMethod() called AFTER all the tests...");		
	}

}
