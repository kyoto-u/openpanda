package org.theospi.portfolio.presentation.model;

import java.util.Comparator;

public class PresentationTemplateNameComparator implements Comparator<PresentationTemplate> {
	public int compare(PresentationTemplate arg0, PresentationTemplate arg1) {
		return arg0.getName().compareTo(arg1.getName());
	}
}
