package org.theospi.portfolio.tagging.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.sakaiproject.taggable.api.TaggableItem;
import org.theospi.portfolio.tagging.api.DecoratedTaggableItem;

public class DecoratedTaggableItemImpl implements DecoratedTaggableItem {
	private String typeName;
	private Set<TaggableItem> taggableItems = new HashSet<TaggableItem>();
	
	public DecoratedTaggableItemImpl() {
	}
	
	public DecoratedTaggableItemImpl(String typeName) {
		this.typeName = typeName;
	}
	
	public DecoratedTaggableItemImpl(String typeName, Set<TaggableItem> taggableItems) {
		this.typeName = typeName;
		this.taggableItems = taggableItems;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public Set<TaggableItem> getTaggableItems() {
		return taggableItems;
	}
	
	public List<TaggableItem> getSortedTaggableItems() {
		List<TaggableItem> taggableItemList = new ArrayList<TaggableItem>(getTaggableItems());
		Collections.sort(taggableItemList, taggableItemComparator);
		return taggableItemList;
	}

	public void setTaggableItems(Set<TaggableItem> taggableItems) {
		this.taggableItems = taggableItems;
	}
	
	public void addTaggableItem(TaggableItem taggableItem) {
		this.taggableItems.add(taggableItem);
	}
	
	private static Comparator<TaggableItem> taggableItemComparator;
	static {
		taggableItemComparator = new Comparator<TaggableItem>() {
			public int compare(TaggableItem o1, TaggableItem o2) {
				return o1.getActivity().getTitle().toLowerCase().compareTo(
						o2.getActivity().getTitle().toLowerCase());
			}
		};
	}
}
