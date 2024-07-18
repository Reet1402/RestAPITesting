package tests;

import java.util.HashMap;

import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import base.TestBase;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class GetAPITest extends TestBase {

	String boardId;
	String listId;
	String cardId;
	RequestSpecification request;

	@BeforeTest
	public void Setup() {
		RestAssured.baseURI = prop.getProperty("url");
		request = RestAssured.given();
		request.queryParam("key", prop.getProperty("APIkey"));
		request.queryParam("token", prop.getProperty("APItoken"));
	}

	@Test(priority = 1)
	public void createNewBoard() {
		request.queryParam("name", "JAY");
		request.header("Content-Type", "application/json; charset=utf-8");

		// sent post request and get response
		Response response = request.post("boards/");

		System.out.println("-----------------------------------Create New Board--------------------------------------");
		// status code
		int statusCode = response.statusCode();
		System.out.println("Status Code:" + statusCode);
		Assert.assertEquals(statusCode, 200);
		System.out.println(
				"----------------------------------------------------------------------------------------------------");

		// status Line
		String statusLine = response.statusLine();
		System.out.println(statusLine);
		System.out.println(
				"----------------------------------------------------------------------------------------------------");

		// Response body
		JsonPath jsonBody = response.jsonPath();
		String boardName = jsonBody.getString("name");
		System.out.println(boardName);
		System.out.println(
				"----------------------------------------------------------------------------------------------------");

		boardId = jsonBody.getString("id");
		System.out.println(boardId);
		System.out.println(
				"----------------------------------------------------------------------------------------------------");

		// this line will print the whole json response as we get in postman
		System.out.println(response.prettyPrint());

	}

	@Test(priority = 2)
	public void verifyBoardCreatedByHasMap() {
		// TODO Auto-generated method stub

		System.out.println("-----------------------------------------------------------");
		System.out.println("-----------------------Verify Board Created----------------");
		// hasmap store data as key values
		HashMap<String, String> queryParms = new HashMap<String, String>();
		request.queryParams(queryParms);

		// adding path parameter
		request.pathParam("boardID", boardId);

		// send get request and get response
		Response response = request.get("boards/{boardID}/");

		// Status code
		int statusCode = response.statusCode();
		System.out.println("Status code: " + statusCode);
		Assert.assertEquals(statusCode, 200);
	}

	@Test(priority = 3)
	public void createNewList() {

		System.out.println("-----------------------Create New List---------------------");
		RestAssured.baseURI = "https://api.trello.com/1";

		Response response = RestAssured.given().header("Content-Type", "application/json").pathParam("id", boardId)
				.queryParam("name", prop.getProperty("ListName")).queryParam("key", prop.getProperty("APIkey"))
				.queryParam("token", prop.getProperty("APItoken")).when().post("/boards/{id}/lists/").then()
				.statusCode(200) // Verify the status code
				.extract().response();

		JsonPath jsonBody = response.jsonPath();
		listId = jsonBody.getString("id");
		System.out.println(listId);
		// Print the response for debugging

		System.out.println("Response: " + response.asString());
	}

	@Test(priority = 4)
	public void verifyListCreated() {

		System.out.println("-----------------------Verify List Created---------------------");

		Response getResponse = RestAssured.given().header("Content-Type", "application/json").pathParam("id", boardId)
				.queryParam("key", prop.getProperty("APIkey")).queryParam("token", prop.getProperty("APItoken")).when()
				.get("/boards/{id}/lists").then().statusCode(200) // Verify the status code for fetching lists
				.extract().response();

		System.out.println("Get Response: " + getResponse.asString());

		// Step 3: Verify the created list is present in the response
		boolean listCreated = getResponse.jsonPath().getList("name").contains(prop.getProperty("ListName"));
		Assert.assertTrue(listCreated, "The list was not created successfully.");
	}

	@Test(priority = 5)
	public void createNewCard() {

		System.out.println("-----------------------Create New Card---------------------");

		Response createCardResponse = RestAssured.given().header("Content-Type", "application/json")
				.queryParam("name", prop.getProperty("CardName")).queryParam("idList", listId)
				.queryParam("key", prop.getProperty("APIkey")).queryParam("token", prop.getProperty("APItoken")).when()
				.post("/cards").then().statusCode(200) // Verify the status code
				.extract().response();

		JsonPath jsonBody = createCardResponse.jsonPath();
		cardId = jsonBody.getString("id");
		System.out.println(cardId);
		// Print the response for debugging
		System.out.println("Create Card Response: " + createCardResponse.asString());

		// Step 5: Verify the created card is present in the response
		String cardId = createCardResponse.jsonPath().getString("id");
		Assert.assertNotNull(cardId, "The card was not created successfully.");

	}

	@Test(priority = 6)
	public void deleteBoard() {
		System.out.println("-----------------------Delete Board---------------------");

		Response deleteBoardResponse = RestAssured.given().header("Content-Type", "application/json")
				.pathParam("id", boardId).queryParam("key", prop.getProperty("APIkey"))
				.queryParam("token", prop.getProperty("APItoken")).when().delete("/boards/{id}").then().statusCode(200) // Verify
																														// the
																														// status
																														// code
				.extract().response();

		System.out.println("Delete Board Response: " + deleteBoardResponse.asString());

	}

	@Test(priority = 7)
	public void verifyDeleteBoard() {
		System.out.println("-----------------------Verify Board Deleted---------------------");

		Response verifyDeleteBoardResponse = RestAssured.given().header("Content-Type", "application/json")
				.pathParam("id", boardId).queryParam("key", prop.getProperty("APIkey"))
				.queryParam("token", prop.getProperty("APItoken")).when().delete("/boards/{id}").then().statusCode(404) // Verify
																														// the
																														// status
																														// code
				.extract().response();

		System.out.println("Delete Board Response: " + verifyDeleteBoardResponse.asString());
		int statusCode = verifyDeleteBoardResponse.statusCode();
		System.out.println("Status Code:" + statusCode);
		Assert.assertEquals(statusCode, 404);
	}

}
