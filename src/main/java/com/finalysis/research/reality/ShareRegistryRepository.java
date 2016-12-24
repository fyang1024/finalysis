package com.finalysis.research.reality;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShareRegistryRepository extends JpaRepository<ShareRegistry, Integer> {

    ShareRegistry findByName(String name);

    List<ShareRegistry> findByAddress(String address);
}
