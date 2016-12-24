package com.finalysis.research.reality;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GicsIndustryGroupRepository extends JpaRepository<GicsIndustryGroup, Integer> {

    GicsIndustryGroup findByName(String name);

    GicsIndustryGroup findByCodeAndName(String code, String name);

}
