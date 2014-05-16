package com.mongodb.datagen;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.mongodb.util.RandomUtils;

public class RandomServiceImpl {

    private Map<String, IndexedDictionary> dictionaries;
    
    public List<String> dictList(String dictionaryName, int maxLength) {
        int length = RandomUtils.getRandomInt(1, maxLength - 2);
        List<String> list = new ArrayList<String>(length);
        IndexedDictionary d = dictionaries.get(dictionaryName);
        for (int i = 0; i < length; i++) {
            list.add(dict(d));
        }
        return list;
    }

    public String dict(String dictionaryName, int index) {
        IndexedDictionary d = dictionaries.get(dictionaryName);
        return dict(d, index);
    }

    public String dict(String dictionaryName) {
        IndexedDictionary d = dictionaries.get(dictionaryName);
        return dict(d);
    }

    public String dict(IndexedDictionary d) {
        int index = RandomUtils.getRandomInt(0, d.getSize() - 1);
        return dict(d, index);
    }

    public String dict(IndexedDictionary d, int index) {
        String element = ((IndexedDictionary) d).get(index);
        if (d instanceof TupleDictionary) {
            return element.split(",|\t")[0];
        } else {
            return element;
        }
    }

    public String[] tuple(String dictionaryName) {
        TupleDictionary d = (TupleDictionary) dictionaries.get(dictionaryName);
        int index = RandomUtils.getRandomInt(0, d.getSize() - 1);
        String[] result = d.getTuple(index);
        return result;
    }

    public String tuple(String dictionaryName, int tupleIndex) {
        TupleDictionary d = (TupleDictionary) dictionaries.get(dictionaryName);
        int index = RandomUtils.getRandomInt(0, d.getSize() - 1);
        String[] result = d.getTuple(index);
        return result[tupleIndex];
    }

    public String sentence(String dictionaryName, int maxWords) {
        IndexedDictionary d = dictionaries.get(dictionaryName);
        int length = RandomUtils.getRandomInt(1, maxWords - 2);
        StringBuilder sb = new StringBuilder();
        String first = ((IndexedDictionary) d).get(RandomUtils.getRandomInt(0, d.getSize() - 1));
        sb.append(StringUtils.capitalize(first));
        for (int i = 0; i < length; i++) {
            sb.append(" ");
            sb.append(((IndexedDictionary) d).get(RandomUtils.getRandomInt(0, d.getSize() - 1)));
        }
        return sb.toString();
    }

    public void setDictionaries(Map<String, IndexedDictionary> dictionaries) {
        this.dictionaries = dictionaries;
    }

}
