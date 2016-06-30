package com.project.bean;

public class SearchInnBean {
	
	private boolean isPage = true;
	
	private int searchType = 1;
	
	private String input;
	
	private String innIds;
	
	private boolean isSearchOnly = false;

	public boolean isPage() {
		return isPage;
	}

	public void setPage(boolean isPage) {
		this.isPage = isPage;
	}

	public int getSearchType() {
		return searchType;
	}

	public void setSearchType(int searchType) {
		this.searchType = searchType;
	}

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}

	public String getInnIds() {
		return innIds;
	}

	public void setInnIds(String innIds) {
		this.innIds = innIds;
	}

	public boolean isSearchOnly() {
		return isSearchOnly;
	}

	public void setSearchOnly(boolean isSearchOnly) {
		this.isSearchOnly = isSearchOnly;
	}
	
	
	
}
