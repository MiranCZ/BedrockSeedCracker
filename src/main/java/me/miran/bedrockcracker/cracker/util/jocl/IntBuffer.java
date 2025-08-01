package me.miran.bedrockcracker.cracker.util.jocl;

import org.jocl.Pointer;
import org.jocl.Sizeof;
import org.jocl.cl_command_queue;
import org.jocl.cl_context;

import static org.jocl.CL.CL_TRUE;
import static org.jocl.CL.clEnqueueReadBuffer;

/**
 * Specific {@link Buffer} implementation for ints.
 *
 * @see Buffer
 */
public class IntBuffer extends Buffer {

    private final int[] memHolder;

    public IntBuffer(cl_context context, int size) {
        super(context, size);
        this.memHolder = new int[(int) size];
    }

    public IntBuffer(cl_context context, int[] input) {
        super(context, Pointer.to(input), input.length);
        this.memHolder = input;
    }

    public int[] read(cl_command_queue queue) {
        clEnqueueReadBuffer(queue, memPointer, CL_TRUE, 0,
                (long) size * getTypeSize(), Pointer.to(memHolder), 0, null, null);

        return memHolder;
    }

    @Override
    protected int getTypeSize() {
        return Sizeof.cl_int;
    }
}
