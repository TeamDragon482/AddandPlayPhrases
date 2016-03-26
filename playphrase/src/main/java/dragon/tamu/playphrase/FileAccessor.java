package dragon.tamu.playphrase;

import android.app.Activity;
import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class FileAccessor
{
    //Members
    public ArrayList<Category> informationList;
    public Map<String, String> languageList;
    private Context context;

    //constructor
    public FileAccessor(Context c) {
        context = c;
        informationList = this.getInfoList();
        languageList = this.getLangList();
    }

    public ArrayList<Category> getInfoList() {
        ArrayList<Category> result = new ArrayList<Category>();
        InputStream input;
        JSONObject json;
        try
        {
            input = context.openFileInput("fileLayout.json");
            int size = input.available();
            byte[] buffer = new byte[size];
            input.read(buffer);
            input.close();
            String text = new String(buffer);
            json = new JSONObject(text);
            return parseInfoJSON(json);
        } catch(IOException e){
            return getEmptyFileSystem();
        } catch(JSONException e) {
            return getEmptyFileSystem();
        }
    }

    public Map<String, String> getLangList() {
        InputStream input;
        JSONObject json;
        try
        {
            input = context.openFileInput("langList.json");
            int size = input.available();
            byte[] buffer = new byte[size];
            input.read(buffer);
            input.close();
            String text = new String(buffer);
            json = new JSONObject(text);
            return parseLangJSON(json);
        } catch(IOException e){
            return getEmptyLanguageFile();
        } catch(JSONException e) {
            return getEmptyLanguageFile();
        }
    }

    //region Language Manipulation
    public void addLanguage(String name, String abbreviation) {
    }

    public void removeLanguage(String name) {
    }
    //endregion

    //region Phrase Manipulation
    public void addPhrase(String name, String language, String filePath, String categoryName) {
        String[] split = filePath.split("\\.");
        String extension = split[split.length-1];

        String storageFileName = name + language.toUpperCase();
        File directory = context.getFilesDir();
        File soundFile = new File(filePath);
        File newName = new File(directory, storageFileName + "." + extension);
        soundFile.renameTo(newName);

        Category category = null;
        for (Category cat : informationList)
        {
            if (cat.name.equals(categoryName))
            {
                category = cat;
                break;
            }
        }

        //TODO make go to uncaterogized
        if (category == null)
            category = informationList.get(informationList.size()-1);

        Phrase phrase = null;
        for (Phrase phr : category.phraseList)
        {
            if (phr.name.equals(name))
            {
                phrase = phr;
                break;
            }
        }

        if (phrase == null)
        {
            phrase = new Phrase(name);
            category.phraseList.add(phrase);
        }
        phrase.addLanguage(language, soundFile.getPath());

        saveInfoToFile(informationList);
    }

    public void removePhrase(String name, String categoryName) {
        Category category = null;
        for (Category cat : informationList)
        {
            if (cat.name.equals(categoryName))
            {
                category = cat;
                break;
            }
        }
        if (category == null)
            return;

        Phrase phrase = null;
        for (Phrase phr : category.phraseList)
        {
            if (phr.name.equals(name))
            {
                phrase = phr;
                break;
            }
        }

        if (phrase != null)
        {
            category.phraseList.remove(phrase);
            saveInfoToFile(informationList);
        }
    }

    public void movePhrase(String name, Category cat, int pos) {
    }
    //endregion

    //region Category Manipulation
    public ArrayList<Category> addCategory(String name) {

        //TODO make unable to delete uncaterogized
        Category category = null;
        for (Category cat : informationList)
        {
            if (cat.name.equals(name))
            {
                category = cat;
                break;
            }
        }
        if (category == null)
        {
            Category newCat = new Category(name);
            informationList.add(newCat);
            saveInfoToFile(informationList);
        }
        return informationList;
    }

    public ArrayList<Category> removeCategory(String name) {
        Category category = null;
        for (Category cat : informationList)
        {
            if (cat.name.equals(name))
            {
                category = cat;
                break;
            }
        }
        if (category != null)
        {
            informationList.remove(category);
            saveInfoToFile(informationList);
        }
        return informationList;
    }

    public void moveCategory(String name, int pos) {
    }
    //endregion

    //region Private Helper Methods
    private void saveLangToFile(Map<String, String> langList) {
        JSONObject languages = new JSONObject(langList);

        JSONObject saveJSON = new JSONObject();
        try {
            saveJSON.put("Languages", languages);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            OutputStream outputStream = context.openFileOutput("langList.json", context.MODE_PRIVATE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void saveInfoToFile(ArrayList<Category> categoryArrayList) {
        JSONArray categories = categoryToJSON(categoryArrayList);

        JSONObject saveJSON = new JSONObject();
        try {
            saveJSON.put("Categories", categories);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            OutputStream outputStream = context.openFileOutput("fileLayout.json", context.MODE_PRIVATE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static Map<String, String> getEmptyLanguageFile(){
        Map<String, String> defaultLanguages =  new HashMap<>();
        defaultLanguages.put("English", "ENG");
        return defaultLanguages;
    }

    private static ArrayList<Category> getEmptyFileSystem()
    {
        ArrayList<Category> defaultFileSystem = new ArrayList<Category>();
        defaultFileSystem.add(new Category("Uncatergorized"));
        return defaultFileSystem;
    }

    private static JSONArray categoryToJSON(ArrayList<Category> categoryArrayList) {
        try{
            JSONArray result = new JSONArray();
            for(Category category: categoryArrayList)
            {
                JSONObject catJSON = new JSONObject();
                catJSON.put("Name", category);
                JSONArray phraseJSON_list = new JSONArray();
                for(Phrase phrase: category.phraseList)
                {
                    JSONObject phraseJSON = new JSONObject();
                    phraseJSON.put("Name", phrase.name);
                    phraseJSON.put("Languages", new JSONObject(phrase.phraseLanguages));
                    phraseJSON_list.put(phraseJSON);
                }
                catJSON.put("Phrases", phraseJSON_list);
                result.put(catJSON);
            }
            return result;
        } catch(JSONException e) {
            return new JSONArray();
        }
    }
    //endregion

    //region Private Methods Exposed for Testing
    public static ArrayList<Category> parseInfoJSON(JSONObject json) throws JSONException {
        ArrayList<Category> result = new ArrayList<Category>();
        JSONArray cats = json.getJSONArray("Categories");
        if (cats == null)
            return getEmptyFileSystem();

        for (int i=0; i<cats.length(); i++)
        {
            JSONObject catJson = cats.getJSONObject(i);
            Category catObj = new Category(catJson.getString("Name"));
            JSONArray phrasesJson = catJson.getJSONArray("Phrases");
            for (int j=0; j<phrasesJson.length(); j++)
            {
                JSONObject phraseJson = phrasesJson.getJSONObject(j);
                Phrase phraseObj = new Phrase(phraseJson.getString("Name"));

                JSONObject fileMap = phraseJson.getJSONObject("Languages");
                Iterator<String> keyIt = fileMap.keys();
                while (keyIt.hasNext()) {
                    String lang = keyIt.next();
                    phraseObj.addLanguage(lang, fileMap.getString(lang));
                }
                catObj.phraseList.add(phraseObj);
            }
            result.add(catObj);
        }
        return result;
    }

    public static Map<String, String> parseLangJSON(JSONObject json) throws JSONException {
        Map<String, String> languageMap = new HashMap<>();
        JSONObject languages = json.getJSONObject("Languages");
        if(languages == null){
            return getEmptyLanguageFile();
        }

        Iterator<String> keyIt = languages.keys();
        while (keyIt.hasNext()) {
            String lang = keyIt.next();
            languageMap.put(lang, languages.getString(lang));
        }
        return languageMap;
    }
    //endregion
}

