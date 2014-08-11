package org.theospi.portfolio.tagging.api;

import java.util.List;
import java.util.Set;

import org.sakaiproject.taggable.api.TaggableItem;

public interface DecoratedTaggableItem {

	public String getTypeName();

	public void setTypeName(String typeName);

	public Set<TaggableItem> getTaggableItems();
	
	public List<TaggableItem> getSortedTaggableItems();

	public void setTaggableItems(Set<TaggableItem> taggableItems);
	
	public void addTaggableItem(TaggableItem taggableItem);

}