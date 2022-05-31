package com.terator.repository;

import com.terator.model.inductionLoops.Fixture;
import com.terator.model.inductionLoops.FixtureId;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FixtureRepository extends CrudRepository<Fixture, FixtureId> {

    Iterable<Fixture> findBysegmentId(Integer rseg_id);
}
