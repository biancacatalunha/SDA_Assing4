package com.example.catalunhab.type;

import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.Date;

/**
 * Object representing a book reservation
 *
 * Properties that don't map to class fields are ignored when serializing to a class annotated with this annotation.
 */

@IgnoreExtraProperties
public class Reservation {

    private String reservationId;
    private String userId;
    private String bookId;
    private Date pickUpDate;
    private Date returnDate;
    private String status;
    private Date reservedOn;

    /**
     * Default constructor required for calls to DataSnapshot.getValue(Reservation.class)
     */
    public Reservation() {}

    public Reservation(String reservationId, String userId, String bookId, Date pickUpDate, Date returnDate, String status, Date reservedOn) {
        this.reservationId = reservationId;
        this.userId = userId;
        this.bookId = bookId;
        this.pickUpDate = pickUpDate;
        this.returnDate = returnDate;
        this.status = status;
        this.reservedOn = reservedOn;
    }

    public String getReservationId() {
        return reservationId;
    }

    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public Date getPickUpDate() {
        return pickUpDate;
    }

    public void setPickUpDate(Date pickUpDate) {
        this.pickUpDate = pickUpDate;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getReservedOn() {
        return reservedOn;
    }

    public void setReservedOn(Date reservedOn) {
        this.reservedOn = reservedOn;
    }
}
