package partner42.modulecommon.domain.model.user;


import partner42.modulecommon.domain.model.BaseEntity;
import partner42.modulecommon.domain.model.BaseTimeEntity;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Singular;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PRIVATE)
@Getter
@Table(name="ROLE")
@Entity
public class Role extends BaseEntity implements Serializable {

    //********************************* static final 상수 필드 *********************************/


    /********************************* PK 필드 *********************************/

    /**
     * 기본 키
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ROLE_ID")
    private Long id;

    /********************************* PK가 아닌 필드 *********************************/
    @Builder.Default
    @Column(nullable = false, updatable = false, length = 50)
    private final String apiId = UUID.randomUUID().toString();


    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private RoleEnum value;

    /********************************* 비영속 필드 *********************************/

    /********************************* 연관관계 매핑 *********************************/

    @Singular
    @ManyToMany( fetch = FetchType.EAGER)
    @JoinTable(name = "ROLE_AUTHORITY",
        joinColumns = {
            @JoinColumn(name = "ROLE_ID", referencedColumnName = "ROLE_ID", nullable = false)},
        inverseJoinColumns = {
            @JoinColumn(name = "AUTHORITY_ID", referencedColumnName = "AUTHORITY_ID", nullable = false)})
    private final Set<Authority> authorities = new HashSet<>();

    /********************************* 생성 메서드 *********************************/
    public static Role of(RoleEnum value) {
        return Role.builder()
            .value(value)
            .build();
    }

    /********************************* 비니지스 로직 *********************************/

    public void addAuthorities(Authority... authorities) {
        this.authorities.addAll(Arrays.stream(authorities).collect(Collectors.toSet()));
    }


}
