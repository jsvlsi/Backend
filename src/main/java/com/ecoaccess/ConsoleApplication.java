package com.ecoaccess;

import java.time.*;
import java.util.*;

import com.ecoaccess.console.*;
import com.ecoaccess.dao.*;
import com.ecoaccess.exception.AppExceptions.*;
import com.ecoaccess.model.Entities.*;
import com.ecoaccess.model.Enums.*;
import com.ecoaccess.service.*;

public class ConsoleApplication {
    private final ConsoleInput in = new ConsoleInput();
    private final AuthService auth = new AuthService();
    private final BookingService booking = new BookingService();
    private final OperationsService ops = new OperationsService(auth.dao(), booking.dao(), booking.operations(), new CatalogDao());

    public static void main(String[] args) {
        new ConsoleApplication().run();
    }

    private void run() {
        while (true) {
            title("ECOACCESS RAILWAY ASSISTANCE");
            System.out.println("1. Register passenger\n2. Login\n3. Reset password\n0. Exit");
            try {
                switch (in.number("Choice: ")) {
                    case 1 -> register();
                    case 2 -> login();
                    case 3 -> reset();
                    case 0 -> {
                        return;
                    }
                    default -> System.out.println("Invalid choice. Please select an option from the menu.");
                }
            } catch (ApplicationException e) {
                error(e);
            } catch (Exception e) {
                System.out.println("Unable to complete the operation. Please try again.");
            }
            in.pause();
        }

    }

    private void register() {
        Passenger p = auth.register(in.text("Full name: "), in.text("Mobile: "), in.text("Email: "), in.text("Password: "), in.text("Confirm password: "), in.text("Demo OTP (123456): "));
        System.out.println("Registration successful. Welcome, " + p.name());
        passengerMenu(p);
    }

    private void login() {
        Role r = role();
        Object user = auth.login(r, in.text(r == Role.PASSENGER ? "Mobile: " : r == Role.STAFF ? "Employee ID: " : "Email: "), in.text("Password: "));
        System.out.println("Login successful.");
        if (user instanceof Passenger p) passengerMenu(p);
        else if (user instanceof Staff s) staffMenu(s);
        else adminMenu((Admin) user);
    }

    private void reset() {
        Role r = role();
        if (r == Role.ADMIN) throw new AuthorizationException("Admin password reset is not available.");
        auth.reset(r, in.text(r == Role.PASSENGER ? "Mobile: " : "Employee ID: "), in.text("New password: "));
        System.out.println("Password reset successful.");
    }

    private Role role() {
        System.out.println("1. Passenger\n2. Staff\n3. Admin");
        return switch (in.number("Role: ")) {
            case 1 -> Role.PASSENGER;
            case 2 -> Role.STAFF;
            case 3 -> Role.ADMIN;
            default -> throw new ValidationException("Invalid role.");
        };
    }

    private void passengerMenu(Passenger p) {
        while (true) {
            title("PASSENGER MENU — " + p.name());
            System.out.println("1 Dashboard  2 Validate journey  3 Create booking  4 My bookings\n5 Submit waste  6 Rewards  7 Feedback/complaint  8 Profile  0 Logout");
            try {
                switch (in.number("Choice: ")) {
                    case 1 -> dashboard(p);
                    case 2 -> {
                        Journey j = booking.validateJourney(p, in.text("PNR: "));
                        System.out.println("Journey validated: " + j.ticket().train() + " at " + j.ticket().station());
                    }
                    case 3 -> createBooking(p);
                    case 4 -> showBookings(booking.passengerBookings(p.id(), in.text("Search (blank for all): ")));
                    case 5 -> {
                        Waste w = ops.submitWaste(p, in.text("Photo path or PHOTO_CAPTURED: "));
                        System.out.println("Submitted for review: " + w.id());
                    }
                    case 6 -> {
                        rewards(p);
                        p = auth.dao().passengerById(p.id()).orElse(p);
                    }
                    case 7 -> caseMenu(p);
                    case 8 -> {
                        p = profile(p);
                    }
                    case 0 -> {
                        return;
                    }
                    default -> System.out.println("Invalid choice. Please select an option from the menu.");
                }
            } catch (ApplicationException e) {
                error(e);
            }
            in.pause();
        }
    }

