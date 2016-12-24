package com.finalysis.research.reality;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Fei on 23/08/2014.
 */

@Repository
public interface CompanyIndustryRepository extends JpaRepository<CompanyIndustry, Integer> {
}
