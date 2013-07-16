package com.jimmy.rssreader;

import java.lang.reflect.Type;

import android.net.Uri;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class UriDeserializer implements JsonDeserializer<Uri> {
	@Override
	public Uri deserialize(final JsonElement src,final Type srcType,
			final JsonDeserializationContext context) throws JsonParseException{
		// TODO Auto-generated constructor stub
		return Uri.parse(src.toString());
	}
}
