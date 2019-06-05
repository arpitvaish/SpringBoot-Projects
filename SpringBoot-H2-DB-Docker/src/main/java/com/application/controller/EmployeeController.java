package com.application.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.application.dao.EmployeeRegistration;
import com.application.dto.Employee;

@RestController
@RequestMapping("/v1")
public class EmployeeController {

	@Autowired
	EmployeeRegistration emp;
	
	@RequestMapping(value = "/greeting", method=RequestMethod.GET)
	public String index(){
		System.out.println("hello world");
		return "Greeting from health check";
	}
	
	@RequestMapping(value = "/getEmployee", method=RequestMethod.GET)
	public Employee getEmployee(Long id){
		return emp.getOne(id);
	}
	
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public int saveEmployee(@ModelAttribute Employee employee){
		 emp.save(employee);
		 return 0;
	}
	
	@RequestMapping(value="/viewAll", method=RequestMethod.GET)
	public List<Employee> getAll(){
		return emp.findAll();
	}
}
