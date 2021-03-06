package com.dkanada.gramophone.service.playback;

public interface Playback {
    void setDataSource(String path);

    void queueDataSource(String path);

    void setCallbacks(PlaybackCallbacks callbacks);

    void start();

    void pause();

    void stop();

    boolean isInitialized();

    boolean isPlaying();

    int position();

    int duration();

    int seek(int position);

    void setVolume(float volume);

    interface PlaybackCallbacks {
        void onTrackStarted();

        void onTrackWentToNext();

        void onTrackEnded();
    }
}
