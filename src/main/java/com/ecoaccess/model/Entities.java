package com.ecoaccess.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import com.ecoaccess.model.Enums.BookingStatus;
import com.ecoaccess.model.Enums.ComplaintType;
import com.ecoaccess.model.Enums.CouponStatus;
import com.ecoaccess.model.Enums.ServiceType;
import com.ecoaccess.model.Enums.StaffStatus;
import com.ecoaccess.model.Enums.WasteStatus;

/**
 * Legacy compatibility container. New code should import one class per entity
 * from com.ecoaccess.model instead of using nested entity records.
 */
@Deprecated
public final class Entities {
    private Entities() { }

    public static class Passenger extends com.ecoaccess.model.Passenger {
        public Passenger(String id, String name, String mobile, String email, String passwordHash, int points) {
            super(id, name, mobile, email, passwordHash, points);
        }
        public String id() { return getId(); }
        public String name() { return getName(); }
        public String mobile() { return getMobile(); }
        public String email() { return getEmail(); }
        public String passwordHash() { return getPasswordHash(); }
        public int points() { return getPoints(); }
    }

    public static class Staff extends com.ecoaccess.model.Staff {
        public Staff(String id, String employeeId, String name, String passwordHash, String jobRole, StaffStatus status) {
            super(id, employeeId, name, passwordHash, jobRole, status);
        }
        public String id() { return getId(); }
        public String employeeId() { return getEmployeeId(); }
        public String name() { return getName(); }
        public String passwordHash() { return getPasswordHash(); }
        public String jobRole() { return getJobRole(); }
        public StaffStatus status() { return getStatus(); }
    }

    public static class Admin extends com.ecoaccess.model.Admin {
        public Admin(String id, String name, String email, String passwordHash) {
            super(id, name, email, passwordHash);
        }
        public String id() { return getId(); }
        public String name() { return getName(); }
        public String email() { return getEmail(); }
        public String passwordHash() { return getPasswordHash(); }
    }

    public static class Ticket extends com.ecoaccess.model.Ticket {
        public Ticket(String pnr, String train, LocalDate date, LocalTime time, String station, String platform, String origin, String destination, String coach, String className) {
            super(pnr, train, date, time, station, platform, origin, destination, coach, className);
        }
        public String pnr() { return getPnr(); }
        public String train() { return getTrain(); }
        public LocalDate date() { return getDate(); }
        public LocalTime time() { return getTime(); }
        public String station() { return getStation(); }
        public String platform() { return getPlatform(); }
        public String origin() { return getOrigin(); }
        public String destination() { return getDestination(); }
        public String coach() { return getCoach(); }
        public String className() { return getClassName(); }
    }

    public static class Journey extends com.ecoaccess.model.Journey {
        public Journey(String id, String passengerId, Ticket ticket, boolean valid, LocalDateTime validatedAt) {
            super(id, passengerId, ticket, valid, validatedAt);
        }
        public String id() { return getId(); }
        public String passengerId() { return getPassengerId(); }
        public Ticket ticket() { return (Ticket) getTicket(); }
        public boolean valid() { return isValid(); }
        public LocalDateTime validatedAt() { return getValidatedAt(); }
    }

    public static class Booking extends com.ecoaccess.model.Booking {
        public Booking(String id, String passengerId, String passenger, ServiceType service, String station, String platform, String pickup, String drop, LocalDate date, LocalTime time, int fare, int grossFare, int discount, String couponCode, BookingStatus status, String staffId, String train, Integer bags, Integer weightRate, int passengerCount) {
            super(id, passengerId, passenger, service, station, platform, pickup, drop, date, time, fare, grossFare, discount, couponCode, status, staffId, train, bags, weightRate, passengerCount);
        }
        public String id() { return getId(); }
        public String passengerId() { return getPassengerId(); }
        public String passenger() { return getPassenger(); }
        public ServiceType service() { return getService(); }
        public String station() { return getStation(); }
        public String platform() { return getPlatform(); }
        public String pickup() { return getPickup(); }
        public String drop() { return getDrop(); }
        public LocalDate date() { return getDate(); }
        public LocalTime time() { return getTime(); }
        public int fare() { return getFare(); }
        public int grossFare() { return getGrossFare(); }
        public int discount() { return getDiscount(); }
        public String couponCode() { return getCouponCode(); }
        public BookingStatus status() { return getStatus(); }
        public String staffId() { return getStaffId(); }
        public String train() { return getTrain(); }
        public Integer bags() { return getBags(); }
        public Integer weightRate() { return getWeightRate(); }
        public int passengerCount() { return getPassengerCount(); }
    }

    public static class Resource extends com.ecoaccess.model.Resource {
        public Resource(String id, String station, ServiceType type, int quantity) {
            super(id, station, type, quantity);
        }
        public String id() { return getId(); }
        public String station() { return getStation(); }
        public ServiceType type() { return getType(); }
        public int quantity() { return getQuantity(); }
    }

