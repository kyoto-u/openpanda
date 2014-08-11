package org.theospi.portfolio.tagging.api;

public interface DTaggingPager {
	
	public static final String FIRST = "|<", PREVIOUS = "<", NEXT = ">",
	LAST = ">|";
	
	public int getFirstItemNumber();

	public int getLastItemNumber();

	public boolean getCanFirst();

	public boolean getCanPrevious();

	public boolean getCanNext();

	public boolean getCanLast();

	public int[] getPageSizes();

	public int getFirstItem();

	public void setFirstItem(int firstItem);

	public int getPageSize();

	public void setPageSize(int pageSize);

	public int getTotalItems();

	public void setTotalItems(int totalItems);
}
