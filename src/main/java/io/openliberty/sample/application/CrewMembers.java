/*******************************************************************************
* Copyright (c) 2023 IBM Corporation and others.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*     IBM Corporation - initial API and implementation
*******************************************************************************/
package io.openliberty.sample.application;

import java.util.List;
import java.util.stream.Stream;

import jakarta.data.page.Page;
import jakarta.data.page.Pageable;
import jakarta.data.repository.DataRepository;
import jakarta.data.repository.OrderBy;
import jakarta.data.repository.Repository;
import jakarta.data.repository.Save;
import jakarta.validation.Valid;

@Repository
public interface CrewMembers extends DataRepository<CrewMember, Integer> {
    @Save
    CrewMember save(@Valid CrewMember m);
    
    List<CrewMember> findByRank(Rank rank);

    Page<CrewMember> findByRank(Rank rank, Pageable pageRequest);
    
    List<CrewMember> findByShipSizeAndRank(Ship.Size size, Rank rank);

    @OrderBy("name")
    Stream<CrewMember> findAll();

    void deleteByCrewID(int crewID);

    void deleteAll();
}
