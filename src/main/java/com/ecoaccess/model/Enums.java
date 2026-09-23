package com.ecoaccess.model;

public final class Enums {
    private Enums() { }
    public enum Role { PASSENGER, STAFF, ADMIN }
    public enum ServiceType { PORTER("Porter"), WHEELCHAIR("Wheelchair"), INTER_VEHICLE("Inter Vehicle");
        private final String label; ServiceType(String label) { this.label = label; }
        public String label() { return label; }
        public static ServiceType from(String value) { for (ServiceType s : values()) if (s.label.equalsIgnoreCase(value)) return s; throw new IllegalArgumentException("Unknown service"); }
    }
    public enum BookingStatus { BOOKED("Booked"), ASSIGNED("Assigned"), ACCEPTED("Accepted"), REACHED_PASSENGER("Reached Passenger"), SERVICE_STARTED("Service Started"), COMPLETED("Completed"), REJECTED("Rejected");
        private final String label; BookingStatus(String label) { this.label = label; } public String label() { return label; }
        public static BookingStatus from(String value) { for (BookingStatus s : values()) if(s.label.equalsIgnoreCase(value)) return s; throw new IllegalArgumentException("Unknown booking status"); }
    }
    public enum StaffStatus { AVAILABLE("Available"), UNAVAILABLE("Unavailable"); private final String label; StaffStatus(String l){label=l;} public String label(){return label;} }
    public enum WasteStatus { PENDING("pending"), ACCEPTED("accepted"), REJECTED("rejected"); private final String label; WasteStatus(String l){label=l;} public String label(){return label;} }
    public enum CouponStatus { ACTIVE("Active"), USED("Used"), EXPIRED("Expired"); private final String label; CouponStatus(String l){label=l;} public String label(){return label;} }
    public enum ComplaintType { FEEDBACK("Feedback"), COMPLAINT("Complaint"); private final String label; ComplaintType(String l){label=l;} public String label(){return label;} }
}
