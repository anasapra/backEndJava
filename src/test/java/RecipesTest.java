import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import net.javacrumbs.jsonunit.JsonAssert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static io.restassured.RestAssured.given;
import static net.javacrumbs.jsonunit.core.Option.IGNORING_ARRAY_ORDER;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


public class RecipesTest {
    private static final String API_KEY = "801c235e0d9a4f2fbcc2d9c11e9cf89c";

    public RecipesTest() throws Exception {
    }

    @BeforeAll
    static void beforeAll() {
        RestAssured.baseURI = "https://api.spoonacular.com/";
    }
    //1 мой+
    @Test
    void getRecipeWithQueryParametersPositiveTest() {
        given()
                .queryParam("apiKey", API_KEY)
                .queryParam("includeNutrition", "false")
                .pathParam("id", "716429")
                .when()
                .get("/recipes/{id}/information")
                .then()
                .statusCode(200);
    }
    //2 мой+
    @Test
    void getRecipePositiveTest() {
        given()
                .queryParam("apiKey", API_KEY)
                .queryParam("includeNutrition", "false")
                .pathParam("id", "716429")
                .when()
                .get("/recipes/{id}/information")
                .then()
                .statusCode(200);
    }
//тест из методички +
    @Test
    void addMealTest() {
        String id = given()
                .log()
                .all()
                .queryParam("hash", "a3da66460bfb7e62ea1c96cfa0b7a634a346ccbf")
                .queryParam("apiKey", API_KEY)
                .body("{\n"
                        + " \"date\": 1644881179,\n"
                        + " \"slot\": 1,\n"
                        + " \"position\": 0,\n"
                        + " \"type\": \"INGREDIENTS\",\n"
                        + " \"value\": {\n"
                        + " \"ingredients\": [\n"
                        + " {\n"
                        + " \"name\": \"1 banana\"\n"
                        + " }\n"
                        + " ]\n"
                        + " }\n"
                        + "}")

                .when()
                .post("/mealplanner/geekbrains/items")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .get("id")
                .toString();
    }
    //тест из методички +
    @Test
    void getRecipeWithBodyChecksAfterRequestPositiveTest() {
        JsonPath response = given()
                .queryParam("apiKey", API_KEY)
                .queryParam("includeNutrition", "false")
                .when()
                .get("https://api.spoonacular.com/recipes/716429/information")
                .body()
                .jsonPath();
        assertThat(response.get("vegetarian"), is(false));
        assertThat(response.get("vegan"), is(false));
        assertThat(response.get("license"), equalTo("CC BY-SA 3.0"));
        assertThat(response.get("pricePerServing"), equalTo(163.15F));
        assertThat(response.get("extendedIngredients[0].aisle"), equalTo("Milk, Eggs, Other Dairy"));
    }
    //тест из методички +
    @Test
    void getRecipeWithBodyChecksInGivenPositiveTest() {
        given()
                .log()
                .all()
                .queryParam("apiKey", API_KEY)
                .queryParam("includeNutrition", "false")
                .expect()
                .body("vegetarian", is(false))
                .body("vegan", is(false))
                .body("license", equalTo("CC BY-SA 3.0"))
                .body("pricePerServing", equalTo(163.15F))
                .body("extendedIngredients[0].aisle", equalTo("Milk, Eggs, Other Dairy"))
                .when()
                .get("https://api.spoonacular.com/recipes/716429/information")
                .prettyPrint();
    }

