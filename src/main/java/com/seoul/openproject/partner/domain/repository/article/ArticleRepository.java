package com.seoul.openproject.partner.domain.repository.article;

import com.seoul.openproject.partner.domain.model.article.Article;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article, Long> {

}
