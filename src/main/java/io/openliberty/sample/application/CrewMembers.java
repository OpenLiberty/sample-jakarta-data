package io.openliberty.sample.application;

import java.util.List;

import jakarta.data.repository.DataRepository;
import jakarta.data.repository.OrderBy;
import jakarta.data.repository.Repository;

@Repository
public interface CrewMembers extends DataRepository<CrewMember, String> {
    List<CrewMember> findByRank(String rank);

    @OrderBy("crewID")
    List<CrewMember> findAll();

    void deleteByCrewID(String crewID);

    void save(CrewMember a);
}
