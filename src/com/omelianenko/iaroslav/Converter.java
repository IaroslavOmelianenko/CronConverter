package com.omelianenko.iaroslav;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.*;


public class Converter implements DatesToCronConverter  {
    StringBuilder cronStringBuilder = new StringBuilder();

    @Override
    public String getImplementationInfo() {
        String name = "Omelianenko Iaroslav Sergeevich";
        String realisationClassName = getClass().toString();
        String packageName = getClass().getPackage().toString();
        String projectGitHubLink = "https://github.com/IaroslavOmelianenko";

        return name + ", " + realisationClassName + ", " + packageName + ", " + projectGitHubLink;
    }


    @Override
    public String convert(List<String> inputDates) throws DatesToCronConvertException {

        List<LocalDateTime> datesList = new ArrayList<>();
        for (String stringDate : inputDates) {
            datesList.add(LocalDateTime.parse(stringDate));
        }

        //Set for unique values
        Set<Integer> monthSet = new HashSet<>();
        Set<DayOfWeek> dayOfWeekSet = new HashSet<>();
        Set<Integer> daySet = new HashSet<>();
        Set<Integer> hoursSet = new HashSet<>();
        Set<Integer> minutesSet = new HashSet<>();
        Set<Integer> secondsSet = new HashSet<>();

        //Maps for date as key and sequence as value
        Map<Integer, Integer> monthMap = new HashMap<>();
        Map<Integer, Integer> dayMap = new HashMap<>();
        Map<Integer, Integer> hoursMap = new HashMap<>();
        Map<Integer, Integer> minutesMap = new HashMap<>();
        Map<Integer, Integer> secondsMap = new HashMap<>();

        //List and map for counting and searching sequences
        List<Integer> sequenceLists = new ArrayList<>();
        Map<Integer, Integer> sequenceMaps = new HashMap<>();

        //List for 'asOneDay' check
        List<Integer> dayList = new ArrayList<>();

        //Add dates to set and list
        for (LocalDateTime localDateTime : datesList) {

            dayList.add(localDateTime.getDayOfMonth());

            monthSet.add(localDateTime.getMonthValue());
            daySet.add(localDateTime.getDayOfMonth());
            hoursSet.add(localDateTime.getHour());
            minutesSet.add(localDateTime.getMinute());
            secondsSet.add(localDateTime.getSecond());
            dayOfWeekSet.add(localDateTime.getDayOfWeek());
        }

        //If seconds,minutes or hours have from day to day sequence, than days are not counting
        boolean asOneDay = datesList.size() / secondsSet.size() == dayList.size() ||
                datesList.size() / minutesSet.size() == dayList.size() ||
                datesList.size() / hoursSet.size() == dayList.size();

        secondsToCron(daySet, secondsSet, secondsMap, sequenceMaps, sequenceLists);
        minutesHoursToCron(daySet, minutesSet, minutesMap, sequenceMaps, sequenceLists);
        minutesHoursToCron(daySet, hoursSet, hoursMap, sequenceMaps, sequenceLists);
        daysToCron(daySet, dayMap, sequenceMaps, sequenceLists, asOneDay);
        monthsToCron(monthSet, monthMap, sequenceMaps, sequenceLists);
        dayOfWeekToCron(dayOfWeekSet, datesList);


        return cronStringBuilder.toString();
    }