    private void dashboard(Passenger p) {
        List<Booking> b = booking.passengerBookings(p.id(), "");
        long done = b.stream().filter(x -> x.status() == BookingStatus.COMPLETED).count();
        System.out.printf("Journey: %s | Upcoming: %d | Completed: %d | Points: %d%n", safeJourney(p), b.size() - done, done, p.points());
        showBookings(b);
    }

    private String safeJourney(Passenger p) {
        try {
            return booking.requireJourney(p.id()).ticket().pnr();
        } catch (ApplicationException e) {
            return "Not validated";
        }
    }

    private void createBooking(Passenger p) {
        System.out.println("Service: 1 Porter, 2 Wheelchair, 3 Inter Vehicle");
        ServiceType s = switch (in.number("Service: ")) {
            case 1 -> ServiceType.PORTER;
            case 2 -> ServiceType.WHEELCHAIR;
            case 3 -> ServiceType.INTER_VEHICLE;
            default -> throw new ValidationException("Select a service.");
        };
        Journey j = booking.requireJourney(p.id());
        String station = in.text("Station [" + j.ticket().station() + "]: ");
        if (station.isBlank()) station = j.ticket().station();
        System.out.println("Available now: " + booking.available(station, s));
        int count = s == ServiceType.PORTER ? 1 : in.number("Passenger count: ");
        Integer bags = s == ServiceType.PORTER ? in.number("Bags (1-20): ") : null;
        Integer rate = s == ServiceType.PORTER ? in.number("Weight rate (50/60/70/80): ") : null;
        String code = in.text("Coupon code (blank none): ");
        String method = in.text("Payment method (upi/card): ");
        String cn = "", no = "", ex = "", cvv = "";
        if (method.equalsIgnoreCase("card")) {
            cn = in.text("Card name: ");
            no = in.text("Card number: ");
            ex = in.text("MM/YY: ");
            cvv = in.text("CVV: ");
        }
        String bookingPnr = in.text("PNR [" + j.ticket().pnr() + "]: ");
        if (bookingPnr.isBlank()) bookingPnr = j.ticket().pnr();
        BookingRequest r = new BookingRequest(bookingPnr, j.ticket().train(), in.date("Service date (YYYY-MM-DD): "), in.time("Service time (HH:MM): "), station, s, count, bags, rate, in.text("Pickup: "), in.text("Drop: "), code, method, cn, no, ex, cvv);
        Booking b = booking.create(p, r);
        System.out.println("Booking created: " + b.id() + " | " + b.status().label() + " | Payable ₹" + b.fare());
    }

    private void rewards(Passenger p) {
        System.out.println("Points: " + p.points());
        showCoupons(ops.coupons(p.id(), ""));
        System.out.println("1 Redeem all points  2 Apply coupon to train PNR  0 Back");
        switch (in.number("Choice: ")) {
            case 1 -> {
                Coupon c = ops.redeem(p);
                System.out.println("Coupon generated: " + c.code() + " (₹" + c.value() + ")");
            }
            case 2 -> {
                Coupon c = ops.applyToTrain(p, in.text("Coupon code: "));
                System.out.println("Coupon applied; remaining ₹" + c.remaining());
            }
            case 0 -> {
            }
            default -> throw new ValidationException("Invalid choice.");
        }
    }

    private void caseMenu(Passenger p) {
        ComplaintType t = in.text("Type (Feedback/Complaint): ").equalsIgnoreCase("Complaint") ? ComplaintType.COMPLAINT : ComplaintType.FEEDBACK;
        CaseRecord x = ops.submitCase(p, t, t == ComplaintType.COMPLAINT ? in.text("Booking ID: ") : null, in.number("Rating (1-5): "), in.text("Subject: "), in.text("Description: "));
        System.out.println("Submitted: " + x.id() + " — " + x.status());
    }

