package com.example.musiced.screen.playmusic;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.musiced.R;
import com.example.musiced.data.model.Song;
import com.example.musiced.screen.listsong.SongActivity;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

//import android.arch.lifecycle.ViewModelProviders;

public class PlayMusicFragment extends Fragment {
    public static final String TAG = "PlayMusicFragment";
    public TextView tvNameSong, tvNameSinger;
    public String pathUri;
    public int durations;
    TextView tvStart, tvLast;
    ImageButton imShuffle, imPre, imPlay, imNext, imRepeat;
    SeekBar seekbar;
    ImageView imageViewDisc;
    SongActivity songActivity;
    MediaPlayer mediaPlayer;
    Animation animation;

    ArrayList<Song> songArrayList;
    int idPosition;
    private ImageButton imageButton;
    // lưu tất cả các đường dẫn bài hát
    private ArrayList<String> paths;
    private boolean mShuffle = false;
    private boolean mRepeat = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            songArrayList = new ArrayList<>();
            songArrayList = getArguments().getParcelableArrayList("array");
            Log.d("minh", "" + songArrayList);
        }
    }

    private void setTime() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dinhdang = new SimpleDateFormat("mm:ss");
        tvLast.setText(String.valueOf(dinhdang.format(songArrayList.get(idPosition).getDurationSong())));
        // gan max seekbar = thoi gian cua bai hat
        seekbar.setMax(songArrayList.get(idPosition).getDurationSong());

    }

    /*
    - Khởi tạo Media
    - @Author: NvMinh
    */
    private void createMedia() {
        String ur = songArrayList.get(idPosition).getPathSong();
        tvNameSinger.setText(songArrayList.get(idPosition).getSingerName());
        tvNameSong.setText(songArrayList.get(idPosition).getSongName());
        Uri myUri = Uri.parse(ur);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                mediaPlayer.setDataSource(Objects.requireNonNull(getActivity()), myUri);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.start();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SongActivity) {
            songActivity = (SongActivity) context;


        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void upDateTimeSong() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                @SuppressLint("SimpleDateFormat") SimpleDateFormat dinhdang = new SimpleDateFormat("mm:ss");
                tvStart.setText(dinhdang.format(mediaPlayer.getCurrentPosition())); // getCurrentPosition: vi tri hien tai
                // update seek bar
                seekbar.setProgress(mediaPlayer.getCurrentPosition());
                // kiem tra thoi gian bai hat -> neu ket thuc -> next
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        if (mRepeat) {
                            createMedia();
                        } else {
                            idPosition++;
                            if (idPosition > songArrayList.size() - 1) {

                            }

                            createMedia();


                        }
                        if (mediaPlayer.isPlaying()) {
                            mediaPlayer.stop();
                        }
                        mediaPlayer.start();
                        imPlay.setImageResource(R.drawable.ic_pause);
                        setTime();
                        upDateTimeSong();
                    }
                });
                handler.postDelayed(this, 300); // gọi lại chính nó ( hàm Run)
            }
        }, 100); // delay là 1/10s
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_play_music, container, false);
        initView(view);
        animation = AnimationUtils.loadAnimation(getActivity(), R.anim.disc);
        getData();
        setTime();
        createMedia();
        checkEvent();
        upDateTimeSong();
        return view;

    }

    private void getData() {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            idPosition = bundle.getInt("position");
            Log.d("position", "la" + idPosition);
            String name = bundle.getString("title");
            String namesinger = bundle.getString("artist");
            pathUri = bundle.getString("path");
            durations = bundle.getInt("duration");

        }
    }

    private void checkEvent() {
        imPlay.setImageResource(R.drawable.ic_pause);
        imRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRepeat(mRepeat);
            }
        });

        imShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setShuffle(mShuffle);
            }
        });

        // event click play
        imPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) { // kiểm tra có hát hay ko
                    // nếu đang phát -> pause -> đổi hình ic_play
                    imPlay.setImageResource(R.drawable.ic_play);
                    mediaPlayer.pause();
                    imageViewDisc.clearAnimation();
                } else {
                    imPlay.setImageResource(R.drawable.ic_pause);
                    mediaPlayer.start();
                    imageViewDisc.startAnimation(animation);
                }
                setTime();
                upDateTimeSong();
            }
        });
        imNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mShuffle) {
                    Random rand = new Random();
                    idPosition = rand.nextInt(songArrayList.size());
                } else {
                    idPosition++;
                    if (idPosition > songArrayList.size() - 1) {
                        idPosition = 0;
                    }
                }
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                createMedia();
                mediaPlayer.start();
                imPlay.setImageResource(R.drawable.ic_pause);
                setTime();
                upDateTimeSong();
            }
        });
        imPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mShuffle) {
                    Random rand = new Random();
                    idPosition = rand.nextInt(songArrayList.size());
                } else {
                    idPosition--;
                    if (idPosition < 0) {
                        idPosition = songArrayList.size() - 1;
                    }
                }
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                createMedia();
                mediaPlayer.start();
                imPlay.setImageResource(R.drawable.ic_pause);
                setTime();
                upDateTimeSong();
            }
        });
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekbar.getProgress()); // nhảy đến chỗ nào của bài hát
            }
        });
    }


    private void setShuffle(boolean s) {
        if (!s) {
            imShuffle.setImageResource(R.drawable.ic_action_shuffle);
            mShuffle = true;
        } else {
            imShuffle.setImageResource(R.drawable.shuffle);
            mShuffle = false;
        }
    }

    private void setRepeat(boolean s) {
        if (!s) {
            imRepeat.setImageResource(R.drawable.ic_action_repeat);
            mRepeat = true;
        } else {
            imRepeat.setImageResource(R.drawable.repeat);
            mRepeat = false;
        }
    }

    private void initView(View view) {
        tvStart = view.findViewById(R.id.text_view_start);
        tvLast = view.findViewById(R.id.text_view_end);
        seekbar = view.findViewById(R.id.seekbar_song);
        imageViewDisc = view.findViewById(R.id.imageViewDisc);
        imShuffle = view.findViewById(R.id.image_shuffle);
        imPre = view.findViewById(R.id.image_back);
        imPlay = view.findViewById(R.id.img_play);
        imNext = view.findViewById(R.id.image_next);
        imRepeat = view.findViewById(R.id.image_repeat);
        tvNameSong = view.findViewById(R.id.text_track_title);
        tvNameSinger = view.findViewById(R.id.text_view_artist);
        imageButton = view.findViewById(R.id.image_button_down);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getFragmentManager() != null) {
                    getFragmentManager().popBackStack();
                }
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
    }


}
