package lol.hub.lazykeys;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

public final class IntervalGate {

    private final Duration interval;
    private final Clock clock;
    private Instant last;

    public IntervalGate(Duration interval) {
        this(interval, Clock.systemUTC());
    }

    IntervalGate(Duration interval, Clock clock) {
        this.interval = interval;
        this.clock = clock;
        this.last = clock.instant().minus(Duration.ofSeconds(1));
    }

    public boolean tryFire() {
        var now = clock.instant();
        if (last.plus(interval).isAfter(now)) return false;
        last = now;
        return true;
    }

}
