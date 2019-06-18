package com.application.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.application.dao.EmployeeRegistration;
import com.application.dto.Employee;
import com.google.gson.Gson;

@RestController
@RequestMapping("/v1")
public class EmployeeController {

	@Autowired
	EmployeeRegistration emp;
	private static final Gson gson = new Gson();

	@RequestMapping(value = "/greeting", method = RequestMethod.GET)
	public ResponseEntity<?> index() {
		System.out.println("hello world");
		return new ResponseEntity<>("Hello World!", HttpStatus.OK);
	}

	/*
	 * Using Request Param http://localhost:8080/v1/getEmployee?id=abc ---- ID:
	 * abc
	 * 
	 * putting Request Param required = false will return 404 if name not
	 * present
	 * 
	 */
	@RequestMapping(value = "/getEmployee", method = RequestMethod.GET, produces = { "application/json",
			"application/xml" })
	public ResponseEntity<?> getEmployee(@RequestParam(name = "id", required = false) String userid) {
		return new ResponseEntity<>(gson.toJson(emp.getOne(Long.parseLong(userid))), HttpStatus.ACCEPTED);
	}

	/*
	 * Using PathVariable http://localhost:8080/v1/getEmployee/abc ---- ID: abc
	 * we can use same params as Request Param above for the same.
	 * 
	 */

	@RequestMapping(value = "/getEmployee/{id}", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE,
			"application/xml" })
	public ResponseEntity<?> getEmployee(@PathVariable(required = false) Long id) {
		return new ResponseEntity<>(emp.getOne(id), HttpStatus.ACCEPTED);
	}

	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public int saveEmployee(@ModelAttribute Employee employee) {
		emp.save(employee);
		return 0;
	}

	@RequestMapping(value = "/viewAll", method = RequestMethod.GET)
	public List<Employee> getAll() {
		return emp.findAll();
	}
}
