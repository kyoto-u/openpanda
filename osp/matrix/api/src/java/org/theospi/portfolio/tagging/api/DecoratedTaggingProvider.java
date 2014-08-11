package org.theospi.portfolio.tagging.api;

import java.util.List;

import org.sakaiproject.taggable.api.Tag;
import org.sakaiproject.taggable.api.TagColumn;
import org.sakaiproject.taggable.api.TaggingProvider;

public interface DecoratedTaggingProvider {
	public DTaggingSort getSort();

	public DTaggingPager getPager();

	public boolean getAllowViewTags();
	public TaggingProvider getProvider();

	public List<Tag> getTags();

	public List<TagColumn> getColumns();
}
