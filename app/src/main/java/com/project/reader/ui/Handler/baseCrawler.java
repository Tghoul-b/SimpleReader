package com.project.reader.ui.Handler;

import com.project.reader.entity.SearchBookBean;

import org.jsoup.nodes.Document;

import java.util.List;

public interface baseCrawler {
    List<SearchBookBean> getSearchResult(String url,String sourceClass,String searchRule);
    List<SearchBookBean>  deal_with_doc(Document document,String key,String sourceClass,String searchRule);
    SearchBookBean getInfo(String url,SearchBookBean searchBookBean);
}
