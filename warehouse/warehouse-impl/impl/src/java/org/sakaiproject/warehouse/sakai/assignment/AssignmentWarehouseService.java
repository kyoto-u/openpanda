package org.sakaiproject.warehouse.sakai.assignment;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.assignment.api.Assignment;
import org.sakaiproject.assignment.api.AssignmentService;
import org.sakaiproject.assignment.api.AssignmentSubmission;
import org.sakaiproject.javax.PagingPosition;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.site.api.SiteService.SelectionType;
import org.sakaiproject.site.api.SiteService.SortType;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserNotDefinedException;
import org.sakaiproject.user.cover.UserDirectoryService;
import org.sakaiproject.entity.api.ResourceProperties;


public class AssignmentWarehouseService {

    private SiteService siteService;
    private List siteTypes;
    private AssignmentService assService;
    private List roles;

    private final Log logger = LogFactory
            .getLog(AssignmentWarehouseService.class);

    public List getDWAssignmentStatusAll() {

        List assignmentStatus = new ArrayList();
        List sites_list = getAllCourseSites();
        logger.info("Gathering " + sites_list.size()
                + " course sites to set....");


        Iterator sites = sites_list.iterator();

        while (sites.hasNext()) {
            // here we want to loop through each
            Site site = (Site) sites.next();
            //Set siteMembers = site.getUsersHasRole("Student");
            Set siteMembers = getUsersByRole(site, getRoles());


            Iterator assignments = assService.getAssignmentsForContext(site
                    .getId());
            while (assignments.hasNext()) {
                Assignment as = (Assignment) assignments.next();

                Iterator members = siteMembers.iterator();
                // for each member in the site, get this assignments status
                // if non exists then we need to persist that as well
                while (members.hasNext()) {
                    try {

                        User student = UserDirectoryService.getUser((String)members.next());

                        AssignmentStatus aStats = new AssignmentStatus();

                        AssignmentSubmission asub = assService.getSubmission(as.getId(), student);



                        setAssignmentStats(aStats, as, asub, site, student);

                        //setGoalRatings(as,student,aStats);


                        assignmentStatus.add(aStats);

                    } catch (UserNotDefinedException e) {

                        e.printStackTrace();
                    }

                }
            }
        }
        return assignmentStatus;
    }



    private void setAssignmentStats(AssignmentStatus aStats, Assignment as,
                                    AssignmentSubmission asub, Site site, User student) {

        if (asub == null) {
            aStats.setAssignment_grade("n/a");
            aStats.setAssignment_status("Not submitted");
        } else {
            aStats.setAssignment_grade(asub.getGrade());
            aStats.setAssignment_status(asub.getStatus());
        }

        aStats.setAssignment_id(as.getId());
        aStats.setStudent_first_name(student.getFirstName());
        aStats.setStudent_last_name(student.getLastName());
        aStats.setUser_id(student.getId());

        aStats.setCourse_title(site.getTitle());
        aStats.setAssignment_title(as.getTitle());

        ResourceProperties srp = student.getProperties();
        /*aStats.setAdvisor(srp.getProperty(UserProperties.ADVISOR));
        aStats.setClass_year(srp.getProperty(UserProperties.CLASS_YEAR));
        aStats.setDistrict(srp.getProperty(UserProperties.DISTRICT));
        aStats.setSchool(srp.getProperty(UserProperties.SCHOOL_NUMBER));    */

        ResourceProperties siteRP = site.getProperties();
     /*   aStats.setCourse_code(siteRP.getProperty(RINETCourse.COURSE_NUMBER));
        aStats.setCourse_section(siteRP.getProperty(RINETCourse.COURSE_SECTION));
        aStats.setCourse_term(siteRP.getProperty(RINETCourse.COURSE_TERM));
        aStats.setCourse_start_date(siteRP.getProperty(RINETCourse.COURSE_START_DATE));    */

        String maintainRole = site.getMaintainRole();
        Set instructors = site.getUsersHasRole(maintainRole);

        String instructorsNames = getInstructors(instructors);
        aStats.setInstructor(instructorsNames);

    }

    private String getInstructors(Set instructors) {

        String result = "";
        Iterator it = instructors.iterator();

        while (it.hasNext()) {
            try{
                User inst = UserDirectoryService.getUser((String)it.next());
                if(inst==null || inst.getDisplayId().equalsIgnoreCase("admin"))
                    continue;



            if (result.length() > 0) {
                result = result.concat(", ");
            }
            result = result.concat(inst.getFirstName() + " " + inst.getLastName());


            }catch(UserNotDefinedException unde){unde.printStackTrace();}
        }

        return result;
    }

    public List getAllCourseSites() {
        // List getSites(SelectionType type, Object ofType, String criteria, Map
        // propertyCriteria, SortType sort, PagingPosition page);

        PagingPosition pp = new PagingPosition();
        pp.setPaging(true);


        return siteService.getSites(SelectionType.ANY, getSiteTypes(), null, null,
                SortType.NONE, null);
    }

    protected Set getUsersByRole(Site site, List roles){

        Set users = new HashSet();
        if(site==null || roles==null || roles.size()<1)
            return users;

        int rolesSize = roles.size();

        for(int r = 0; r < rolesSize; r++){
            users.addAll(site.getUsersHasRole((String)roles.get(r)));
        }

        return users;

    }

    public AssignmentService getAssService() {
        return assService;
    }

    public void setAssService(AssignmentService assService) {
        this.assService = assService;
    }

    public SiteService getSiteService() {
        return siteService;
    }

    public void setSiteService(SiteService siteService) {
        this.siteService = siteService;
    }

    public List getSiteTypes() {
        return siteTypes;
    }

    public void setSiteTypes(List siteTypes) {
        this.siteTypes = siteTypes;
    }

    public List getRoles() {
        return roles;
    }

    public void setRoles(List roles) {
        this.roles = roles;
    }

}
