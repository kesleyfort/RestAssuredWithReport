package smokeTests;

import com.aventstack.extentreports.testng.listener.ExtentIReporterSuiteClassListenerAdapter;
import com.google.gson.Gson;
import data.Note;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.springframework.boot.test.context.SpringBootTest;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.*;

@Listeners(ExtentIReporterSuiteClassListenerAdapter.class)
@SpringBootTest
public class Login {
    final static String ROOT_URI = "https://jsonplaceholder.typicode.com";
    final static String NOTE_REGEX = "\\{(\\r|\\n)  \"userId\": [0-9],\\n  \"id\": [0-9],\\n  \"title\": \"(.*)\",\\n  \"completed\": (false|true)\\n\\}";
    Gson gson = new Gson();
    private String NOTE_PATTERN = "{\"useridid\":%s,\"title\":\"%s\"\"completed\":%s}";

    @AfterMethod
    public void cleanUp() {
        delete(ROOT_URI + "/deleteAll");
    }


    @Test
    public void getNoteWithID_ShouldReturnNote() {
        when()
                .get(ROOT_URI + "/todos/1").
                then()
                .statusCode(200).assertThat().body(Matchers.matchesRegex(NOTE_REGEX));
    }

    @Test
    public void textNoteAdded_ReturnsNote() {
        Note note = new Note();
        note.setTitle("Meu deus do céu");
        note.setUserId(120);


        Response response = given()
                .contentType(ContentType.JSON)
                .body(gson.toJson(note)).
                        when()
                .post(ROOT_URI + "/posts");
        Assert.assertEquals(gson.fromJson(response.body().asString(), Note.class).toString(), note.toString());
    }

    @Test
    public void getNoteWithNonExistentId_ShouldReturn404() {
        when().get(ROOT_URI + "/posts/101").then().statusCode(404);
    }
}
