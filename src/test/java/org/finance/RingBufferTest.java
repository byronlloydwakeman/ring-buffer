package org.finance;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class RingBufferTest
{
    private RingBuffer ringBuffer;

    @BeforeEach
    void beforeEach()
    {
        ringBuffer = new RingBuffer();
    }

    @Test
    void shouldBeCorrectBufferSizeOf1()
    {
        final long input = 0L;
        ringBuffer.offer( input );
        assertEquals( 1L, ringBuffer.size() );
    }

    @Test
    void shouldBeCorrectBufferSizeOf0()
    {
        assertEquals( 0L, ringBuffer.size() );
    }

    @Test
    void shouldBeCorrectBufferSizeOf0AfterPoll()
    {
        final long input = 0L;
        ringBuffer.offer( input );
        assertEquals( 1L, ringBuffer.size() );
        ringBuffer.poll();
        assertEquals( 0L, ringBuffer.size() );
    }

    // Offer
    // - When the buffer is empty
    // - Partially filled
    // - Filled
    // Edge cases for the value itself.
    // 0L, Long.MIN_VALUE, Long.MAX_VALUE, a random negative value, a random positive value.

    @Test
    void shouldAcceptOfferWhenBufferIsEmpty()
    {
        final long input = 0L;
        final boolean result = ringBuffer.offer( input );
        // Operation should be successful
        assertTrue( result );
        // The buffer should now have one value inside
        assertEquals( 1, ringBuffer.size() );
        // The value should be retrievable via Poll
        assertEquals( input, ringBuffer.poll() );
    }

    @Test
    void shouldAcceptOfferWhenBufferIsPartiallyFilled()
    {
        final long input = 1L;
        final boolean result = ringBuffer.offer( input );
        // Operation should be successful
        assertTrue( result );
        // The buffer should now have one value inside
        assertEquals( 1L, ringBuffer.size() );

        final boolean result2 = ringBuffer.offer( input );
        // Operation should be successful again
        assertTrue( result2 );
        // The buffer should now have two values inside
        assertEquals( 2L, ringBuffer.size() );
    }

    @Test
    void shouldRejectOfferWhenBufferIsFull()
    {
        final long input = 1L;
        for (int i = 0; i < 8; i++)
        {
            final boolean result = ringBuffer.offer( input );
            assertTrue( result );
        }

        // Should now be full
        assertEquals( 8L, ringBuffer.size() );

        final boolean result2 = ringBuffer.offer( input );
        assertFalse( result2 );

        // Should still be full
        assertEquals( 8L, ringBuffer.size() );
    }

    @Test
    void shouldAcceptOfferWhenBufferIsFullButIsFollowedByAPoll()
    {
        for (long i = 0; i < 8; i++)
        {
            final boolean result = ringBuffer.offer( i );
            assertTrue( result );
        }

        // Should now be full
        assertEquals( 8L, ringBuffer.size() );

        final boolean result2 = ringBuffer.offer( 8L );
        assertFalse( result2 );

        // Should still be full
        assertEquals( 8L, ringBuffer.size() );

        // Poll a value
        final long pollResult = ringBuffer.poll();

        assertEquals( 0L, pollResult );

        // Should now be max size - 1
        assertEquals( 7L, ringBuffer.size() );

        // We should be able to do another offer
        assertTrue( ringBuffer.offer( 9L ) );

        // Should now be full again
        assertEquals( 8L, ringBuffer.size() );
    }

    @Test
    void shouldSuccessfullyPollValuesInOrder()
    {
        for (long i = 0; i < 8; i++)
        {
            final boolean result = ringBuffer.offer( i );
            assertTrue( result );
        }

        // Should now be full
        assertEquals( 8L, ringBuffer.size() );

        for (long i = 0; i < 8; i++)
        {
            final long pollResult = ringBuffer.poll();
            assertEquals( i, pollResult );
        }

        // Should now be empty
        assertEquals( 0L, ringBuffer.size() );
    }

    @Test
    void shouldThrowAnExceptionWhenPollingAnEmptyBuffer()
    {
        assertThrows( IllegalStateException.class, () -> ringBuffer.poll() );
    }

    @Test
    void shouldThrowAnExceptionWhenPollingAnEmptyBufferAfterOfferingAndPolling()
    {
        final long input = 10L;
        final boolean offerResult = ringBuffer.offer( input );
        assertTrue( offerResult );

        final long pollResult = ringBuffer.poll();
        assertEquals( input, pollResult );

        assertThrows( IllegalStateException.class, () -> ringBuffer.poll() );
    }

    @Test
    void shouldKeepOrderAcrossManyWrapArounds()
    {
        for ( long i = 0; i < 1000; i++ )
        {
            assertTrue( ringBuffer.offer( i ) );
            assertEquals( i, ringBuffer.poll() );
        }
    }
}
