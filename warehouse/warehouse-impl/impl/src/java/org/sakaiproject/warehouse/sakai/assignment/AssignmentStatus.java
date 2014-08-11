package org.sakaiproject.warehouse.sakai.assignment;


public class AssignmentStatus {


	private String assignment_id;
	private String user_id;
	private String course_title;
	private String assignment_title;
	private String assignment_status;
	private String assignment_grade;
	private String student_first_name;
	private String student_last_name;
	private String school;
	private String district;
	private String class_year;
	private String advisor;
	private String course_term;
	private String course_code;
	private String course_start_date;
	private String course_section;
	private String instructor;


	public String getAdvisor() {
		return advisor;
	}
	public void setAdvisor(String advisor) {
		this.advisor = advisor;
	}
	public String getAssignment_grade() {
		return assignment_grade;
	}
	public void setAssignment_grade(String assignment_grade) {
		this.assignment_grade = assignment_grade;
	}
	public String getAssignment_id() {
		return assignment_id;
	}
	public void setAssignment_id(String assignment_id) {
		this.assignment_id = assignment_id;
	}
	public String getAssignment_status() {
		return assignment_status;
	}
	public void setAssignment_status(String assignment_status) {
		this.assignment_status = assignment_status;
	}
	public String getClass_year() {
		return class_year;
	}
	public void setClass_year(String class_year) {
		this.class_year = class_year;
	}
	public String getCourse_code() {
		return course_code;
	}
	public void setCourse_code(String course_code) {
		this.course_code = course_code;
	}
	public String getCourse_section() {
		return course_section;
	}
	public void setCourse_section(String course_section) {
		this.course_section = course_section;
	}
	public String getCourse_term() {
		return course_term;
	}
	public void setCourse_term(String course_term) {
		this.course_term = course_term;
	}
	public String getDistrict() {
		return district;
	}
	public void setDistrict(String district) {
		this.district = district;
	}
	public String getInstructor() {
		return instructor;
	}
	public void setInstructor(String instructor) {
		this.instructor = instructor;
	}
	public String getSchool() {
		return school;
	}
	public void setSchool(String school) {
		this.school = school;
	}
	public String getStudent_first_name() {
		return student_first_name;
	}
	public void setStudent_first_name(String student_first_name) {
		this.student_first_name = student_first_name;
	}
	public String getStudent_last_name() {
		return student_last_name;
	}
	public void setStudent_last_name(String student_last_name) {
		this.student_last_name = student_last_name;
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getAssignment_title() {
		return assignment_title;
	}
	public void setAssignment_title(String assignment_title) {
		this.assignment_title = assignment_title;
	}
	public String getCourse_title() {
		return course_title;
	}
	public void setCourse_title(String course_title) {
		this.course_title = course_title;
	}

	public String getCourse_start_date() {
		return course_start_date;
	}
	public void setCourse_start_date(String course_start_date) {
		this.course_start_date = course_start_date;
	}

}
