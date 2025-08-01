package me.miran.bedrockcracker.cracker.util.jocl;

import org.jocl.*;

import static org.jocl.CL.*;


/**
 * Helper class to wrap usage of {@link cl_mem} and its read/write from arrays.
 *
 * @see IntBuffer
 * @see LongBuffer
 */
public abstract class Buffer {

    protected final cl_mem memPointer;
    protected final int size;

    public Buffer(cl_context context, int size) {
        this.memPointer = clCreateBuffer(context, CL_MEM_READ_WRITE, (long) getTypeSize() * size, null, null);
        this.size = size;
    }

    protected Buffer(cl_context context, Pointer inputPointer, int inputSize) {
        this.memPointer = clCreateBuffer(context, CL_MEM_USE_HOST_PTR, (long) getTypeSize() * inputSize, inputPointer, null);
        this.size = inputSize;
    }

    public void passAsArg(cl_kernel kernel, int argIndex) {
        clSetKernelArg(kernel, argIndex, Sizeof.cl_mem, Pointer.to(memPointer));
    }

    public void release() {
        clReleaseMemObject(memPointer);
    }

    protected abstract int getTypeSize();


}
