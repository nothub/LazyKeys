package lol.hub.lazykeys;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IntervalGateTest {

    private static final Duration INTERVAL = Duration.ofMillis(125);

    @Test
    void firesOnFirstCall() {
        var gate = new IntervalGate(INTERVAL, new MutableClock(Instant.EPOCH));

        assertTrue(gate.tryFire());
    }

    @Test
    void doesNotFireAgainBeforeIntervalElapses() {
        var clock = new MutableClock(Instant.EPOCH);
        var gate = new IntervalGate(INTERVAL, clock);
        gate.tryFire();

        clock.advance(INTERVAL.minusMillis(1));

        assertFalse(gate.tryFire());
    }

    @Test
    void firesAgainOnceIntervalElapses() {
        var clock = new MutableClock(Instant.EPOCH);
        var gate = new IntervalGate(INTERVAL, clock);
        gate.tryFire();

        clock.advance(INTERVAL.plusMillis(1));

        assertTrue(gate.tryFire());
    }

    // Pins down the boundary as inclusive (elapsed >= interval fires),
    // matching standard rate-limiter semantics -- not an accident to be
    // silently reverted to a strict '>' by a future refactor.
    @Test
    void firesExactlyAtInterval() {
        var clock = new MutableClock(Instant.EPOCH);
        var gate = new IntervalGate(INTERVAL, clock);
        gate.tryFire();

        clock.advance(INTERVAL);

        assertTrue(gate.tryFire());
    }

    private static final class MutableClock extends Clock {

        private Instant now;

        MutableClock(Instant now) {
            this.now = now;
        }

        void advance(Duration duration) {
            now = now.plus(duration);
        }

        @Override
        public ZoneId getZone() {
            return ZoneOffset.UTC;
        }

        @Override
        public Clock withZone(ZoneId zone) {
            return this;
        }

        @Override
        public Instant instant() {
            return now;
        }

    }

}
