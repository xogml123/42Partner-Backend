package com.Seoul.OpenProject.Partner.domain.model.user;


import com.Seoul.OpenProject.Partner.domain.model.BaseEntity;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PRIVATE)
@Getter
@Table(name = "USER_ROLE")
@Entity
public class UserRole extends BaseEntity implements Serializable    {

    //********************************* static final 상수 필드 *********************************/


    /********************************* PK 필드 *********************************/

    /**
     * 기본 키
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ROLE_ID")
    private Long id;

    /********************************* PK가 아닌 필드 *********************************/


    /********************************* 비영속 필드 *********************************/


    /********************************* 연관관계 매핑 *********************************/


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="USER_ID", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="ROLE_ID", nullable = false)
    private Role role;

    /********************************* 연관관계 편의 메서드 *********************************/
    public void setUserAndAddUserRoleToUser(User user) {
        user.getUserRoles().add(this);
        this.user = user;
    }
    /********************************* 생성 메서드 *********************************/
    public static UserRole createUserRole(User user, Role role) {
        UserRole userRole = UserRole.builder()
            .role(role)
            .build();
        userRole.setUserAndAddUserRoleToUser(user);
        return userRole;
    }



    /********************************* 비니지스 로직 *********************************/


}