    public static class Waste extends com.ecoaccess.model.Waste {
        public Waste(String id, String passengerId, String passenger, WasteStatus status, int rewardPoints, String photoPath, LocalDateTime submittedAt, LocalDateTime reviewedAt, String remark, String station, String platform) {
            super(id, passengerId, passenger, status, rewardPoints, photoPath, submittedAt, reviewedAt, remark, station, platform);
        }
        public String id() { return getId(); }
        public String passengerId() { return getPassengerId(); }
        public String passenger() { return getPassenger(); }
        public WasteStatus status() { return getStatus(); }
        public int rewardPoints() { return getRewardPoints(); }
        public String photoPath() { return getPhotoPath(); }
        public LocalDateTime submittedAt() { return getSubmittedAt(); }
        public LocalDateTime reviewedAt() { return getReviewedAt(); }
        public String remark() { return getRemark(); }
        public String station() { return getStation(); }
        public String platform() { return getPlatform(); }
    }

    public static class Coupon extends com.ecoaccess.model.Coupon {
        public Coupon(String id, String code, String passengerId, String passenger, int pointsRedeemed, int value, int remaining, CouponStatus status, LocalDateTime createdAt, LocalDateTime expiresAt, LocalDateTime usedAt, String usedFor, String usedOn) {
            super(id, code, passengerId, passenger, pointsRedeemed, value, remaining, status, createdAt, expiresAt, usedAt, usedFor, usedOn);
        }
        public String id() { return getId(); }
        public String code() { return getCode(); }
        public String passengerId() { return getPassengerId(); }
        public String passenger() { return getPassenger(); }
        public int pointsRedeemed() { return getPointsRedeemed(); }
        public int value() { return getValue(); }
        public int remaining() { return getRemaining(); }
        public CouponStatus status() { return getStatus(); }
        public LocalDateTime createdAt() { return getCreatedAt(); }
        public LocalDateTime expiresAt() { return getExpiresAt(); }
        public LocalDateTime usedAt() { return getUsedAt(); }
        public String usedFor() { return getUsedFor(); }
        public String usedOn() { return getUsedOn(); }
    }

    public static class Redemption extends com.ecoaccess.model.Redemption {
        public Redemption(String id, String passengerId, String passenger, String rewardName, int points, String couponCode, int couponValue, String status, LocalDate date, LocalDateTime createdAt, LocalDateTime expiresAt) {
            super(id, passengerId, passenger, rewardName, points, couponCode, couponValue, status, date, createdAt, expiresAt);
        }
        public String id() { return getId(); }
        public String passengerId() { return getPassengerId(); }
        public String passenger() { return getPassenger(); }
        public String rewardName() { return getRewardName(); }
        public int points() { return getPoints(); }
        public String couponCode() { return getCouponCode(); }
        public int couponValue() { return getCouponValue(); }
        public String status() { return getStatus(); }
        public LocalDate date() { return getDate(); }
        public LocalDateTime createdAt() { return getCreatedAt(); }
        public LocalDateTime expiresAt() { return getExpiresAt(); }
    }

    public static class CaseRecord extends com.ecoaccess.model.CaseRecord {
        public CaseRecord(String id, ComplaintType type, String passengerId, String passenger, String bookingId, int rating, String subject, String description, String status, LocalDateTime createdAt) {
            super(id, type, passengerId, passenger, bookingId, rating, subject, description, status, createdAt);
        }
        public String id() { return getId(); }
        public ComplaintType type() { return getType(); }
        public String passengerId() { return getPassengerId(); }
        public String passenger() { return getPassenger(); }
        public String bookingId() { return getBookingId(); }
        public int rating() { return getRating(); }
        public String subject() { return getSubject(); }
        public String description() { return getDescription(); }
        public String status() { return getStatus(); }
        public LocalDateTime createdAt() { return getCreatedAt(); }
    }

    public static class FareQuote extends com.ecoaccess.model.FareQuote {
        public FareQuote(int base, int tax, int gross, int discount, int payable) {
            super(base, tax, gross, discount, payable);
        }
        public int base() { return getBase(); }
        public int tax() { return getTax(); }
        public int gross() { return getGross(); }
        public int discount() { return getDiscount(); }
        public int payable() { return getPayable(); }
    }

    public static class BookingRequest extends com.ecoaccess.model.BookingRequest {
        public BookingRequest(String pnr, String train, LocalDate date, LocalTime time, String station, ServiceType service, int passengerCount, Integer bags, Integer weightRate, String pickup, String drop, String couponCode, String paymentMethod, String cardName, String cardNumber, String cardExpiry, String cvv) {
            super(pnr, train, date, time, station, service, passengerCount, bags, weightRate, pickup, drop, couponCode, paymentMethod, cardName, cardNumber, cardExpiry, cvv);
        }
        public String pnr() { return getPnr(); }
        public String train() { return getTrain(); }
        public LocalDate date() { return getDate(); }
        public LocalTime time() { return getTime(); }
        public String station() { return getStation(); }
        public ServiceType service() { return getService(); }
        public int passengerCount() { return getPassengerCount(); }
        public Integer bags() { return getBags(); }
        public Integer weightRate() { return getWeightRate(); }
        public String pickup() { return getPickup(); }
        public String drop() { return getDrop(); }
        public String couponCode() { return getCouponCode(); }
        public String paymentMethod() { return getPaymentMethod(); }
        public String cardName() { return getCardName(); }
        public String cardNumber() { return getCardNumber(); }
        public String cardExpiry() { return getCardExpiry(); }
        public String cvv() { return getCvv(); }
    }
}
