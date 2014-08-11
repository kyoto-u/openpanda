/**
 * 
 */
package org.theospi.portfolio.presentation.support;

public class CreatePresentationCommand {
	private String presentationType;
	private String templateId;
	private String presentationName;
	
	public CreatePresentationCommand() {}
	
	public String getPresentationType() {
		return presentationType;
	}
	public void setPresentationType(String presentationType) {
		this.presentationType = presentationType;
	}
	public String getTemplateId() {
		return templateId;
	}
	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	public String getPresentationName() {
		return presentationName;
	}

	public void setPresentationName(String presentationName) {
		this.presentationName = presentationName;
	}
}