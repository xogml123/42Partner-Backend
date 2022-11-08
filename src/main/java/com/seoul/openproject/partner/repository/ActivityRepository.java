package com.seoul.openproject.partner.repository;

import com.seoul.openproject.partner.domain.model.activity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityRepository extends JpaRepository<Activity, Long> {


}
