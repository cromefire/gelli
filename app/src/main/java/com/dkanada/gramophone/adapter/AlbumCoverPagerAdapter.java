package com.dkanada.gramophone.adapter;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.dkanada.gramophone.R;
import com.dkanada.gramophone.glide.CustomGlideRequest;
import com.dkanada.gramophone.glide.CustomPaletteTarget;
import com.dkanada.gramophone.misc.CustomFragmentStatePagerAdapter;
import com.dkanada.gramophone.model.Song;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class AlbumCoverPagerAdapter extends CustomFragmentStatePagerAdapter {

    private List<Song> dataSet;

    private AlbumCoverFragment.ColorReceiver currentColorReceiver;
    private int currentColorReceiverPosition = -1;

    public AlbumCoverPagerAdapter(FragmentManager fm, List<Song> dataSet) {
        super(fm);
        this.dataSet = dataSet;
    }

    @Override
    public Fragment getItem(final int position) {
        return AlbumCoverFragment.newInstance(dataSet.get(position));
    }

    @Override
    public int getCount() {
        return dataSet.size();
    }

    @Override
    @NonNull
    public Object instantiateItem(ViewGroup container, int position) {
        Object o = super.instantiateItem(container, position);
        if (currentColorReceiver != null && currentColorReceiverPosition == position) {
            receiveColor(currentColorReceiver, currentColorReceiverPosition);
        }

        return o;
    }

    // only the latest ColorReceiver is guaranteed a response
    public void receiveColor(AlbumCoverFragment.ColorReceiver colorReceiver, int position) {
        AlbumCoverFragment fragment = (AlbumCoverFragment) getFragment(position);
        if (fragment != null) {
            currentColorReceiver = null;
            currentColorReceiverPosition = -1;
            fragment.receiveColor(colorReceiver, position);
        } else {
            currentColorReceiver = colorReceiver;
            currentColorReceiverPosition = position;
        }
    }

    public static class AlbumCoverFragment extends Fragment {
        private static final String SONG_ARG = "song";

        private Unbinder unbinder;

        @BindView(R.id.player_image)
        ImageView albumCover;

        private boolean isColorReady;
        private int color;
        private Song song;
        private ColorReceiver colorReceiver;
        private int request;

        public static AlbumCoverFragment newInstance(final Song song) {
            AlbumCoverFragment frag = new AlbumCoverFragment();
            final Bundle args = new Bundle();
            args.putParcelable(SONG_ARG, song);
            frag.setArguments(args);
            return frag;
        }

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            song = getArguments().getParcelable(SONG_ARG);
        }

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_album_cover, container, false);
            unbinder = ButterKnife.bind(this, view);
            return view;
        }

        @Override
        public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            loadAlbumCover();
        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();
            unbinder.unbind();
            colorReceiver = null;
        }

        private void loadAlbumCover() {
            CustomGlideRequest.Builder
                    .from(Glide.with(getContext()), song.primary)
                    .generatePalette(getActivity()).build()
                    .into(new CustomPaletteTarget(albumCover) {
                        @Override
                        public void onColorReady(int color) {
                            setColor(color);
                        }
                    });
        }

        private void setColor(int color) {
            this.color = color;
            isColorReady = true;
            if (colorReceiver != null) {
                colorReceiver.onColorReady(color, request);
                colorReceiver = null;
            }
        }

        public void receiveColor(ColorReceiver colorReceiver, int request) {
            if (isColorReady) {
                colorReceiver.onColorReady(color, request);
            } else {
                this.colorReceiver = colorReceiver;
                this.request = request;
            }
        }

        public interface ColorReceiver {
            void onColorReady(int color, int request);
        }
    }
}
