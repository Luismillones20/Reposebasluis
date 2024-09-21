package org.e2e.labe2e02;

import jakarta.persistence.Column;
import org.e2e.labe2e02.AbstractContainerBaseTest;
import org.e2e.labe2e02.coordinate.domain.Coordinate;
import org.e2e.labe2e02.passenger.domain.Passenger;
import org.e2e.labe2e02.passenger.infrastructure.PassengerRepository;
import org.e2e.labe2e02.ride.domain.Ride;
import org.e2e.labe2e02.ride.domain.Status;
import org.e2e.labe2e02.ride.infrastructure.RideRepository;
import org.e2e.labe2e02.user.domain.Role;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;


import java.awt.print.Pageable;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.e2e.labe2e02.user.domain.Role.PASSENGER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@DataJpaTest
@ActiveProfiles("test")
public class RideRepositoryTest extends AbstractContainerBaseTest {

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private PassengerRepository passengerRepository;

    @BeforeEach
    public void setUp() {
        rideRepository.deleteAll();
    }

    @Test // Verifica si se puede crear un viaje.
    public void createRide() {

        Ride ride = new Ride();
        ride.setOriginName("Gracias Dios por solo poner");
        ride.setDestinationName("Estos 4 datos como no nulleables <3");
        ride.setPrice(12.2);
        ride.setStatus(Status.ACCEPTED);



        Ride savedRide = rideRepository.save(ride);

        assertThat(savedRide).isNotNull();
        assertThat(savedRide.getId()).isNotNull();
        assertThat(savedRide.getOriginName()).isNotNull();
        assertThat(savedRide.getDestinationName()).isNotNull();
        assertThat(savedRide.getStatus()).isNotNull();

        Optional<Ride> retrievedRide = rideRepository.findById(ride.getId());

        Assertions.assertTrue(retrievedRide.isPresent());


    }

        @Test
        public void findById_ShouldReturnRide_WhenRideExists() {
            Ride ride = new Ride();
            ride.setOriginName("Me volveré loco");
            ride.setDestinationName("No, mentira <3");
            ride.setPrice(12.2);
            ride.setStatus(Status.ACCEPTED);


            Ride savedRide = rideRepository.save(ride);


            Optional<Ride> foundRide = rideRepository.findById(savedRide.getId());


            assertThat(foundRide).isPresent();
            assertThat(foundRide.get().getId()).isEqualTo(savedRide.getId());
        }


    @Test
    public void findById_ShouldReturnEmpty_WhenRideDoesNotExist() {
        // Act
        Optional<Ride> foundRide = rideRepository.findById(999L); // Un ID que no existe

        // Assert
        assertThat(foundRide).isNotPresent();
    }

    @Test
    public void deleteById_ShouldRemoveRide_WhenRideExists() {

        Ride rideToDelete = new Ride();
        rideToDelete.setOriginName("Origen");
        rideToDelete.setDestinationName("Destino");
        rideToDelete.setPrice(50.0);
        rideToDelete.setStatus(Status.REQUESTED);


        Ride savedRide = rideRepository.save(rideToDelete);


        Optional<Ride> foundRideBeforeDeletion = rideRepository.findById(savedRide.getId());
        assertThat(foundRideBeforeDeletion).isPresent();


        rideRepository.deleteById(savedRide.getId());


        Optional<Ride> foundRideAfterDeletion = rideRepository.findById(savedRide.getId());
        assertThat(foundRideAfterDeletion).isNotPresent();
    }

    @Test
    public void deleteById_ShouldNotThrowException_WhenRideDoesNotExist() {

        Long nonExistentRideId = 999L;


        Optional<Ride> rideBeforeDeletion = rideRepository.findById(nonExistentRideId);
        assertThat(rideBeforeDeletion).isNotPresent();

        rideRepository.deleteById(nonExistentRideId);

        Optional<Ride> rideAfterDeletion = rideRepository.findById(nonExistentRideId);
        assertThat(rideAfterDeletion).isNotPresent();
    }

