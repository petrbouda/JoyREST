package org.joyrest.ittest;

import static com.jayway.restassured.RestAssured.given;

import org.joyrest.model.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;

public class RequestResponseDataIT extends AbstractBasicIT {

	@Before
	public void setUp() {
		RestAssured.port = port;
	}

	@Test
	public void postRouteTest() {
		given()
			.contentType(ContentType.JSON)
			.body(feedEntity)
			.when()
			.post("feeds")
			.then()
			.statusCode(HttpStatus.NO_CONTENT.code());
	}

}