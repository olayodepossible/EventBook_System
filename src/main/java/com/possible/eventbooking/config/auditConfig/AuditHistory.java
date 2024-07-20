package com.possible.eventbooking.config.auditConfig;


import java.util.Date;
import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import static javax.persistence.TemporalType.TIMESTAMP;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AuditHistory<U> {

    @CreatedBy
    @Column(name = "createdBy")
    protected U createdBy;

    @Column(name = "createdOn")
    @CreatedDate
    @Temporal(TIMESTAMP)
    protected Date createdDate;

    @Column(name = "modifiedBy")
    @LastModifiedBy
    protected U lastModifiedBy;

    @Column(name = "modifiedOn")
    @LastModifiedDate
    @Temporal(TIMESTAMP)
    protected Date lastModifiedDate;

}

