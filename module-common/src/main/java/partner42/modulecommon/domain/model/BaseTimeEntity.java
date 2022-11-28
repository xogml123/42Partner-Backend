package partner42.modulecommon.domain.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseTimeEntity implements Serializable {

    @CreatedDate
    @Column(updatable = false)
//    @Column(nullable = false, insertable = false, updatable = false,
//        columnDefinition = "datetime default CURRENT_TIMESTAMP")
//    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime createdAt;


    @LastModifiedDate
//    @Column(nullable = false, insertable = false, updatable = false,
//        columnDefinition = "datetime default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP")
//    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime updatedAt;


}