    private Passenger profile(Passenger p) {
        System.out.println("Mobile (immutable): " + p.mobile());
        String n = in.text("New name (blank keep): "), e = in.text("New email (blank keep): ");
        if (n.isBlank() && e.isBlank()) return p;
        Passenger q = new Passenger(p.id(), n.isBlank() ? p.name() : com.ecoaccess.util.Validation.name(n), p.mobile(), e.isBlank() ? p.email() : com.ecoaccess.util.Validation.email(e), p.passwordHash(), p.points());
        auth.updatePassenger(q);
        System.out.println("Profile updated.");
        return q;
    }

    private void staffMenu(Staff s) {
        while (true) {
            s = auth.dao().staffByEmployeeId(s.employeeId()).orElse(s);
            title("STAFF MENU — " + s.name() + " [" + s.status().label() + "]");
            System.out.println("1 Dashboard  2 Assigned work  3 Advance service  4 Toggle availability  0 Logout");
            try {
                switch (in.number("Choice: ")) {
                    case 1 -> {
                        List<Booking> b = booking.staffBookings(s.employeeId(), "", "", "");
                        String employeeId = s.employeeId();
                        System.out.println("Assigned/unassigned: " + b.stream().filter(x -> x.status() != BookingStatus.COMPLETED).count() + " | Completed: " + b.stream().filter(x -> employeeId.equals(x.staffId()) && x.status() == BookingStatus.COMPLETED).count());
                    }
                    case 2 -> {
                        showBookings(booking.staffBookings(s.employeeId(), in.text("Search: "), "", in.text("Status (blank all): ")));
                        String id = in.text("Booking ID to accept/reject (blank back): ");
                        if (!id.isBlank()) {
                            boolean accept = in.text("Accept? (y/n): ").equalsIgnoreCase("y");
                            System.out.println("Now " + ops.staffAction(s, id, accept).status().label());
                        }
                    }
                    case 3 -> {
                        showBookings(booking.staffBookings(s.employeeId(), "", "", ""));
                        System.out.println("Now " + ops.advance(s, in.text("Own booking ID: ")).status().label());
                    }
                    case 4 -> {
                        ops.staffAvailability(s, s.status() != StaffStatus.AVAILABLE);
                        System.out.println("Availability updated.");
                    }
                    case 0 -> {
                        return;
                    }
                    default -> System.out.println("Invalid choice.");
                }
            } catch (ApplicationException e) {
                error(e);
            }
            in.pause();
        }
    }

    private void adminMenu(Admin a) {
        while (true) {
            title("ADMIN MENU — " + a.name());
            System.out.println("1 Dashboard 2 Staff 3 Inventory 4 Monitor bookings 5 Waste review\n6 Coupons 7 Redemptions 8 Complaints 0 Logout");
            try {
                switch (in.number("Choice: ")) {
                    case 1 -> adminDash();
                    case 2 -> staffAdmin();
                    case 3 -> inventory();
                    case 4 -> showBookings(booking.all(in.text("Search: "), in.text("Service filter blank/all: ")));
                    case 5 -> wasteAdmin();
                    case 6 -> showCoupons(ops.coupons(null, in.text("Search: ")));
                    case 7 -> redemptions();
                    case 8 -> complaints();
                    case 0 -> {
                        return;
                    }
                    default -> System.out.println("Invalid choice.");
                }
            } catch (ApplicationException e) {
                error(e);
            }
            in.pause();
        }
    }

    private void adminDash() {
        System.out.println("Bookings: " + booking.all("", null).size() + " | Assignment pending: " + booking.all("", null).stream().filter(b -> b.status() == BookingStatus.BOOKED || b.status() == BookingStatus.ASSIGNED).count() + " | Waste pending: " + ops.wastes(null, "").stream().filter(w -> w.status() == WasteStatus.PENDING).count() + " | Open complaints: " + ops.cases(null, true, "").stream().filter(c -> "Open".equals(c.status())).count());
    }

