package com.finalysis.research.reality;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Integer> {

    Company findByName(String name);

    Company findByWebsite(String website);

    Company findByRegisteredOfficeAddress(String address);
}
