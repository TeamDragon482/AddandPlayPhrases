package dragon.tamu.playphrase;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class FileAccessor
{
    //Members
    public ArrayList<Category> informationList;
    public Map<String, String> languageList; //Name, ABV
    private Context context;
    private String uncategorized = "Uncategorized";

    //constructor
    public FileAccessor(Context c) {
        context = c;
        informationList = this.getInfoList();
        languageList = this.getLangList();
    }

    private static Map<String, String> getEmptyLanguageFile() {
        Map<String, String> defaultLanguages = new HashMap<>();
        defaultLanguages.put("English", "ENG");
        return defaultLanguages;
    }

    private static ArrayList<Category> getEmptyFileSystem() {
        ArrayList<Category> defaultFileSystem = new ArrayList<>();
        defaultFileSystem.add(new Category("Uncategorized"));

        return defaultFileSystem;
    }

    private static JSONArray categoryToJSON(ArrayList<Category> categoryArrayList) {
        try {
            JSONArray result = new JSONArray();
            for (Category category : categoryArrayList) {
                JSONObject catJSON = new JSONObject();
                catJSON.put("Name", category.name);
                JSONArray phraseJSON_list = new JSONArray();

                for (int i = 0; i < category.phraseList.size(); i++) {
                    Phrase phrase = (Phrase) category.phraseList.get(i);
                    JSONObject phraseJSON = new JSONObject();
                    phraseJSON.put("Name", phrase.name);
                    phraseJSON.put("Languages", new JSONObject(phrase.phraseLanguages));
                    phraseJSON_list.put(phraseJSON);
                }
                catJSON.put("Phrases", phraseJSON_list);
                result.put(catJSON);
            }
            return result;
        } catch (JSONException e) {
            Log.d("categoryToJSON", e.getMessage());
            return new JSONArray();
        }
    }

    //region Private Methods Exposed for Testing
    public static ArrayList<Category> parseInfoJSON(JSONObject json) throws JSONException {
        ArrayList<Category> result = new ArrayList<>();
        JSONArray cats = json.getJSONArray("Categories");
        if (cats == null)
            return getEmptyFileSystem();

        for (int i = 0; i < cats.length(); i++) {
            JSONObject catJson = cats.getJSONObject(i);
            Category catObj = new Category(catJson.getString("Name"));
            JSONArray phrasesJson = catJson.getJSONArray("Phrases");
            for (int j = 0; j < phrasesJson.length(); j++) {
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
        if (languages == null) {
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

    public ArrayList<Category> getInfoList() {
        ArrayList<Category> result = new ArrayList<>();
        InputStream input;
        JSONObject json;
        try {
            input = context.openFileInput("fileLayout.json");
            int size = input.available();
            byte[] buffer = new byte[size];
            //TODO check if the number of bytes read is non-zero.
            input.read(buffer);
            input.close();
            String text = new String(buffer);
            json = new JSONObject(text);
            return parseInfoJSON(json);
        } catch (IOException e) {
            Log.d("FileAccessor", e.getMessage());
            return getEmptyFileSystem();
        } catch (JSONException e) {
            Log.d("FileAccessor2", e.getMessage());
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
            //TODO check if the number of bytes read is non-zero.
            input.read(buffer);
            input.close();
            String text = new String(buffer);
            json = new JSONObject(text);
            return parseLangJSON(json);
        } catch (IOException e) {
            return getEmptyLanguageFile();
        } catch (JSONException e) {
            return getEmptyLanguageFile();
        }
    }

    //region Language Manipulation
    public void addLanguage(String name, String abbreviation) {
        if (!languageList.containsKey(name)) {
            languageList.put(name, abbreviation);
        }
        saveLangToFile(languageList);
    }
    //endregion

    public void removeLanguage(String name) {
        if (languageList.containsKey(name)) {
            languageList.remove(name);
        }
        saveLangToFile(languageList);
    }

    //Get language names from map
    public ArrayList<String> extractLangNames() {
        ArrayList<String> langNames = new ArrayList<>();

        for (String key : languageList.keySet()) {
            langNames.add(key);
        }

        return langNames;
    }

    //region Phrase Manipulation
    public Phrase addPhrase(String name, String language, String filePath, String categoryName) {
        String[] split = filePath.split("\\.");
        String extension = split[split.length - 1];

        String storageFileName = name + language.toUpperCase();
        storageFileName = storageFileName.replace(' ', '_');
        File directory = new File(context.getFilesDir().getAbsolutePath() + "/recordings");
        File soundFile = new File(filePath);
        File newName = new File(directory.getAbsolutePath() + "/" + storageFileName + "." + extension);

        directory.setWritable(true);

        //TODO no error checking here, might need it.
        if (!soundFile.renameTo(newName)) {
            Log.d("Rename", "False");
        } else
            Log.d("Rename", "True");

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
        {
            for (Category cat : informationList) {
                if (cat.name.equals(uncategorized)) {
                    category = cat;
                    break;
                }
            }
        }

        Phrase phrase = null;
        List<Object> phraseList = category.phraseList;
        for (int i = 0; i < phraseList.size(); i++) {
            Phrase phr = (Phrase) phraseList.get(i);
            if (phr.name.equals(name)) {
                phrase = phr;
                break;
            }
        }

        if (phrase == null) {
            phrase = new Phrase(name);
            category.phraseList.add(phrase);
        }
        phrase.addLanguage(language, newName.getPath());

        saveInfoToFile(informationList, false);

        return phrase;
    }
    //endregion

    public void removePhrase(String name, String categoryName) {
        Category category = null;
        for (Category cat : informationList) {
            if (cat.name.equals(categoryName)) {
                category = cat;
                break;
            }
        }
        if (category == null)
            return;

        Phrase phrase = null;
        List<Object> phraseList = category.phraseList;
        for (int i = 0; i < phraseList.size(); i++) {
            Phrase phr = (Phrase) phraseList.get(i);
            if (phr.name.equals(name)) {
                phrase = phr;
                break;
            }
        }

        if (phrase != null) {
            category.phraseList.remove(phrase);
            saveInfoToFile(informationList, false);
        }
    }

    public ArrayList<Category> getLocalInformationList() {
        return informationList;
    }

    //region Category Manipulation
    public ArrayList<Category> addCategory(String name) {

        Category category = null;
        for (Category cat : informationList) {
            if (cat.name.equals(name)) {
                category = cat;
                break;
            }
        }
        if (category == null) {
            Category newCat = new Category(name);
            // Adding a new category to the top will help keep Uncategorized at end
            // Also provides right functionality for the AddCategory button
            informationList.add(0, newCat);
            saveInfoToFile(informationList, false);
        }
        return informationList;
    }

    public ArrayList<Category> removeCategory(String name) {
        Category category = null;
        for (Category cat : informationList) {
            if (cat.name.equals(name) && (!name.equals(uncategorized)))
            {
                category = cat;
                break;
            }
        }
        if (category != null) {
            //TODO check that this actually works...
            List<Object> phraseList = category.phraseList;
            if (phraseList.size() != 0) {
                Category ucat = null;
                for (Category cat : informationList) {
                    if (cat.name.equals(uncategorized)) {
                        ucat = cat;
                        break;
                    }
                }
                if (ucat != null) {
                    List<Object> uPhraseList = ucat.phraseList;
                    uPhraseList.addAll(phraseList);
                    ucat.setChildItemList(uPhraseList);
                }
            }

            informationList.remove(category);
            saveInfoToFile(informationList, false);
        }
        return informationList;
    }

    public void moveCategory(String name, int pos) {
        // TODO - move category backend code
    }
    //endregion

    //region Private Helper Methods
    public void saveLangToFile(Map<String, String> langList) {
        JSONObject languages = new JSONObject(langList);

        JSONObject saveJSON = new JSONObject();
        try {
            saveJSON.put("Languages", languages);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            OutputStream outputStream = context.openFileOutput("langList.json", Context.MODE_PRIVATE);
            PrintStream printStream = new PrintStream(outputStream);
            printStream.print(saveJSON.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void saveInfoToFile(ArrayList<Category> categoryArrayList, boolean cleanFiles) {
        JSONArray categories = categoryToJSON(categoryArrayList);

        JSONObject saveJSON = new JSONObject();
        try {
            saveJSON.put("Categories", categories);
        } catch (JSONException e) {
            Log.d("saveInfoToFile", "JSONException " + e.getMessage());
            e.printStackTrace();
        }

        if (cleanFiles) {
            File directory = new File(context.getFilesDir().getAbsolutePath() + "/recordings");
            if (directory.exists()) {
                List<File> temp = Arrays.asList(directory.listFiles());
                List<File> toDelete = new ArrayList<>(temp);
                List<File> toKeep = new ArrayList<>();

                for (Category cat : categoryArrayList) {
                    for (Object objPhrase : cat.phraseList) {
                        Phrase phrase = (Phrase) objPhrase;
                        for (String location : phrase.phraseLanguages.values()) {
                            for (File check : toDelete) {
                                if (check.getPath().equals(location)) {
                                    toKeep.add(check);
                                    break;
                                }
                            }
                        }
                    }
                }
                for (File file : toKeep) {
                    toDelete.remove(file);
                }

                for (File file : toDelete) {
                    file.delete();
                }
                Log.d("saveInfoToFile", "delete check List\n" + toDelete.toString() + "\n\n");
                Log.d("saveInfoToFile", "All files\n" + temp.toString() + "\n\n");

            }
        }

        try {
            informationList = categoryArrayList;
            OutputStream outputStream = context.openFileOutput("fileLayout.json", Context.MODE_PRIVATE);
            PrintStream printStream = new PrintStream(outputStream);
            printStream.print(saveJSON.toString());
        } catch (FileNotFoundException e) {
            Log.d("saveInfoToFile", "FileNotFoundException " + e.getMessage());
            e.printStackTrace();
        }
    }
    //endregion
}