    public void secondsToCron(Set<Integer> daySet,
                              Set<Integer> smhSet,
                              Map<Integer, Integer> smhMap,
                              Map<Integer, Integer> sequenceMaps,
                              List<Integer> sequenceLists) {

        if (smhSet.size() != 1 && daySet.size() != 1) {
            int sequence = 0;
            //Sorting unique values
            List<Integer> uniqueValues = new ArrayList<>(smhSet);
            Collections.sort(uniqueValues);

            //Search for sequence between time values and put it into map (time value as key, sequence as value)
            for (int i = uniqueValues.size() - 1; i > 0; i--) {
                sequence = uniqueValues.get(i) - uniqueValues.get(i - 1);
                smhMap.put(uniqueValues.get(i), sequence);

                for (int j = 0; j < smhMap.size(); j++) {
                    sequenceMaps.putIfAbsent(sequence, 0);
                }
                if (smhMap.containsValue(sequence)) {
                    sequenceMaps.computeIfPresent(sequence, (key, value) -> value + 1);
                }
            }

            //Find the largest sequence
            int maxSequence = Collections.max(sequenceMaps.values());

            //Getting keys (date) by values (maximum sequence)
            int maxSequenceValue = 0;
            for (int i : getKeys(sequenceMaps, maxSequence)) {
                maxSequenceValue = i;
            }
            sequenceLists.addAll(getKeys(smhMap, maxSequenceValue));

            Collections.sort(sequenceLists);
            //We are missing the first element of the largest sequence, so we need to add it
            sequenceLists.add(0, sequenceLists.get(0) - maxSequenceValue);

            //MIN and MAX value of the largest sequence
            int minSMH = getMinValue(sequenceLists, sequenceLists.size());
            int maxSMH = getMaxValue(sequenceLists, sequenceLists.size());

            if (sequence == 1) {
                cronStringBuilder.append(minSMH).append("-").append(maxSMH);
            } else if (sequenceLists.size() > smhSet.size() / 2) {
                cronStringBuilder.append(minSMH).append("/").append(sequence);
            }
        } else cronStringBuilder.append("0");  //For template accordance
        cronStringBuilder.append(" ");

        sequenceLists.clear();
        sequenceMaps.clear();
    }


    public void minutesHoursToCron(Set<Integer> daySet,
                                   Set<Integer> smhSet,
                                   Map<Integer, Integer> smhMap,
                                   Map<Integer, Integer> sequenceMaps,
                                   List<Integer> sequenceLists) {

        if (smhSet.size() != 1 && daySet.size() != 1) {
            int sequence = 0;
            //Sorting unique values
            List<Integer> uniqueValues = new ArrayList<>(smhSet);
            Collections.sort(uniqueValues);

            //Search for sequence between time values and put it into map (time value as key, sequence as value)
            for (int i = uniqueValues.size() - 1; i > 0; i--) {
                sequence = uniqueValues.get(i) - uniqueValues.get(i - 1);
                smhMap.put(uniqueValues.get(i), sequence);

                for (int j = 0; j < smhMap.size(); j++) {
                    sequenceMaps.putIfAbsent(sequence, 0);
                }
                if (smhMap.containsValue(sequence)) {
                    sequenceMaps.computeIfPresent(sequence, (key, value) -> value + 1);
                }
            }

            //Find the largest sequence
            int maxSequence = Collections.max(sequenceMaps.values());

            //Getting keys (date) by values (maximum sequence)
            int maxSequenceValue=0;
            for (int i : getKeys(sequenceMaps, maxSequence)) {
                maxSequenceValue = i;
            }
            sequenceLists.addAll(getKeys(smhMap, maxSequenceValue));

            Collections.sort(sequenceLists);
            //We are missing the first element of the largest sequence, so we need to add it
            sequenceLists.add(0,sequenceLists.get(0)-maxSequenceValue);

            //MIN and MAX value of the largest sequence
            int minSMH = getMinValue(sequenceLists, sequenceLists.size());
            int maxSMH = getMaxValue(sequenceLists, sequenceLists.size());

            if (sequence == 1) {
                cronStringBuilder.append(minSMH).append("-").append(maxSMH);
            }
            else if (sequenceLists.size()>smhSet.size()/2) {
                cronStringBuilder.append(minSMH).append("/").append(sequence);
            }
        } else cronStringBuilder.append("*");

        cronStringBuilder.append(" ");

        sequenceLists.clear();
        sequenceMaps.clear();
    }


