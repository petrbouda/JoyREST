/*
 * Copyright 2015 Petr Bouda
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.joyrest.model.http;

import static java.util.Objects.*;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.joyrest.exception.type.RestException.internalServerErrorSupplier;

import java.util.*;

public final class MediaType {

	public static final MediaType FORM_URLENCODED = new MediaType("application", "x-www-form-urlencoded");
	public static final MediaType JSON = new MediaType("application", "json");
	public static final MediaType OCTET_STREAM = new MediaType("application", "octet-stream");
	public static final MediaType MULTIPART_FORM_DATA = new MediaType("multipart", "form-data");
	public static final MediaType HTML = new MediaType("text", "html");
	public static final MediaType PLAIN_TEXT = new MediaType("text", "plain");
	public static final MediaType WILDCARD = new MediaType("*", "*");

	public static final MediaType TEXT_XML = new MediaType("text", "xml");
	public static final MediaType XML = new MediaType("application", "xml");
	public static final MediaType ATOM_XML = new MediaType("application", "atom+xml", XML);
	public static final MediaType XHTML_XML = new MediaType("application", "xhtml+xml", XML);

	public static final MediaType SERIALIZATION_JAVA = new MediaType("serialization", "java");
	public static final MediaType SERIALIZATION_HESSIAN = new MediaType("serialization", "hessian");

	private static final Map<String, MediaType> BASIC_MEDIA_TYPE;

	static {
		BASIC_MEDIA_TYPE = new HashMap<>();
		BASIC_MEDIA_TYPE.put("json", JSON);
		BASIC_MEDIA_TYPE.put("xml", XML);
	}

	private final String type;

	private final String subType;

	private Optional<MediaType> processingType;

	private Map<String, String> params;

	private MediaType(String type, String subType) {
		this(type, subType, null);
	}

	private MediaType(String type, String subType, MediaType processingType) {
		this(type, subType, null, null);
	}

	private MediaType(String type, String subType, MediaType processingType, Map<String, String> params) {
		this.type = type;
		this.subType = subType;
		this.processingType = Optional.ofNullable(processingType);
		this.params = params;
	}

	public static List<MediaType> list(String mediaTypes) {
		return Arrays.stream(mediaTypes.split(","))
			.filter(Objects::nonNull)
			.map(String::trim)
			.distinct()
			.map(MediaType::of)
			.collect(toList());
	}

	public static MediaType of(String mediaType) {
		if (isNull(mediaType) || mediaType.isEmpty()) {
			return WILDCARD;
		}

		Map<String, String> params = new HashMap<>();
		if (mediaType.contains(";")) {
			String[] mediaTypeSplit = mediaType.split(";");
			mediaType = mediaTypeSplit[0];
			params = Arrays.stream(mediaTypeSplit)
				.skip(1)
				.filter(Objects::nonNull)
				.filter(param -> param.contains("="))
				.map(String::trim)
				.map(MediaType::paramToNameValue)
				.collect(toMap(NameValueEntity::getName, NameValueEntity::getValue));
		}

		String[] typeSplit = mediaType.split("/");
		if (typeSplit.length != 2)
			throw internalServerErrorSupplier(
					String.format("Invalid MediaType [%s]", mediaType)).get();

		MediaType processingType = null;
		if (typeSplit[1].contains("+")) {
			String[] processingSplit = typeSplit[1].split("\\+");
			if (processingSplit.length == 2) {
				processingType = BASIC_MEDIA_TYPE.get(processingSplit[1]);
			}
		}

		return new MediaType(typeSplit[0], typeSplit[1], processingType, params);
	}

	private static final NameValueEntity<String, String> paramToNameValue(String param) {
		String[] paramSplit = param.split("=");
		return nonNull(paramSplit[0]) && nonNull(paramSplit[1]) ?
				new NameValueEntity<>(paramSplit[0].toLowerCase(), paramSplit[1].toLowerCase()) : null;
	}

	public String get() {
		return type + "/" + subType;
	}

	public String getType() {
		return type;
	}

	public String getSubType() {
		return subType;
	}

	public Optional<MediaType> getProcessingType() {
		return processingType;
	}

	public Map<String, String> getParams() {
		return params;
	}

	public Optional<String> getParam(String name) {
		requireNonNull(name, "Param name cannot be null.");
		return Optional.ofNullable(params.get(name.toLowerCase()));
	}

	@Override
	public int hashCode() {
		return Objects.hash(type, subType);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		final MediaType other = (MediaType) obj;
		return Objects.equals(this.type, other.type)
				&& Objects.equals(this.subType, other.subType);
	}

	@Override
	public String toString() {
		return type + "/" + subType;
	}
}
