package dragon.tamu.playphrase;

import android.app.Activity;

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
import java.util.ListIterator;
import java.util.Map;

public class FileAccessor extends Activity
{
    //Members
    public ArrayList<Category> informationList;
    public Map<String, String> languageList;
    //constructor
    public FileAccessor() {
        informationList = this.getInfoList();
        languageList = this.getLangList();
    }

    public ArrayList<Category> getInfoList()
    {
        ArrayList<Category> result = new ArrayList<Category>();
        InputStream input;
        JSONObject json;
        try
        {
            input = openFileInput("fileLayout.json");
            int size = input.available();
            byte[] buffer = new byte[size];
            input.read(buffer);
            input.close();
            String text = new String(buffer);
            json = new JSONObject(text);

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
                    while (keyIt.hasNext())
                        phraseObj.addLanguage(keyIt.toString(), fileMap.getString(keyIt.toString()));
                }
                result.add(catObj);
            }
        } catch(IOException e){
            return getEmptyFileSystem();
        } catch(JSONException e) {
            return getEmptyFileSystem();
        }
        return new ArrayList<Category>();
    }

    public Map<String, String> getLangList()
    {
        Map<String, String> languageMap = new HashMap<>();
        InputStream input;
        JSONObject json;
        try
        {
            input = openFileInput("langList.json");
            int size = input.available();
            byte[] buffer = new byte[size];
            input.read(buffer);
            input.close();
            String text = new String(buffer);
            json = new JSONObject(text);

            JSONArray languages = json.getJSONArray("Languages");
            if(languages == null){
                return getEmptyLanguageFile();
            }
            for(int i=0; i<languages.length(); i++){
                JSONObject langJson = languages.getJSONObject(i);
                String langName = langJson.getString("Name");
                String langABV = langJson.getString("ABV");
                languageMap.put(langABV, langName);
            }
        } catch(IOException e){
            return getEmptyLanguageFile();
        } catch(JSONException e) {
            return getEmptyLanguageFile();
        }

        return languageMap;
    }

    public void addLanguage(String name, String abbreviation)
    {
    }

    public void removeLanguage(String name)
    {
    }

    public ArrayList<Category> addPhrase(String name, String language, String filePath, String categoryName)
    {
        String[] split = filePath.split("\\.");
        String extension = split[split.length-1];

        String storageFileName = name + language.toUpperCase();
        File directory = getFilesDir();
        File soundFile = new File(filePath);
        File newPlace = new File(directory,  storageFileName + "." + extension);
        soundFile.renameTo(newPlace);

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
        phrase.addLanguage(language, filePath);

        saveInfoToFile(informationList);
        return informationList;
    }

    public ArrayList<Category> removePhrase(String name, String categoryName)
    {
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
            return informationList;

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
        return informationList;
    }

    public void movePhrase(String name, Category cat, int pos)
    {
    }

    public ArrayList<Category> addCategory(String name)
    {
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

    public ArrayList<Category> removeCategory(String name)
    {
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

    public void moveCategory(String name, int pos)
    {
    }

    private Map<String, String> getEmptyLanguageFile(){
        return new HashMap<>();
    }

    private ArrayList<Category> getEmptyFileSystem()
    {
        return new ArrayList<Category>();
    }

    private void saveLangToFile(Map<String, String> langList)
    {
        JSONObject languages = new JSONObject(langList);

        JSONObject saveJSON = new JSONObject();
        try {
            saveJSON.put("Languages", languages);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            OutputStream outputStream = openFileOutput("langList.json", MODE_PRIVATE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void saveInfoToFile(ArrayList<Category> categoryArrayList)
    {
        JSONArray categories = categoryToJSON(categoryArrayList);

        JSONObject saveJSON = new JSONObject();
        try {
            saveJSON.put("Categories", categories);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            OutputStream outputStream = openFileOutput("fileLayout.json", MODE_PRIVATE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private JSONArray categoryToJSON(ArrayList<Category> categoryArrayList)
    {
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
}

