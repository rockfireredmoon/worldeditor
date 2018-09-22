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
package org.icemoon.worldeditor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;

import javax.servlet.http.HttpServletResponse;

import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.request.resource.AbstractResource;
import org.apache.wicket.request.resource.ContentDisposition;
import org.apache.wicket.util.io.ByteArrayOutputStream;
import org.apache.wicket.util.io.IOUtils;
import org.apache.wicket.util.io.Streams;
import org.apache.wicket.util.time.Time;

public class FileResource extends AbstractResource {
	private static final long serialVersionUID = 1L;

	private final String contentType;
	private final Time lastModified = Time.now();
	private final String filename;
	private File file;

	public FileResource(final String contentType) {
		this(contentType, null, null);
	}

	public FileResource(final String contentType, File file) {
		this(contentType, file, null);
	}

	public FileResource(final String contentType, File file, final String filename) {
		this.contentType = contentType;
		this.file = file;
		this.filename = filename;
	}

	protected void configureResponse(final ResourceResponse response, final Attributes attributes) {
	}

	/**
	 * @see org.apache.wicket.request.resource.AbstractResource#newResourceResponse(org.apache.wicket.request.resource.IResource.Attributes)
	 */
	@Override
	protected ResourceResponse newResourceResponse(final Attributes attributes) {
		final ResourceResponse response = new ResourceResponse();

		String contentType = this.contentType;

		if (contentType == null) {
			if (filename != null) {
				contentType = URLConnection.getFileNameMap().getContentTypeFor(filename);
			}

			if (contentType == null) {
				contentType = "application/octet-stream";
			}
		}

		response.setContentType(contentType);
		response.setLastModified(lastModified);

		if (!file.exists()) {
			response.setError(HttpServletResponse.SC_NOT_FOUND);
		} else {
			response.setContentLength(file.length());

			if (response.dataNeedsToBeWritten(attributes)) {
				if (filename != null) {
					response.setFileName(filename);
					response.setContentDisposition(ContentDisposition.ATTACHMENT);
				} else {
					response.setContentDisposition(ContentDisposition.INLINE);
				}

				response.setWriteCallback(new WriteCallback() {
					@Override
					public void writeData(final Attributes attributes) {
						InputStream inputStream = null;
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						try {
							inputStream = new FileInputStream(file);
							Streams.copy(inputStream, baos);
							attributes.getResponse().write(baos.toByteArray());
						} catch (FileNotFoundException rsnfx) {
							throw new WicketRuntimeException(rsnfx);
						} catch (IOException iox) {
							throw new WicketRuntimeException(iox);
						} finally {
							IOUtils.closeQuietly(inputStream);
							IOUtils.closeQuietly(baos);
						}
					}
				});

				configureResponse(response, attributes);
			}
		}

		return response;
	}
}
