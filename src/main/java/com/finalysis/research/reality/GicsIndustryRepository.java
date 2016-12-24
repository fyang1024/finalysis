package com.finalysis.research.reality;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GicsIndustryRepository extends JpaRepository<GicsIndustry, Integer> {

    GicsIndustry findByCodeAndName(String code, String name);

    GicsIndustry findByName(String name);
}