    private void staffAdmin() {
        List<Staff> x = auth.dao().staff(in.text("Search: "));
        x.forEach(s -> System.out.println(s.id() + " | " + s.employeeId() + " | " + s.name() + " | " + s.jobRole() + " | " + s.status().label()));
        System.out.println("1 Add 2 Edit name 3 Delete 0 Back");
        int c = in.number("Choice: ");
        if (c == 1) {
            Staff s = ops.addStaff(in.text("Name: "), in.text("Job role (blank Porter): "));
            System.out.println("Created " + s.employeeId() + " with default password Test@123");
        } else if (c == 2) {
            Staff s = auth.dao().staffById(in.text("Staff internal ID: ")).orElseThrow(() -> new NotFoundException("Staff not found."));
            ops.editStaff(s, in.text("New name: "));
        } else if (c == 3) {
            Staff s = auth.dao().staffById(in.text("Staff internal ID: ")).orElseThrow(() -> new NotFoundException("Staff not found."));
            ops.deleteStaff(s);
        }
    }

    private void inventory() {
        showResources(ops.resources(in.text("Search station: ")));
        System.out.println("1 Add/merge 2 Update 3 Remove 0 Back");
        int c = in.number("Choice: ");
        if (c == 1) {
            ServiceType t = in.text("Type (Wheelchair/Inter Vehicle): ").toLowerCase().startsWith("w") ? ServiceType.WHEELCHAIR : ServiceType.INTER_VEHICLE;
            ops.resource(in.text("Station: "), t, in.number("Quantity: "));
        } else if (c == 2) ops.updateResource(in.text("Resource ID: "), in.number("Quantity: "));
        else if (c == 3) booking.operations().deleteResource(in.text("Resource ID: "));
    }

    private void wasteAdmin() {
        List<Waste> w = ops.wastes(null, in.text("Search: "));
        w.forEach(x -> System.out.println(x.id() + " | " + x.passenger() + " | " + x.status().label() + " | " + x.submittedAt()));
        String id = in.text("Submission ID to review (blank back): ");
        if (!id.isBlank())
            System.out.println("Reviewed: " + ops.reviewWaste(id, in.text("Accept? (y/n): ").equalsIgnoreCase("y"), in.text("Remark if rejected: ")).status().label());
    }

    private void redemptions() {
        showRedemptions(ops.redemptions(in.text("Search: ")));
        String id = in.text("Pending redemption ID to decide (blank back): ");
        if (!id.isBlank()) {
            ops.decidePendingRedemption(id, in.text("Approve? (y/n): ").equalsIgnoreCase("y"));
            System.out.println("Redemption updated.");
        }
    }

    private void complaints() {
        List<CaseRecord> x = ops.cases(null, true, in.text("Search: "));
        x.stream().filter(c -> !"Closed".equals(c.status())).forEach(c -> System.out.println(c.id() + " | " + c.status() + " | " + c.subject()));
        String id = in.text("Complaint ID (blank back): ");
        if (!id.isBlank()) ops.complaintStatus(id, in.text("Close? y closes, n resolves: ").equalsIgnoreCase("y"));
    }

    private void showBookings(List<Booking> x) {
        page(x.stream().map(b -> b.id() + " | " + b.passenger() + " | " + b.service().label() + " | " + b.station() + " | " + b.status().label() + " | ₹" + b.fare()).toList());
    }

    private void showCoupons(List<Coupon> x) {
        page(x.stream().map(c -> c.code() + " | " + c.status().label() + " | ₹" + c.remaining() + " | expires " + c.expiresAt()).toList());
    }

    private void showRedemptions(List<Redemption> x) {
        page(x.stream().map(r -> r.id() + " | " + r.passenger() + " | " + r.couponCode() + " | " + r.status()).toList());
    }

    private void showResources(List<Resource> x) {
        page(x.stream().map(r -> r.id() + " | " + r.station() + " | " + r.type().label() + " | " + r.quantity()).toList());
    }

    private void page(List<String> x) {
        if (x.isEmpty()) {
            System.out.println("No records found.");
            return;
        }
        int pg = Math.max(1, in.number("Page (5 rows/page): ")), from = Math.min((pg - 1) * 5, x.size()), to = Math.min(from + 5, x.size());
        x.subList(from, to).forEach(System.out::println);
        System.out.println("Showing " + from + 1 + "-" + to + " of " + x.size());
    }

    private void title(String s) {
        System.out.println("\n========================================\n" + s + "\n========================================");
    }

    private void error(Exception e) {
        System.out.println("Unable to complete the operation.\nReason: " + e.getMessage());
    }
}
