package com.seoul.openproject.partner.domain.model;


import java.io.Serializable;
import javax.persistence.MappedSuperclass;


@MappedSuperclass
public abstract class BaseVersionEntity extends BaseTimeVersionEntity implements Serializable {

    private String createdBy;
    private String updatedBy;

}