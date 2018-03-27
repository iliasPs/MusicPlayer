package com.example.android.musicplayer;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by vega on 05-Mar-18.
 */

public class SongAdapter extends ArrayAdapter<Song> { //we add the extend in order to inherit behavior from the arrayadapter. is is needed cause we will change the getView method


            public SongAdapter(Activity context, ArrayList<Song> words) {
            // Here, we initialize the ArrayAdapter's internal storage for the context and the list.
            // the second argument is used when the ArrayAdapter is populating a single TextView.
            // Because this is a custom adapter for two TextViews and an ImageView, the adapter is not
            // going to use this second argument, so it can be any value. Here, we used 0.
            super(context, 0, words);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {


            // Check if the existing view is being reused, otherwise inflate a completely new one
            View listItemView = convertView;
            if(listItemView == null) {
                listItemView = LayoutInflater.from(getContext()).inflate(
                        R.layout.song_list_view, parent, false);
            }
            // Get the {@link Song} object located at this position in the list
            Song currentSong = getItem(position);

            ImageView albumImg = listItemView.findViewById(R.id.albumImg);
            albumImg.setImageBitmap(currentSong.getmAlbumImg());

            // Find the TextView in the list_view.xml layout with the ID songTitle
            TextView songTitleView = listItemView.findViewById(R.id.songTitle);

            //get the default word and set the text in the list_view.xml
            songTitleView.setText(currentSong.getSongTitle());

            // Find the TextView in the list_view.xml layout with the ID songAlbum
            TextView songAlbumTitle = listItemView.findViewById(R.id.songAlbum);
            //get the translated word and set the text in the list_view.xml
            songAlbumTitle.setText(currentSong.getSongAlbum());

            TextView songArtist = listItemView.findViewById(R.id.songArtist);
            songArtist.setText(currentSong.getSongArtist());

            TextView songGenre = listItemView.findViewById(R.id.songGenre);
            songGenre.setText(currentSong.getSongGenre());



            return listItemView;
        }


}
