package org.joyrest.ittest.path;

import static com.jayway.restassured.RestAssured.given;

import org.joyrest.ittest.AbstractBasicIT;
import org.joyrest.model.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;

public class Path3RouteIT extends AbstractBasicIT {

	@Before
	public void setUp() {
		RestAssured.port = port;
	}

	@Test
	public void testPath_EmptyPath() {
		given()
			.contentType(ContentType.ANY)
			.when()
			.post("/ittest/path3")
			.then()
			.statusCode(HttpStatus.NO_CONTENT.code());
	}

}