    public void daysToCron(Set<Integer> daySet,
                           Map<Integer, Integer> dayMap,
                           Map<Integer, Integer> sequenceMaps,
                           List<Integer> sequenceLists,
                           boolean asOneDay) {

        if (daySet.size() != 1 && !asOneDay) {
            int sequence = 0;

            List<Integer> uniqueValues = new ArrayList<>(daySet);
            Collections.sort(uniqueValues);

            for (int i = uniqueValues.size() - 1; i > 0; i--) {
                sequence = uniqueValues.get(i) - uniqueValues.get(i - 1);
                dayMap.put(uniqueValues.get(i), sequence);

                for (int j = 0; j < dayMap.size(); j++) {
                    sequenceMaps.putIfAbsent(sequence, 0);
                }
                if (dayMap.containsValue(sequence)) {
                    sequenceMaps.computeIfPresent(sequence, (key, value) -> value + 1);
                }
            }
            int maxSequence = Collections.max(sequenceMaps.values());
            int maxSequenceValue=0;

            for (int i : getKeys(sequenceMaps, maxSequence)) {
                maxSequenceValue = i;
            }
            sequenceLists.addAll(getKeys(dayMap, maxSequenceValue));

            Collections.sort(sequenceLists);
            sequenceLists.add(0,sequenceLists.get(0)-maxSequenceValue);

            int minDay = getMinValue(sequenceLists, sequenceLists.size());
            int maxDay = getMaxValue(sequenceLists, sequenceLists.size());


          if (sequence == 1 ) {
                cronStringBuilder.append(minDay).append("-").append(maxDay);
            }

        } else cronStringBuilder.append("*");
        cronStringBuilder.append(" ");
        sequenceLists.clear();
        sequenceMaps.clear();
    }

    public void monthsToCron(Set<Integer> monthSet,
                             Map<Integer, Integer> monthMap,
                             Map<Integer, Integer> sequenceMaps,
                             List<Integer> sequenceLists) {

        if (monthSet.size() != 1) {
            int sequence = 0;

            List<Integer> uniqueValues = new ArrayList<>(monthSet);

            Collections.sort(uniqueValues);

            for (int i = uniqueValues.size() - 1; i > 0; i--) {
                sequence = uniqueValues.get(i) - uniqueValues.get(i - 1);
                monthMap.put(uniqueValues.get(i), sequence);
                System.out.println("sequence= " + sequence);

                for (int j = 0; j < monthMap.size(); j++) {
                    sequenceMaps.putIfAbsent(sequence, 0);
                }
                if (monthMap.containsValue(sequence)) {
                    sequenceMaps.computeIfPresent(sequence, (key, value) -> value + 1);
                    System.out.println("SEQUENCE MAP " + sequenceMaps);
                }
            }

            int maxSequence = Collections.max(sequenceMaps.values());
            int maxSequenceValue=0;

            for (int i : getKeys(sequenceMaps, maxSequence)) {
                maxSequenceValue = i;
            }
            for (int i : getKeys(monthMap, maxSequenceValue)) {
                System.out.println("y : "+i);
                sequenceLists.add(i);
            }

            Collections.sort(sequenceLists);
            sequenceLists.add(0,sequenceLists.get(0)-maxSequenceValue);

            int minMonth = getMinValue(sequenceLists, sequenceLists.size());
            int maxMonth = getMaxValue(sequenceLists, sequenceLists.size());

            if (sequence == 1) {
                cronStringBuilder.append(minMonth).append("-").append(maxMonth);
            }
            else if ((sequenceLists.size())>(monthSet.size()/2)) {
                cronStringBuilder.append(minMonth).append("/").append(sequence);
            }
        } else cronStringBuilder.append("*");
        cronStringBuilder.append(" ");
        sequenceLists.clear();
        sequenceMaps.clear();
    }

    public void dayOfWeekToCron(Set<DayOfWeek> dayOfWeekSet,
                                List<LocalDateTime> datesList) {

        if (dayOfWeekSet.size() == 1) {
            cronStringBuilder.append(datesList.get(0).getDayOfWeek().toString().substring(0,3));
        } else cronStringBuilder.append("*");
    }

    int getMinValue(List<Integer> listToMin, int n) {
        int min = listToMin.get(0);
        for (int i = 1; i < n; i++) {
            if (listToMin.get(i) < min)
                min = listToMin.get(i);
        }
        return min;
    }

    int getMaxValue(List<Integer> listToMax, int n) {
        int max = listToMax.get(0);
        for (int i = 1; i < n; i++) {
            if (listToMax.get(i) > max)
                max = listToMax.get(i);
        }
        return max;
    }

    //Get keys by value method
    private static Set<Integer> getKeys(Map<Integer, Integer> map, Integer value) {

        Set<Integer> result = new HashSet<>();
        if (map.containsValue(value)) {
            for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
                if (Objects.equals(entry.getValue(), value)) {
                    result.add(entry.getKey());
                }
            }
        }
        return result;
    }

}

