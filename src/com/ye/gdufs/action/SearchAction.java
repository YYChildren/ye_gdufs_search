package com.ye.gdufs.action;

import java.util.ArrayList;
import java.util.List;

import com.opensymphony.xwork2.ActionSupport;
import com.ye.gdufs.global.GdufsSearch;
import com.ye.gdufs.model.Result;

public class SearchAction extends ActionSupport{
	private static final long serialVersionUID = -1618973736366534533L;
	private static final int NRESULTS = 10;
	private static final int CURRENT_PAGE = 1;
	private static final int VIEW_PAGE_COUNT = 5;
	private int nresults = NRESULTS;
	private int currentPage = CURRENT_PAGE;
	private int resultCount;
	private int pageCount;
	private int viewPageCount = VIEW_PAGE_COUNT ;
	private List<Integer> viewPageNos;
	private boolean hasPrev;
	private boolean hasNext;
	private String reqStr;
	private List<Result> resultL;
	private List<Result> subResultL;
	@Override
	public String execute() throws Exception {
		do_submit();
		return SUCCESS;
	}	
	private void do_submit() {
		GdufsSearch gs = new GdufsSearch();
		gs.setReqStr(reqStr);
		gs.search();
		this.resultL = gs.getResultL();
		this.resultCount = resultL.size();
		int begin = (currentPage - 1)*nresults;
		this.nresults = nresults <= 0 ? NRESULTS  : nresults; 
		if(begin  < resultCount){
			int end = begin + nresults > resultCount ? resultCount : begin + nresults;
			this.subResultL = resultL.subList(begin, end);
			
			this.pageCount = (resultCount +  nresults - 1)/ nresults;
			this.viewPageCount = viewPageCount <= pageCount ? VIEW_PAGE_COUNT : pageCount;
			int viewBegin;
			int viewEnd;
			if( currentPage - viewPageCount / 2 > 1 ){
				viewBegin = currentPage - viewPageCount / 2; 
			}else{
				viewBegin = 1;
			}
			if(viewBegin + viewPageCount -1<= pageCount){
				viewEnd = viewBegin + viewPageCount - 1;
			}else{
				viewEnd = pageCount;
				viewBegin = pageCount - viewPageCount +1> 1 ? pageCount - viewPageCount +1: 1; 
			}
			hasPrev = viewBegin > 1;
			hasNext = viewEnd < pageCount;
			viewPageNos =new ArrayList<>();
			for(int i = viewBegin;i <= viewEnd;++i){
				viewPageNos.add(i);
			}
		}else{
			this.subResultL = null;
		}
	}
	public int getNresults() {
		return nresults;
	}
	public void setNresults(int nresults) {
		this.nresults = nresults;
	}
	public int getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
	public String getReqStr() {
		return reqStr;
	}
	public void setReqStr(String reqStr) {
		this.reqStr = reqStr;
	}
	public int getResultCount() {
		return resultCount;
	}
	public List<Result> getResultL() {
		return resultL;
	}
	public List<Result> getSubResultL() {
		return subResultL;
	}
	public int getPageCount() {
		return pageCount;
	}
	public int getViewPageCount() {
		return viewPageCount;
	}
	public List<Integer> getViewPageNos() {
		return viewPageNos;
	}
	public boolean isHasPrev() {
		return hasPrev;
	}
	public boolean isHasNext() {
		return hasNext;
	}
	
}
