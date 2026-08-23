package lol.hub.lazykeys;

import java.time.Duration;
import java.time.Instant;

// Fires at most once per interval; used to throttle the twerk toggle.
public final class IntervalGate {

    private final Duration interval;
    private Instant last = Instant.now().minus(Duration.ofSeconds(1));

    public IntervalGate(Duration interval) {
        this.interval = interval;
    }

    public boolean tryFire() {
        var now = Instant.now();
        if (last.plus(interval).isAfter(now)) return false;
        last = now;
        return true;
    }

}
