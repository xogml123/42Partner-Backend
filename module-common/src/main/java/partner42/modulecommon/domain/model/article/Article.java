package partner42.modulecommon.domain.model.article;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import partner42.modulecommon.domain.model.BaseEntity;
import partner42.modulecommon.domain.model.match.ContentCategory;
import partner42.modulecommon.domain.model.matchcondition.ArticleMatchCondition;
import partner42.modulecommon.domain.model.member.Member;
import partner42.modulecommon.domain.model.opinion.Opinion;
import partner42.modulecommon.exception.ErrorCode;
import partner42.modulecommon.exception.InvalidInputException;
import partner42.modulecommon.exception.NotAuthorException;
import partner42.modulecommon.exception.UnmodifiableArticleException;


@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "ARTICLE", uniqueConstraints = {
    @UniqueConstraint(name = "API_ID_UNIQUE", columnNames = {"apiId"}),
})
@Entity
public class Article extends BaseEntity{
    //********************************* static final 상수 필드 *********************************/

    /********************************* PK 필드 *********************************/

    /**
     * 기본 키
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ARTICLE_ID")
    private Long id;

    /********************************* PK가 아닌 필드 *********************************/

    /**
     * 비관적 락 사용.
     * article에 참여, 취소, 확정 시 lost update 및 write skew를 방지하기 위해 필요.
     * MYSQL의 default REPEATABLE READ를 사용하고 있기 때문에 위 두가지 문제가 발생할 수 있음.
     * OptmisticLockException이 발생하면, 해당 article에 대한 트랜잭션을 다시 시작해야 함.
     * AOP를 활용하거나, Controller단에서 단순히 한번더 호출.
     */
    @Version
    private Long version;


    /**
     * AUTH에 필요한 필드
     */

    @Builder.Default
    @Column(nullable = false, updatable = false, length = 50)
    private final String apiId = UUID.randomUUID().toString();

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private String title;

    //longtext형
    @Lob
    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private Boolean anonymity;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isComplete = false;

    @Builder.Default
    @Column(nullable = false)
    private Integer participantNum = 1;

    @Column(nullable = false)
    private Integer participantNumMax;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isDeleted = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private ContentCategory contentCategory;

    /********************************* 비영속 필드 *********************************/

    /********************************* 연관관계 매핑 *********************************/


    @Builder.Default
    @OneToMany(mappedBy = "article", fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE,
        CascadeType.PERSIST})
    private List<ArticleMatchCondition> articleMatchConditions = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "article", fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE,
        CascadeType.PERSIST})
    private List<ArticleMember> articleMembers = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "article", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<Opinion> opinions = new ArrayList<>();

    /********************************* 연관관계 편의 메서드 *********************************/

    /********************************* 생성 메서드 *********************************/

    public static Article of(LocalDate date, String title, String content, Boolean anonymity,
        Integer participantNumMax, ContentCategory contentCategory
    ) {
        return Article.builder()
            .date(date)
            .title(title)
            .content(content)
            .anonymity(anonymity)
            .participantNumMax(participantNumMax)
            .contentCategory(contentCategory)
            .build();

    }

    /********************************* 비니지스 로직 *********************************/


    public void update(LocalDate date, String title, String content, Boolean anonymity,
        Integer participantNumMax, ContentCategory contentCategory,
        List<ArticleMatchCondition> articleMatchConditions) {
        verifyDeleted();
        verifyCompleted();
        verifyChangeableParticipantNumMax(participantNumMax);
        this.date = date;
        this.title = title;
        this.content = content;
        this.anonymity = anonymity;
        this.participantNumMax = participantNumMax;
        this.contentCategory = contentCategory;
        this.getArticleMatchConditions().clear();
        for (ArticleMatchCondition articleMatchCondition : articleMatchConditions) {
            articleMatchCondition.setArticle(this);
        }
    }

    public Member getAuthorMember() {
        return this.getArticleMembers().stream()
            .filter(ArticleMember::getIsAuthor)
            .map(ArticleMember::getMember)
            .findFirst().orElseThrow(() -> (
                new IllegalStateException(ErrorCode.NO_AUTHOR.getMessage())
            ));
    }

    public List<Member> getParticipatedMembers() {
        return this.getArticleMembers().stream()
            .filter((articleMember) ->
                !articleMember.getIsAuthor())
            .map(ArticleMember::getMember)
            .collect(Collectors.toList());
    }
    public boolean isDateToday() {
        return this.date.isEqual(LocalDate.now());

    }
    private void verifyChangeableParticipantNumMax(Integer participantNumMax) {
        if (this.participantNum > participantNumMax) {
            throw new InvalidInputException(ErrorCode.NOT_CHANGEABLE_PARTICIPANT_NUM_MAX);
        }
    }

    private void verifyDeleted() {
        if (this.isDeleted) {
            throw new UnmodifiableArticleException(ErrorCode.DELETED_ARTICLE);
        }
    }

    private void verifyFull() {
        if (this.participantNum >= this.participantNumMax) {
            throw new UnmodifiableArticleException(ErrorCode.FULL_ARTICLE);
        }
    }

    private void verifyEmpty() {
        if (this.participantNum <= 1) {
            throw new UnmodifiableArticleException(ErrorCode.EMPTY_ARTICLE);
        }
    }

    private void verifyCompleted() {
        if (this.isComplete) {
            throw new UnmodifiableArticleException(ErrorCode.COMPLETED_ARTICLE);
        }
    }


    private void verifyParticipatedMember(Member member) {

        if (this.getArticleMembers().stream()
            .anyMatch((articleMember) ->
                articleMember.getMember().equals(member))) {
            throw new InvalidInputException(ErrorCode.ALREADY_PARTICIPATED_MEMBER);
        }
    }

    private void verifyUnParticipatedMember(Member member) {

        if (this.getArticleMembers().stream()
            .noneMatch((articleMember) ->
                articleMember.getMember().equals(member))) {
            throw new InvalidInputException(ErrorCode.NOT_PARTICIPATED_MEMBER);
        }
    }


    public void complete() {
        verifyDeleted();
        verifyCompleted();
        this.isComplete = true;
    }

    public ArticleMember participateMember(Member member) {
        verifyDeleted();
        verifyCompleted();
        verifyFull();
        verifyParticipatedMember(member);
        ArticleMember participateMember = ArticleMember.of(member, false, this);
        this.participantNum++;
        return participateMember;
    }

    public ArticleMember participateCancelMember(Member member) {
        verifyDeleted();
        verifyCompleted();
        verifyEmpty();
        verifyUnParticipatedMember(member);
        verifyAuthorMember(member);
        ArticleMember participateMember = this.getArticleMembers().stream()
            .filter((articleMember1) ->
                articleMember1.getMember().equals(member))
            .findFirst().orElseThrow(() -> (
                new InvalidInputException(ErrorCode.NOT_PARTICIPATED_MEMBER)
            ));
        this.getArticleMembers().remove(participateMember);
        this.participantNum--;
        return participateMember;
    }

    private void verifyAuthorMember(Member member) {
        if (this.getArticleMembers().stream()
            .anyMatch((articleMember) ->
                articleMember.getMember().equals(member) && articleMember.getIsAuthor())) {
            throw new InvalidInputException(ErrorCode.NOT_ALLOW_AUTHOR_MEMBER_DELETE);
        }
    }

    public void recoverableDelete(){
        verifyDeleted();
        this.isDeleted = true;
    }

    public boolean isAuthorMember(Member member) {
        return getAuthorMember().equals(member);
    }

    /********************************* Dto *********************************/

}
