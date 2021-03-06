/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.game.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

/**
 * Manages the music for the game
 *
 * @author Dmitry
 */
public class MusicManager {

    public static final Music MENU_MUSIC = Gdx.audio.newMusic(Gdx.files.internal("rolling_hills.wav"));
    public static final Music GAME_MUSIC = Gdx.audio.newMusic(Gdx.files.internal("onwards.wav"));
    private static Music currentSong;
    private static boolean muted = false;
    private static final float MAX_VOLUME = 1f;

    /**
     * Stops any current song, and starts a new one
     *
     * @param song the new song to be played
     */
    public static void switchSong(Music song) {
        // only start the song anew if it is a song switch
        if (currentSong == song) {
            return;
        }
        // stop the current song if it exists
        if (currentSong != null) {
            currentSong.pause();
        }
        // set the current song
        currentSong = song;
        if (muted)
            muteMusic();
        else
            unmuteMusic();
         // the song should be forever looping
        currentSong.setLooping(true);
        currentSong.play();
    }

    /**
     * Mutes the current song but keeps it streaming
     */
    public static void muteMusic() {
        currentSong.setVolume(0f);
        muted = true;
    }

    /**
     * Unmutes the current song
     */
    public static void unmuteMusic() {
        currentSong.setVolume(MAX_VOLUME);
        muted = false;
    }
}
