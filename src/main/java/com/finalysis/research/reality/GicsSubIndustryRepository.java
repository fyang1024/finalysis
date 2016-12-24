package com.finalysis.research.reality;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GicsSubIndustryRepository extends JpaRepository<GicsSubIndustry, Integer> {

    GicsSubIndustry findByCodeAndName(String code, String name);

}
