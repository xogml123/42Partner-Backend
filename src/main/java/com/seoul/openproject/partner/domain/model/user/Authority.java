package com.seoul.openproject.partner.domain.model.user;


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
public class Authority implements Serializable {

    @Id
    @GeneratedValue
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
}
