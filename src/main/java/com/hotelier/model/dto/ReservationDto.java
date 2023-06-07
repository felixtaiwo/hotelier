package com.hotelier.model.dto;

import com.hotelier.model.entity.Reservation;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Setter @Getter @NotNull
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReservationDto {
    Long id;
    RoomDto room;
    GuestDto guest;
    @NotNull
    LocalDate arrivalDate;
    @NotNull
    LocalDate departureDate;
    ReservationStatus status = ReservationStatus.OPEN;
    @NotNull
    String purposeOfStay;

    public static ReservationDto fromReservation(@NotNull Reservation reservation){
        ReservationDto reservationDto = new ReservationDto();
        reservationDto.setId(reservation.getId());
        reservationDto.setGuest(GuestDto.fromGuest(reservation.getGuest(),null));
        reservationDto.setRoom(RoomDto.fromRoom(reservation.getRoom(), null));
        reservationDto.setStatus(ReservationStatus.getStatus(reservation.getStatus()));
        reservationDto.setArrivalDate(reservation.getCheckInDate());
        reservationDto.setDepartureDate(reservation.getCheckOutDate());
        return reservationDto;
    }
    public static Reservation toReservation(ReservationDto reservationDto){
        Reservation reservation = new Reservation();
        reservation.setCheckInDate(reservationDto.getArrivalDate());
        reservation.setCheckOutDate(reservationDto.getDepartureDate());
        reservation.setPurposeOfStay(reservationDto.getPurposeOfStay());
        reservation.setStatus(ReservationStatus.getStatus(reservationDto.getStatus()));
        return reservation;

    }

    public enum ReservationStatus {
        OPEN,
        DECLINED,
        CHECKED_IN,
        CHECKED_OUT,
        CONFIRMED,
        CANCELLED,
        LOCKED;

        public static Integer getStatus(ReservationStatus status){
            switch (status){
                case OPEN:
                    return 0;
                case CONFIRMED:
                    return 1;
                case CHECKED_IN:
                    return 2;
                case CHECKED_OUT:
                    return 3;
                case CANCELLED:
                    return 4;
                case LOCKED:
                    return -1;
                default:
                    return 5;
            }
        }
        public static ReservationStatus getStatus(Integer status){
            switch (status){
                case 0:
                    return OPEN;
                case 1:
                    return CONFIRMED;
                case 2:
                    return CHECKED_IN;
                case 3:
                    return CHECKED_OUT;
                case 4:
                    return CANCELLED;
                case -1:
                    return LOCKED;
                default:
                    return DECLINED;
            }
        }
    }
}
