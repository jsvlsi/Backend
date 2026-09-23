package com.ecoaccess.model;

import java.time.*;
import com.ecoaccess.model.Enums.*;

public final class Entities {
    private Entities() { }
    public record Passenger(String id, String name, String mobile, String email, String passwordHash, int points) { }
    public record Staff(String id, String employeeId, String name, String passwordHash, String jobRole, StaffStatus status) { }
    public record Admin(String id, String name, String email, String passwordHash) { }
    public record Ticket(String pnr, String train, LocalDate date, LocalTime time, String station, String platform, String origin, String destination, String coach, String className) { }
    public record Journey(String id, String passengerId, Ticket ticket, boolean valid, LocalDateTime validatedAt) { }
    public record Booking(String id, String passengerId, String passenger, ServiceType service, String station, String platform, String pickup, String drop, LocalDate date, LocalTime time, int fare, int grossFare, int discount, String couponCode, BookingStatus status, String staffId, String train, Integer bags, Integer weightRate, int passengerCount) { }
    public record Resource(String id, String station, ServiceType type, int quantity) { }
    public record Waste(String id, String passengerId, String passenger, WasteStatus status, int rewardPoints, String photoPath, LocalDateTime submittedAt, LocalDateTime reviewedAt, String remark, String station, String platform) { }
    public record Coupon(String id, String code, String passengerId, String passenger, int pointsRedeemed, int value, int remaining, CouponStatus status, LocalDateTime createdAt, LocalDateTime expiresAt, LocalDateTime usedAt, String usedFor, String usedOn) { }
    public record Redemption(String id, String passengerId, String passenger, String rewardName, int points, String couponCode, int couponValue, String status, LocalDate date, LocalDateTime createdAt, LocalDateTime expiresAt) { }
    public record CaseRecord(String id, ComplaintType type, String passengerId, String passenger, String bookingId, int rating, String subject, String description, String status, LocalDateTime createdAt) { }
    public record FareQuote(int base, int tax, int gross, int discount, int payable) { }
    public record BookingRequest(String pnr, String train, LocalDate date, LocalTime time, String station, ServiceType service, int passengerCount, Integer bags, Integer weightRate, String pickup, String drop, String couponCode, String paymentMethod, String cardName, String cardNumber, String cardExpiry, String cvv) { }
}
