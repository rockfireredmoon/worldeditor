/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.icemoon.worldeditor.resource;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.wicket.util.file.File;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.util.resource.AbstractResourceStream;
import org.apache.wicket.util.resource.IFixedLocationResourceStream;
import org.apache.wicket.util.resource.ResourceStreamNotFoundException;
import org.apache.wicket.util.time.Time;

/**
 * A FileResourceStream is an IResource implementation for Commons VFS file
 * objects.
 * 
 * @see org.apache.wicket.util.resource.IResourceStream
 * @see org.apache.wicket.util.watch.IModifiable
 * @author Jonathan Locke
 */
// TODO Wicket 1.6 - make #file mandatory. Args.notNull(file).
public class FileObjectResourceStream extends AbstractResourceStream implements IFixedLocationResourceStream {
	private static final long serialVersionUID = 1L;

	/** Any associated file */
	private final FileObject file;

	/** Resource stream */
	private transient InputStream inputStream;

	/**
	 * Constructor.
	 * 
	 * @param file {@link File} containing resource
	 */
	public FileObjectResourceStream(final FileObject file) {
		this.file = file;
	}

	/**
	 * Closes this resource.
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException {
		if (inputStream != null) {
			inputStream.close();
			inputStream = null;
		}
	}

	@Override
	public String getContentType() {
		String contentType = null;
		if (file != null) {
			try {
				contentType = file.getContent().getContentInfo().getContentType();
			} catch (FileSystemException e) {
				throw new RuntimeException(e);
			}
		}
		return contentType;
	}

	/**
	 * @return The file this resource resides in, if any.
	 */
	public FileObject getFile() {
		return file;
	}

	/**
	 * @return A readable input stream for this resource. The same input stream
	 *         is returned until <tt>FileResourceStream.close()</tt> is invoked.
	 * 
	 * @throws ResourceStreamNotFoundException
	 */
	public InputStream getInputStream() throws ResourceStreamNotFoundException {
		if (inputStream == null) {
			try {
				inputStream = file.getContent().getInputStream();
			} catch (FileSystemException e) {
				throw new ResourceStreamNotFoundException("Resource " + file + " could not be found", e);
			}
		}

		return inputStream;
	}

	/**
	 * @see org.apache.wicket.util.watch.IModifiable#lastModifiedTime()
	 * @return The last time this resource was modified
	 */
	@Override
	public Time lastModifiedTime() {
		if (file != null) {
			try {
				return Time.millis(file.getContent().getLastModifiedTime());
			} catch (FileSystemException e) {
				throw new RuntimeException(e);
			}
		}
		return null;
	}

	@Override
	public String toString() {
		if (file != null) {
			return file.toString();
		}
		return "";
	}

	@Override
	public Bytes length() {
		if (file != null) {
			try {
				return Bytes.bytes(file.getContent().getSize());
			} catch (FileSystemException e) {
				throw new RuntimeException(e);
			}
		}
		return null;
	}

	public String locationAsString() {
		if (file != null) {
			return file.getName().getFriendlyURI();
		}
		return null;
	}
}