    @Test
    public void findAllByArrivalDateAndDestinationCoordinates_ShouldReturnRide_WhenMatchFound() {

        Coordinate destinationCoordinates = new Coordinate(42.1234, -71.9876);


        LocalDateTime arrivalDate = LocalDateTime.of(2023, 9, 15, 10, 0);


        Ride ride = new Ride();
        ride.setOriginName("Origen");
        ride.setDestinationName("Destino");
        ride.setArrivalDate(arrivalDate);
        ride.setDestinationCoordinates(destinationCoordinates);
        ride.setPrice(45.0);
        ride.setStatus(Status.COMPLETED);


        Ride savedRide = rideRepository.save(ride);


        Optional<Ride> foundRide = rideRepository.findAllByArrivalDateAndDestinationCoordinates(arrivalDate, destinationCoordinates);


        assertThat(foundRide).isPresent();
        assertThat(foundRide.get().getId()).isEqualTo(savedRide.getId());
        assertThat(foundRide.get().getDestinationCoordinates()).isEqualTo(destinationCoordinates);
        assertThat(foundRide.get().getArrivalDate()).isEqualTo(arrivalDate);
    }

    @Test
    public void findAllByArrivalDateAndDestinationCoordinates_ShouldReturnEmpty_WhenDateDoesNotMatch() {
        Coordinate destinationCoordinates = new Coordinate(42.1234, -71.9876);

        LocalDateTime correctArrivalDate = LocalDateTime.of(2023, 9, 15, 10, 0);

        Ride ride = new Ride();
        ride.setOriginName("Origen de Prueba");
        ride.setDestinationName("Destino de Prueba");
        ride.setArrivalDate(correctArrivalDate);
        ride.setDestinationCoordinates(destinationCoordinates);
        ride.setPrice(45.0);
        ride.setStatus(Status.COMPLETED);

        rideRepository.save(ride);

        LocalDateTime incorrectArrivalDate = LocalDateTime.of(2023, 9, 20, 12, 0);

        Optional<Ride> foundRide = rideRepository.findAllByArrivalDateAndDestinationCoordinates(incorrectArrivalDate, destinationCoordinates);

        assertThat(foundRide).isNotPresent();
    }

    @Test
    public void findAllByPassengerIdAndStatus_ShouldReturnRides_WhenRidesExist() {

        Passenger passenger = new Passenger();



        passenger.setId(1L);
        passenger.setRole(PASSENGER);
        passenger.setFirstName("Mateito");
        passenger.setLastName("Roel");
        passenger.setEmail("Emailcito@email.com");
        passenger.setPassword("contrasenita");
        passenger.setPhoneNumber("234234");
        passenger.setCreatedAt(ZonedDateTime.now());

        passengerRepository.save(passenger);

        Ride ride1 = new Ride();
        ride1.setOriginName("Origen 1");
        ride1.setDestinationName("Destino 1");
        ride1.setPrice(50.0);
        ride1.setStatus(Status.COMPLETED);
        ride1.setPassenger(passenger);

        Ride ride2 = new Ride();
        ride2.setOriginName("Origen 2");
        ride2.setDestinationName("Destino 2");
        ride2.setPrice(75.0);
        ride2.setStatus(Status.COMPLETED);
        ride2.setPassenger(passenger);

        rideRepository.save(ride1);
        rideRepository.save(ride2);

        Page<Ride> foundRides = rideRepository.findAllByPassengerIdAndStatus(passenger.getId(), Status.COMPLETED, PageRequest.of(0, 10));

        assertThat(foundRides.getTotalElements()).isEqualTo(2);
        assertThat(foundRides.getContent()).extracting("passenger").containsOnly(passenger);
        assertThat(foundRides.getContent()).extracting("status").containsOnly(Status.COMPLETED);
    }


    @Test
    public void findAllByPassengerIdAndStatus_ShouldReturnEmpty_WhenNoRidesExist() {

        Passenger passenger = new Passenger();
        passenger.setId(1L);
        passenger.setRole(PASSENGER);
        passenger.setFirstName("Mateito");
        passenger.setLastName("Roel");
        passenger.setEmail("Emailcito@email.com");
        passenger.setPassword("contrasenita");
        passenger.setPhoneNumber("234234");
        passenger.setCreatedAt(ZonedDateTime.now());

        passengerRepository.save(passenger);


        Page<Ride> foundRides = rideRepository.findAllByPassengerIdAndStatus(passenger.getId(), Status.COMPLETED, PageRequest.of(0, 10));


        assertThat(foundRides.getTotalElements()).isEqualTo(0);
        assertThat(foundRides.getContent()).isEmpty();
    }






}

