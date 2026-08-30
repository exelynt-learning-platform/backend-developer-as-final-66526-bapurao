package com.example.booking.repository;

import com.example.booking.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.domain.*;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;

public interface ReservationRepository extends JpaRepository<Reservation, Long>, JpaSpecificationExecutor<Reservation> {
	@Query("select case when count(r)>0 then true else false end from Reservation r "
			+ "where r.resource.id=:resourceId and "
			+ "r.status <> com.example.booking.entity.ReservationStatus.CANCELLED "
			+ "and r.startTime < :endTime "
			+ "and r.endTime > :startTime "
			+ "and (:excludeId is null or r.id <> :excludeId)")
	
	boolean existsConflict(@Param("resourceId") Long resourceId, @Param("startTime") LocalDateTime startTime,
			@Param("endTime") LocalDateTime endTime, @Param("excludeId") Long excludeId);
}
