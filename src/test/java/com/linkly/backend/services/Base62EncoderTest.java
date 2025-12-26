package com.linkly.backend.services;

import com.linkly.backend.utils.Base62Encoder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Base62EncoderTest {

    @Test
    void testEncode() {
        // Test basic encoding
        assertEquals("g8", Base62Encoder.encode(1000));
        assertEquals("gc", Base62Encoder.encode(1004));
        assertEquals("0", Base62Encoder.encode(0));
    }

    @Test
    void testDecode() {
        // Test decoding
        assertEquals(1000, Base62Encoder.decode("g8"));
        assertEquals(1004, Base62Encoder.decode("gc"));
        assertEquals(0, Base62Encoder.decode("0"));
    }

    @Test
    void testEncodeDecode() {
        // Test round-trip
        long original = 123456;
        String encoded = Base62Encoder.encode(original);
        long decoded = Base62Encoder.decode(encoded);

        assertEquals(original, decoded);
    }

}
