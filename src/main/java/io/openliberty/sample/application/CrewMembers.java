package io.openliberty.sample.application;

import java.util.List;
import java.util.stream.Stream;

import jakarta.data.repository.DataRepository;
import jakarta.data.repository.OrderBy;
import jakarta.data.repository.Repository;
import jakarta.data.repository.Save;
import jakarta.validation.Valid;

@Repository
public interface CrewMembers extends DataRepository<CrewMember, String> {
    @Save
    CrewMember save(@Valid CrewMember m);
    
    List<CrewMember> findByRank(Rank rank);

    @OrderBy("name")
    Stream<CrewMember> findAll();

    void deleteByCrewID(String crewID);

    void deleteAll();
}
