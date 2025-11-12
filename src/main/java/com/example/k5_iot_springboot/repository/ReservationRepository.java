package com.example.k5_iot_springboot.repository;

import com.example.k5_iot_springboot.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByTruckId(Long truckId);

    Optional<Reservation> findByIdAndTruckId(Long reservationId, Long truckId);
}

// findById() 는 이미 Reservation 자체의 id를 의미함
// JpaRepository<Reservation, Long> 을 상속했기에
// findById(Long id)/ deleteById(Long id) 이런것들은 Reservation의 PK 를 의미함.