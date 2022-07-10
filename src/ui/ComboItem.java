package ui;

import data.MusicTrack;

public class ComboItem {
    private String key;
    private MusicTrack value;

    public ComboItem(String key, MusicTrack value)
    {
        this.key = key;
        this.value = value;
    }

    @Override
    public String toString()
    {
        return key;
    }

    public String getKey()
    {
        return key;
    }

    public MusicTrack getValue()
    {
        return value;
    }
}
