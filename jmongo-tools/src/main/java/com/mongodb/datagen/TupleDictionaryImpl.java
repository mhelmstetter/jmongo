package com.mongodb.datagen;

public class TupleDictionaryImpl extends WeightedTextDictionary implements TupleDictionary {

	@Override
	public String[] getTuple(int index) {
		String data = super.get(index);
		String[] tuple = data.split(",|\t");
		return tuple;
	}

}
