package dragon.tamu.playphrase;

import org.json.JSONObject;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Map;

import static org.junit.Assert.*;

public class FileUnitTest {
    @Test
    public void getInfoTest() throws Exception{
        String content = "{\"Categories\":[{\"Name\":\"Stuff\",\"Phrases\":[{\"Name\":\"P3\",\"Languages\":{\"frn\":\"file1\",\"eng\":\"file2\"}},{\"Name\":\"P4\",\"Languages\":{\"frn\":\"file3\",\"eng\":\"file4\"}}]}]}";
        JSONObject json = new JSONObject(content);
        ArrayList<Category> result = FileAccessor.parseInfoJSON(json);

        assertEquals(1, result.size());
        assertEquals("Stuff", result.get(0).name);
        assertEquals(2, result.get(0).phraseList.size());
        assertEquals(2, result.get(0).phraseList.get(0).phraseLanguages.size());
        assertEquals("file1", result.get(0).phraseList.get(0).phraseLanguages.get("frn"));
    }

    @Test
    public void getLangTest() throws Exception{
        String content = "{\"Languages\":{\"English\": \"eng\",\"French\":\"frn\"}}";
        JSONObject json = new JSONObject(content);
        Map<String, String> result = FileAccessor.parseLangJSON(json);
        
        assertEquals(2, result.size());
        assertEquals("eng", result.get("English"));
    }
}