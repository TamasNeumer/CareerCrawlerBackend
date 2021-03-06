package NLP;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.apache.commons.io.IOUtils;

public class NLP {
    /**
     * Counts the number of occurrences of each skill/framework from skills.txt in jobTexts.
     *
     * @param jobTexts Array of jobTexts containing all the scraped jobTexts.
     * @return HashMap containing the found words (key) and their occurrence (value)
     */
    public HashMap<String, Integer> getFrequencyOfSkillsInJobTexts(ArrayList<String> jobTexts) {
        ArrayList<String> skills = readSkillsFromFile();
        return calcualteSkillFrequencies(skills, jobTexts);
    }

    /**
     * Reads the skills into an arraylist and returns it to the caller. Returns an empty list, if the file
     * was not found.
     *
     * @return List of skills read from skills.txt
     */
    private ArrayList<String> readSkillsFromFile() {
        String result = "";
        ClassLoader classLoader = getClass().getClassLoader();
        try {
            result = IOUtils.toString(classLoader.getResourceAsStream("skills.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>(Arrays.asList(result.split("\\r?\\n")));
    }

    /**
     * @param skills   ArrayList of skills to be matched.
     * @param jobTexts ArrayList of jobTexts
     * @return HashMap containing the found skills (key) and the number of occurrence in text (value)
     */
    private HashMap<String, Integer> calcualteSkillFrequencies(ArrayList<String> skills,
                                                               ArrayList<String> jobTexts) {

        ArrayList<String> cleanedJobTexts = new ArrayList<>();
        for (String text : jobTexts) {
            cleanedJobTexts.add(cleanTextForSkillRecognition(text));
        }
        HashMap<String, Integer> frequencies = new HashMap<>();
        for (String skill : skills) {
            for (String text : cleanedJobTexts) {
                int count = 0;
                Pattern p = Pattern.compile(addSpacesAroundWord(escapeSpecialCharactersInRegex(skill)));
                Matcher m = p.matcher(text);
                while (m.find()) {
                    count += 1;
                }
                if (count != 0) {
                    if (frequencies.containsKey(skill)) {
                        Integer temp = frequencies.get(skill);
                        temp++;
                        frequencies.put(skill, temp);
                    } else
                        frequencies.put(skill, count);
                }
            }
        }
        return frequencies;
    }

    /**
     * Function used to escape regex characters. (. ( ) + ? etc.)
     * E.g.: If C++ is used as regex it has to be transformed to C\+\+.
     *
     * @param regex
     * @return regex with escaped regex-sensitive characters.
     */
    private String escapeSpecialCharactersInRegex(String regex) {
        return regex.replaceAll("([^a-zA-Z0-9])", "\\\\$1");
    }

    /**
     * Adds two whitespaces around the word.
     *
     * @param keyword keyword to be transformed
     * @return whitespace+keyowrd+whitespace
     */
    private String addSpacesAroundWord(String keyword) {
        return " " + keyword + " ";
    }

    /**
     * Replaces braces (), [], punctuations (, .) with whitespace.
     *
     * @param text text to be cleaned.
     * @return cleaned text, free of braces, punctuations.
     */
    private String cleanTextForSkillRecognition(String text) {
        return text.replaceAll("[\\\\\\.\\(\\)\\,\\[\\]]", " ");
    }
}
