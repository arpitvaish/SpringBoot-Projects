package com.application.dao;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.application.dto.Employee;

@Transactional
public interface EmployeeRegistration extends JpaRepository<Employee, Long>{

}
