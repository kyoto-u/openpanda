package org.theospi.event;


public interface EventConstants {
		/** portfolio events */
	   public static final String EVENT_PORTFOLIO_ADD = "org.theospi.presentation.add";
	   public static final String EVENT_PORTFOLIO_REVISE = "org.theospi.presentation.revise";
	   public static final String EVENT_PORTFOLIO_DELETE = "org.theospi.presentation.delete";

	   /** template events */
	   public static final String EVENT_TEMPLATE_ADD = "org.theospi.template.add";
	   public static final String EVENT_TEMPLATE_REVISE = "org.theospi.template.revise";
	   public static final String EVENT_TEMPLATE_DELETE = "org.theospi.template.delete";

	   /** layout events */
	   public static final String EVENT_LAYOUT_ADD = "org.theospi.layout.add";
	   public static final String EVENT_LAYOUT_REVISE = "org.theospi.layout.revise";
	   public static final String EVENT_LAYOUT_DELETE = "org.theospi.layout.delete";

	   /** style events */
	   public static final String EVENT_STYLE_ADD = "org.theospi.style.add";
	   public static final String EVENT_STYLE_REVISE = "org.theospi.style.revise";
	   public static final String EVENT_STYLE_DELETE = "org.theospi.style.delete";

	   /** matrix events */
	   public static final String EVENT_SCAFFOLD_ADD_REVISE = "org.theospi.scaffold.addRevise";
	   public static final String EVENT_SCAFFOLD_DELETE = "org.theospi.scaffold.delete";
	   public static final String EVENT_SCAFFOLD_PUBLISH = "org.theospi.scaffold.publish";
	   
	   /** wizard events */
	   public static final String EVENT_WIZARD_ADD = "org.theospi.wizard.add";
	   public static final String EVENT_WIZARD_REVISE = "org.theospi.wizard.revise";
	   public static final String EVENT_WIZARD_DELETE = "org.theospi.wizard.delete";
	   public static final String EVENT_WIZARD_PUBLISH = "org.theospi.wizard.publish";

	   /** matrix OR wizard events */
	   public static final String EVENT_FORM_ADD = "org.theospi.form.addRevise";
	   public static final String EVENT_FORM_DELETE = "org.theospi.form.delete";
	   
	   /** feedback events */
	   public static final String EVENT_REVIEW_ADD = "org.theospi.review.add";
	   public static final String EVENT_REVIEW_REVISE = "org.theospi.review.revise";
	   public static final String EVENT_REVIEW_DELETE = "org.theospi.review.delete";

}
