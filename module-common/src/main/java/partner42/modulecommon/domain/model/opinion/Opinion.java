package partner42.modulecommon.domain.model.opinion;

import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import partner42.modulecommon.domain.model.BaseEntity;
import partner42.modulecommon.domain.model.article.Article;
import partner42.modulecommon.domain.model.member.Member;

@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "OPINION", uniqueConstraints = {
    @UniqueConstraint(name = "API_ID_UNIQUE", columnNames = {"apiId"})
})
@Entity
public class Opinion extends BaseEntity {
    //********************************* static final 상수 필드 *********************************/

    /**
     * email 뒤에 붙는 문자열
     */


    /********************************* PK 필드 *********************************/

    /**
     * 기본 키
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "OPINION_ID")
    private Long id;


    /********************************* PK가 아닌 필드 *********************************/

    @Builder.Default
    @Column(nullable = false, updatable = false, length = 50)
    private final String apiId = UUID.randomUUID().toString();


    @Lob
    @Column(nullable = false)
    private String content;

    private String parentApiId;

    @Column(nullable = false, updatable = false)
    private Integer level;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isDeleted = false;

    /********************************* 비영속 필드 *********************************/

    /********************************* 연관관계 매핑 *********************************/

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_AUTHOR_ID", nullable = false, updatable = false)
    private Member memberAuthor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ARTICLE_ID", nullable = false, updatable = false)
    private Article article;


    /********************************* 연관관계 편의 메서드 *********************************/
    public void setMemberAuthorAndArticle(Member memberAuthor, Article article) {
        this.memberAuthor = memberAuthor;
        this.article = article;
        article.getOpinions().add(this);
    }

    /********************************* 생성 메서드 *********************************/

    public static Opinion of(String content, Member memberAuthor, Article article, String parentApiId, Integer level) {
        Opinion opinion = Opinion.builder()
            .content(content)
            .memberAuthor(memberAuthor)
            .parentApiId(parentApiId)
            .level(level)
            .build();
        opinion.setMemberAuthorAndArticle(memberAuthor, article);
        return opinion;
    }



    /********************************* 비니지스 로직 *********************************/

    public void updateContent(String content) {
        this.content = content;
    }

    public void recoverableDelete() {
        this.isDeleted = true;
    }

    public Integer nextLevel() {
        return this.level + 1;
    }

}