    //3 мой - не выходит авторизоваться :(
    @Test
    void testAddToMealPlan() {
        given()
                .log()
                .all()
                .accept(ContentType.JSON)
               //.param("apiKey", API_KEY)
                .pathParam("username", "dsky")
              .queryParam("hash", "4b5v4398573406")
                              .expect()
                .log()
                .all()
                .body("name", is("1 banana"))
                .when()
                .post("/mealplanner/{username}/items")
                .prettyPrint();
    }
    //мой4 +
@Test
    void ComputeIngredientAmount() {
    given()
            .log()
            .all()
            .param("apiKey", API_KEY)
            .pathParam("id", "9266")
            .param("nutrient", "protein")
            .param("target", 2)
            .param("unit", "oz")
            .expect()
            .log()
            .all()
            .body("amount", is(7.05F))
            .body("unit", is("oz"))
            .when()
            .get("/food/ingredients/{id}/amount")
            .prettyPrint();
}
    //5 мой testCreateRecipeCard -
// не проходит, хотя картинка одинаковая при разных ссылках :(
//JSON path url doesn't match.
//Expected: is "https://spoonacular.com/recipeCardImages/recipeCard-1653853364985.png"
//  Actual: https://spoonacular.com/recipeCardImages/recipeCard-1653853610998.png
    @Test
    void testCreateRecipeCard() {
        given()
                .log()
                .all()
                .accept(ContentType.JSON)
                .param("apiKey", API_KEY)
                .pathParam("id", "4632")
                .expect()
                .log()
                .all()
                .body("url", is("https://spoonacular.com/recipeCardImages/recipeCard-1653930502064.png"))
                .when()
                .get("/recipes/{id}/card")
                .prettyPrint();
    }

    //c урока 1+
    @Test
    void testAutocompleteSearch() {
        given()
                .log()
                .all()
                .accept(ContentType.JSON)
                .param("apiKey", API_KEY)
                .param("query", "cheese")
                .param("number", 10)
                .expect()
                .log()
                .all()
                /* .body("",
                         hasItem(
                                 allOf(
                                         hasEntry("firstName", "test"),
                                         hasEntry("lastName", "test")
                                 )
                         )
                 )*/
                //  .body("name", is("Ivan"))
                .when()
                .get("autocomplete")
                .body().prettyPrint();
    }

    String expected = getResourceAsString("spoonaccular/TestAutocompleteSearch/expected.json");


    public void assertJson(Object expected, Object actually) {

        JsonAssert.assertJsonEquals(
                expected,
                actually,
                JsonAssert.when(IGNORING_ARRAY_ORDER)
        );
    }
//https://api.spoonacular.com/mealplanner/dsky/items

    //с урока 2+
    @Test
    void testTasteRecipeById() {
        given()
                .log()
                .all()
                .accept(ContentType.JSON)
                .param("apiKey", API_KEY)
                .pathParam("id", "69095")
                .expect()
                .log()
                .all()
                .body("sweetness", is(48.15F))
                .body("saltiness", is(45.29F))
                .body("sourness", is(15.6F))
                .body("bitterness", is(19.17F))
                .body("savoriness", is(26.45F))
                .body("fattiness", is(100.0F))
                .body("spiciness", is(0.0F))
                .when()
                .get("recipes/{id}/tasteWidget.json")
                .prettyPrint();
    }

//с урока 3 +

    @Test
    void EquipmentById() {
        EquipmentItem equipmentItem = new EquipmentItem("pie-pan.png", "pie form");
        EquipmentResponse response = given()
                .log()
                .all()
                .accept(ContentType.JSON)
                .param("apiKey", API_KEY)
                .pathParam("id", "1003464")
                .expect()
                .log()
                .all()
                .body("equipment[1].name", is("pie form"))
                .body("equipment[1].image", is("pie-pan.png"))
                .when()
                .get("recipes/{id}/equipmentWidget.json")
                .as(EquipmentResponse.class);
        response.getEquipment().stream()
                .filter(item -> item.getName().equals("pie form"))
                .peek(item -> Assertions.assertEquals("pie-pan.png", item.getImage()))
                .findAny()
                .orElseThrow();

    }

    public String getResourceAsString(String resource) throws IOException {

        InputStream stream = getClass().getResourceAsStream(resource);
        assert stream != null;
        byte[] bytes = stream.readAllBytes();
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public File getFile(String name) {
        String resource = getClass().getSimpleName() + "/" + name;
        String file = getClass().getResource(resource).getFile();
        return new File(file);
    }

  /* @AfterEach
    void tearDown() {
        given()
               // .queryParam("hash", "")
                .queryParam("apiKey", API_KEY)
                .delete("/mealplanner/geekbrains/items/" + id)
                .then()
                .statusCode(200);
    }*/

}
