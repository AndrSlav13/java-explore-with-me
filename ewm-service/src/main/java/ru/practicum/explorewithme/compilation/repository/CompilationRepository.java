package ru.practicum.explorewithme.compilation.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.explorewithme.compilation.model.Compilation;

import java.util.List;
import java.util.Map;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {
    @Query("select comp from Compilation as comp " +
            "where :pinned = comp.pinned ")
    List<Compilation> findAllUsePinnedFlag(@Param("pinned") Boolean pinned, Pageable pg);

    @Query(value = "select distinct p.event_id from " +
            "(select * from compilations as comp join event_compilation as evco on comp.id = evco.compilation_id " +
            "where comp.id in :compils" +
            ") as p ", nativeQuery = true)
    List<Long> findIdsEventsByIdsCompils(@Param("compils") List<Long> compils);

    @Query(value = "select distinct p.compilation_id, p.event_id from " +
            "(select * from compilations as comp join event_compilation as evco on comp.id = evco.compilation_id " +
            "where comp.id in :compils" +
            ") as p ", nativeQuery = true)
    Map<Long, List<Long>> test(@Param("compils") List<Long> compils);
}