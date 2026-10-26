package org.finance;

/**
 * Ring Buffer class, a low latency buffer implementation.
 */
public class RingBuffer
{
    private final int CAPACITY = 8;
    
    private long offerCount = 0;
    private long pollCount = 0;
    private final long[] buffer;

    public RingBuffer()
    {
        this.buffer = new long[ CAPACITY ];
    }

    /**
     * "Offers" a value to the buffer. If the buffer is full, the value will be dropped and false will be returned.
     *
     * @param value The long to add to the buffer.
     * @return Whether the value was successfully added to the buffer.
     */
    public boolean offer( long value )
    {
        int index = ( int ) ( offerCount & ( CAPACITY - 1 ) );
        if ( size() < CAPACITY )
        {
            buffer[ index ] = value;
            offerCount++;
            return true;
        }
        return false;
    }

    /**
     * Acting like a FIFO queue, Poll() will return the oldest value in the buffer and remove it from the buffer.
     * <p>
     * Will throw an IllegalStateException is there are no values to poll in the array. The calling code
     * should check that the size is > 0 before calling
     *
     * @return the next value in the buffer.
     */
    public long poll()
    {
        if ( size() > 0 )
        {
            final int index = ( int ) ( pollCount & ( CAPACITY - 1 ) );
            final long value = buffer[ index ];
            pollCount++;
            return value;
        }
        throw new IllegalStateException( "No values to poll" );
    }

    public long size()
    {
        return offerCount - pollCount;
    }
}
