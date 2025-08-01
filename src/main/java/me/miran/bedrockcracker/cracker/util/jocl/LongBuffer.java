package me.miran.bedrockcracker.cracker.util.jocl;

import org.jocl.Pointer;
import org.jocl.Sizeof;
import org.jocl.cl_command_queue;
import org.jocl.cl_context;

import static org.jocl.CL.CL_TRUE;
import static org.jocl.CL.clEnqueueReadBuffer;

/**
 * Specific {@link Buffer} implementation for longs.
 *
 * @see Buffer
 */
public class LongBuffer extends Buffer {

    private final long[] memHolder;

    public LongBuffer(cl_context context, int size) {
        super(context, size);
        this.memHolder = new long[(int) size];
    }

    public LongBuffer(cl_context context, long[] input) {
        super(context, Pointer.to(input), input.length);
        this.memHolder = input;
    }

    public long[] read(cl_command_queue queue) {
        return read(queue, size);
    }

    /**
     * Reads a specific amount of memory from the buffer. Data beyond that amount are undefined.
     */
    public long[] read(cl_command_queue queue, int neededSize) {
        if (neededSize < 0 || neededSize > size) throw new IndexOutOfBoundsException();
        if (neededSize == 0) return memHolder;

        clEnqueueReadBuffer(queue, memPointer, CL_TRUE, 0,
                (long) neededSize * getTypeSize(), Pointer.to(memHolder), 0, null, null);

        return memHolder;
    }

    @Override
    protected int getTypeSize() {
        return Sizeof.cl_long;
    }
}
