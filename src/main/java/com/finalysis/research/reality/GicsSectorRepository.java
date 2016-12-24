package com.finalysis.research.reality;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GicsSectorRepository extends JpaRepository<GicsSector, Integer> {

    GicsSector findByCodeAndName(String code, String name);

    GicsSector findByName(String name);
}
