package org.test;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    private static class NewWord
    {
        public String value;
        public int position;

        public NewWord(String value, int position)
        {
            this.value = value;
            this.position = position;
        }
    }
    private static List<List<String>> findGroups(List<String> lines)
    {
        List<Map<String, Integer>> wordsToGroupsNumbers = new ArrayList<>();
        List<List<String>> linesGroups = new ArrayList<>();
        Map<Integer, Integer> mergedGroupNumberToFinalGroupNumber = new HashMap<>();
        for (String line : lines)
        {
            String[] words = line.split(";");
            TreeSet<Integer> foundInGroups = new TreeSet<>();
            List<NewWord> newWords = new ArrayList<>();
            for (int i = 0; i < words.length; i++)
            {
                String word = words[i];

                if (wordsToGroupsNumbers.size() == i)
                    wordsToGroupsNumbers.add(new HashMap<>());

                if (word.equals(""))
                    continue;

                Map<String, Integer> wordToGroupNumber = wordsToGroupsNumbers.get(i);
                Integer wordGroupNumber = wordToGroupNumber.get(word);
                if (wordGroupNumber != null)
                {
                    while (mergedGroupNumberToFinalGroupNumber.containsKey(wordGroupNumber))
                        wordGroupNumber = mergedGroupNumberToFinalGroupNumber.get(wordGroupNumber);
                    foundInGroups.add(wordGroupNumber);
                }
                else
                {
                    newWords.add(new NewWord(word, i));
                }
            }
            int groupNumber;
            if (foundInGroups.isEmpty())
            {
                groupNumber = linesGroups.size();
                linesGroups.add(new ArrayList<>());
            }
            else
            {
                groupNumber = foundInGroups.first();
            }
            for (NewWord newWord : newWords)
            {
                wordsToGroupsNumbers.get(newWord.position).put(newWord.value, groupNumber);
            }
            for (int mergeGroupNumber : foundInGroups)
            {
                if (mergeGroupNumber != groupNumber)
                {
                    mergedGroupNumberToFinalGroupNumber.put(mergeGroupNumber, groupNumber);
                    linesGroups.get(groupNumber).addAll(linesGroups.get(mergeGroupNumber));
                    linesGroups.set(mergeGroupNumber, null);
                }
            }
            linesGroups.get(groupNumber).add(line);
        }
        linesGroups.removeAll(Collections.singleton(null));
        return linesGroups;
    }

    public static void main(String[] args) {
        if(args.length != 1) {
            System.err.println("There should be one argument.");
            return;
        }


        final Set<String> uniqueElements = new LinkedHashSet<>();
        final int bufferSize = 500;
        final String regex = "^(\"[0-9]*\")(;\"[0-9]*\")*$";

        try(var reader = new BufferedReader(new FileReader(args[0]), bufferSize)) {
            String line;
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher;

            while((line = reader.readLine()) != null) {
                matcher = pattern.matcher(line);
                if(matcher.matches()) {
                    uniqueElements.add(line);
                }
            }

        }
        catch (Exception e) {
            e.printStackTrace();
            if(e.getSuppressed() != null) {
                for(Throwable ex : e.getSuppressed()) {
                    ex.printStackTrace();
                }
            }
            return;
        }

        List<String> newList = new ArrayList<>(uniqueElements);

        List<List<String>> result = findGroups(newList);

        String filePath = "out.txt";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            int i = 1;
            for (List<String> list : result) {
                writer.write("Group" + i + ":\n");
                for(var line : list){
                    writer.write(line);
                    writer.newLine();
                }
                writer.newLine();
                ++i;
            }
            System.out.println("Data has had Successfully written to file  " + filePath);
        } catch (IOException e) {
            System.err.println("Error while writing to file  " + e.getMessage());
        }
    }
}