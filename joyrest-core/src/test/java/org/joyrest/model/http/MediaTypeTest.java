package org.joyrest.model.http;

import org.joyrest.exception.type.RestException;
import org.junit.Test;

import static org.junit.Assert.*;

public class MediaTypeTest {

	@Test
	public void media_type_null_and_empty() {
		MediaType mediaType = MediaType.of(null);
		assertEquals(MediaType.WILDCARD, mediaType);

		MediaType mediaType2 = MediaType.of("");
		assertEquals(MediaType.WILDCARD, mediaType2);
	}

	@Test
	public void content_type_without_charset() {
		MediaType contentType = MediaType.of("text/html");

		assertEquals("text/html", contentType.get());
		assertEquals("text", contentType.getType());
		assertEquals("html", contentType.getSubType());
		assertEquals(0, contentType.getParams().size());
	}

	@Test
	public void content_type_with_charset() {
		MediaType contentType = MediaType.of("text/html; charset=utf-8");

		assertEquals("text/html", contentType.get());
		assertEquals("text", contentType.getType());
		assertEquals("html", contentType.getSubType());
		assertEquals(1, contentType.getParams().size());
		assertEquals("utf-8", contentType.getParam("charset").get());
	}

	@Test
	public void content_type_with_processing_type() {
		MediaType contentType = MediaType.of("application/vnd.github+json");

		assertEquals("application/vnd.github+json", contentType.get());
		assertEquals("application", contentType.getType());
		assertEquals("vnd.github+json", contentType.getSubType());
		assertEquals("application/json", contentType.getProcessingType().get().get());
	}

	@Test(expected = RestException.class)
	public void media_type_wrong_one_part() {
		MediaType.of("application");
	}

	@Test(expected = RestException.class)
	public void media_type_wrong_three_parts() {
		MediaType.of("application/json/xml");
	}

}