package com.seoul.openproject.partner.repository.article;

import com.seoul.openproject.partner.domain.model.article.Article;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface ArticleRepositoryCustom {

    Slice<Article> findSliceByCondition(Pageable pageable, ArticleSearch condition);
}
