package com.ecoaccess.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;
import com.ecoaccess.model.Entities.FareQuote;
import com.ecoaccess.model.Enums.ServiceType;

class CalculationTest {
    private final BookingService service = new BookingService();

    @Test
    void wheelchairUsesRoundedGst() {
        FareQuote q = service.quote(ServiceType.WHEELCHAIR, 1, null, null, null);
        assertEquals(50, q.base());
        assertEquals(3, q.tax());
        assertEquals(53, q.payable());
    }

    @Test
    void porterCalculation() {
        FareQuote q = service.quote(ServiceType.PORTER, 1, 3, 60, null);
        assertEquals(180, q.base());
        assertEquals(189, q.gross());
    }

    @Test
    void couponCannotDiscountBeyondGross() {
        var c = new com.ecoaccess.model.Entities.Coupon("x", "EA", "p", "n", 100, 50, 50, com.ecoaccess.model.Enums.CouponStatus.ACTIVE, java.time.LocalDateTime.now(), java.time.LocalDateTime.now().plusHours(1), null, null, null);
        assertEquals(0, service.quote(ServiceType.WHEELCHAIR, 1, null, null, c).payable());
    }

    @Test
    void invalidPorterBagsFail() {
        assertThrows(RuntimeException.class, () -> service.quote(ServiceType.PORTER, 1, 21, 50, null));
    }
}
