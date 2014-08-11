package org.theospi.portfolio.matrix.util;

import java.util.List;

import org.sakaiproject.content.api.ContentResource;

public class FormNameGeneratorUtil {

	/**
    * 
    * @param formTypeName
    * @param count: this keeps track of the number of times getFormDisplayName is called for naming reasons
    * @param contentResourceList: a list of the resources for looking up the names to compare to the new name
    * @return
    */
   public static String getFormDisplayName(String formTypeName, int count, List contentResourceList) {
      String name = "";

      name = formTypeName;
      
      if(count > 1){
    	  name = name + " (" + count + ")";
      }
      
      count++;
      
      //if the name already exists, then recursively loop through this function untill there is an unique name      
      return formDisplayNameExists(name, contentResourceList) && contentResourceList != null ? 
    		  getFormDisplayName(formTypeName, count, contentResourceList) : name;
   }
   
   /**
    * 
    * @param name
    * @param contentResourceList
    * @return
    * 
    * returns true if the name passed exists in the list of contentResource
    * otherwise returns false
    */
   protected static boolean formDisplayNameExists(String name, List contentResourceList){
	   
	   
	   if(contentResourceList != null){
		   ContentResource cr;
		   for(int i = 0; i < contentResourceList.size(); i++){
			   cr = (ContentResource) contentResourceList.get(i);
			   if(name.equals(cr.getProperties().getProperty(cr.getProperties().getNamePropDisplayName()).toString())){
				   return true;
			   }
		   }
	   }
  
	   return false;
   }
}
