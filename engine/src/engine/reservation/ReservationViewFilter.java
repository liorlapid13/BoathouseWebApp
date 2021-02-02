package engine.reservation;

import java.io.Serializable;

public enum ReservationViewFilter implements Serializable {
    ALL, UNCONFIRMED_ONLY, CONFIRMED_ONLY;
}
