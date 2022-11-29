package partner42.moduleapi.aop;


import org.aspectj.lang.annotation.Pointcut;

public class PointCut {

    @Pointcut("execution(public * partner42.moduleapi.service.article.ArticleService.*(..))")
    public void allPublicArticleService() {

    }
}
