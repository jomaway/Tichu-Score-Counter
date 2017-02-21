package de.jomaway.tichuscorecounter;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Checkable;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;


/**
 * Created by jsma on 13.02.2017.
 */

public class PlayerActionBar extends LinearLayout {
    private static final String TAG = "PlayerActionBar";

    private Players mBindToPlayer;
    private CheckedTextView mTichu;
    private CheckedTextView mGreatTichu;
    private CheckedTextView mOut;

    private boolean isOut = false;
    private int position = 0;

    PlayerOutCallback playerOutCallback;

    public PlayerActionBar(Context context) {
        this(context, null);
    }

    public PlayerActionBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlayerActionBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setOrientation(HORIZONTAL);

        /*
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.PlayerActionBar, 0, 0);

        try {
            mBindToPlayer = a.getInteger(R.styleable.PlayerActionBar_bindToPlayer, 0);
            Log.d(TAG,String.valueOf(mBindToPlayer));
        } finally {
            a.recycle();
        }
        */

        // Inflate Buttons
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_player_actionbar, this);

        // Set CheckedTextViews
        mTichu = (CheckedTextView) findViewById(R.id.check_tichu);
        mGreatTichu = (CheckedTextView) findViewById(R.id.check_great_tichu);
        mOut = (CheckedTextView) findViewById(R.id.check_out);

        // Set OnClickListeners
        setOCL();

    }

    // Toogle the CheckedTextView
    // returns the new state as boolean
    public boolean toogleChecked(Checkable view){
            if ( view.isChecked()){
                view.setChecked(false);
                return false;
            } else {
                view.setChecked(true);
                return true;
            }
    }

    private void setOCL() {
        mTichu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isOut) {
                    Log.d(TAG,"Tichu");
                    if(!mGreatTichu.isChecked()){
                        toogleChecked(mTichu);
                    }
                }
            }
        });
        mGreatTichu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isOut) {
                    Log.d(TAG," Great Tichu");
                    if (!mTichu.isChecked()) {
                        toogleChecked(mGreatTichu);
                    }
                }
            }
        });
        mOut.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isOut) {
                    Log.d(TAG, "player out");
                    if (toogleChecked(mOut)) {
                        // First increment because it starts with 0
                        position = MainActivity.getPlayersOut();
                        mOut.setText(String.valueOf(position));
                        isOut = true;
                        if (playerOutCallback != null) {
                            playerOutCallback.playerOut(mBindToPlayer);
                        }
                    } else {
                        mOut.setText(getResources().getString(R.string.btn_player_out));
                    }
                }

            }
        });
    }

    public void reset() {
        isOut = false;
        mOut.setChecked(false);
        mTichu.setChecked(false);
        mGreatTichu.setChecked(false);
        mOut.setText(getResources().getString(R.string.btn_player_out));
    }

    public void setPlayerOutCallback(PlayerOutCallback callback, Players player) {
        playerOutCallback = callback;
        mBindToPlayer = player;
    }

    public int getPosition() {
        return position;
    }
    public boolean isFirst() {
        if (position == 1) {
            return true;
        } else {
            return false;
        }
    }
    public boolean saidTichu() {
        return mTichu.isChecked();
    }

    public boolean saidGreatTichu() {
        return mGreatTichu.isChecked();
    }

    public void redoPlayerOut() {
        mOut.setChecked(false);
        mOut.setText(getResources().getString(R.string.btn_player_out));
        isOut = false;
        position = 0;
    }

    public int getTichuScore() {
        if (saidTichu()){
            if (isFirst()) {
                return 100;
            } else {
                return -100;
            }
        } else if (saidGreatTichu()) {
            if (isFirst()) {
                return 200;
            } else {
                return -200;
            }
        }
        return 0;
    }

    public boolean getIsOut() {
        return isOut;
    }

}

