package com.dotcms.storage;

import java.io.OutputStream;

/**
 * Simple method to write an object into the stream
 * @author jsanca
 */
@FunctionalInterface
public interface ObjectWriterDelegate {

    /**
     * Writes the object into the stream
     * @param stream {@link OutputStream}
     * @param object {@link Object}
     */
    public void write (final OutputStream stream, Object object);
}