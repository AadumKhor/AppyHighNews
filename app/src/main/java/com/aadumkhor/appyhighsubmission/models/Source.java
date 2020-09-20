package com.aadumkhor.appyhighsubmission.models;

import androidx.room.TypeConverter;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;

public class Source {
    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("name")
    @Expose
    private String name;

    public Source(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @TypeConverter
    public HashMap<String, Object> convertSourceToMap(Source source) {
        String name = source.getName();
        String id = source.getId();
        HashMap<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("id", id);
        return map;
    }

    @TypeConverter
    public Source convertMapToSource(HashMap<String, Object> map) {
        return new Source((String) map.get("id"), (String) map.get("name"));
    }
}
