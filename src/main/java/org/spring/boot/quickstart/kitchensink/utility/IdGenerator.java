package org.spring.boot.quickstart.kitchensink.utility;

import org.springframework.stereotype.Component;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class IdGenerator {

    private final AtomicLong counter = new AtomicLong();

    public long getNextId() {
        return counter.incrementAndGet();
    }
}