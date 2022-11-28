package partner42.modulecommon.domain.model.user;


import javax.persistence.GenerationType;
import partner42.modulecommon.domain.model.BaseEntity;
import partner42.modulecommon.domain.model.BaseTimeEntity;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
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
@Table(name="AUTHORITY")
@Entity
public class Authority extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="AUTHORITY_ID")
    private Long id;

    @Builder.Default
    @Column(nullable = false, updatable = false, length = 50)
    private final String apiId = UUID.randomUUID().toString();


    @Column(nullable = false)
    private String permission;

    @Singular
    @ManyToMany(mappedBy = "authorities", fetch = FetchType.EAGER)
    private final Set<Role> roles = new HashSet<>();
    /********************************* 생성 메서드 *********************************/

    public static Authority of(String permission) {
        return Authority.builder()
                .permission(permission)
                .build();
    }

}
