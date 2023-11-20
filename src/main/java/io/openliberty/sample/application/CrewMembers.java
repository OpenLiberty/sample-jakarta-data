package io.openliberty.sample.application;

import java.util.List;
import java.util.stream.Stream;

import jakarta.data.repository.BasicRepository;
import jakarta.data.repository.OrderBy;
import jakarta.data.repository.Repository;

@Repository
public interface CrewMembers extends BasicRepository<CrewMember, String> {
    
    List<CrewMember> findByRank(String rank);

    @OrderBy("name")
    Stream<CrewMember> findAll();

    void deleteByCrewID(String crewID);
}
