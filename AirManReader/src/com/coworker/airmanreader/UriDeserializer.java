package com.coworker.airmanreader;

import java.lang.reflect.Type;

import android.net.Uri;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class UriDeserializer implements JsonDeserializer<Uri>{

	@Override
	public Uri deserialize(JsonElement src, Type srcType,
			JsonDeserializationContext context) throws JsonParseException {
		// TODO Auto-generated method stub
		return Uri.parse(src.toString());
	}
}
