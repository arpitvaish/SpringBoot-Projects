package com.application.controller;

import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.application.dao.EmployeeRegistration;
import com.application.dto.Employee;

import junit.framework.Assert;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EmployeeControllerTest {

	@Autowired
	EmployeeRegistration emp;
	
	@Test
	public void testGetEmployee() {
		//fail("Not yet implemented");
	}

	@Test
	public void testSaveEmployee() {
		//fail("Not yet implemented");
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testGetAll() {
		List<Employee> e =  emp.findAll();
		Assert.assertNotNull(e);
	}

}
