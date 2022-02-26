package com.federicoberon.newapp.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "melodies")
public class MelodyEntity {

    @PrimaryKey(autoGenerate = true)
    private long id;
    @NonNull
    private String title;
    private String uri;

    @Ignore
    public MelodyEntity() {
    }

    public MelodyEntity(long id, @NonNull String title, String uri) {
        this.id = id;
        this.title = title;
        this.uri = uri;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    public void setTitle(@NonNull String title) {
        this.title = title;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
