package org.sakaiproject.warehouse.sakai.assignment;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.sakaiproject.warehouse.impl.BaseWarehouseTask;

public class AssignmentWarehouseTask extends BaseWarehouseTask{

	private AssignmentWarehouseService assignmentDWService;

	protected Collection getItems() {
		// TODO Auto-generated method stub
		System.out.println("\n\nGOING TO get ASSINGMENTWAREHOUSEITEMS!\n\n");
		List items  = new ArrayList();


		items.addAll(assignmentDWService.getDWAssignmentStatusAll());

		//some service to get the fields needed for the
		//warehouse (get???????ForWarehouse()) return collection
		return items;
	}



	private AssignmentStatus getTestAssignment(){

		AssignmentStatus as = new AssignmentStatus();
		as.setAdvisor("advisor_12345");
		as.setAssignment_grade("A++");
		as.setAssignment_id("123456789");
		as.setAssignment_status("completed");
		as.setClass_year("1999");
		as.setCourse_code("CALC 422");
		as.setCourse_section("422");
		as.setCourse_term("FALL");
		as.setDistrict("Madison");
		as.setInstructor("Dr. Pheanis");
		as.setSchool("St. Mary's");
		as.setStudent_first_name("Lea");
		as.setStudent_last_name("Suft");
		as.setUser_id("lsuft");


		return as;


	}



	public AssignmentWarehouseService getAssignmentDWService() {
		return assignmentDWService;
	}



	public void setAssignmentDWService(
			AssignmentWarehouseService assignmentDWService) {
		this.assignmentDWService = assignmentDWService;
	}


}
