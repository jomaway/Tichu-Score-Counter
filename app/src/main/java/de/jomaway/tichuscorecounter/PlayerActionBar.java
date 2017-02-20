package de.jomaway.tichuscorecounter;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Checkable;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;
import static android.os.Build.VERSION_CODES.M;
import static de.jomaway.tichuscorecounter.R.drawable.cards;


/**
 * Created by jsma on 13.02.2017.
 */

public class PlayerActionBar extends LinearLayout {
    private static final String TAG = "PlayerActionBar";

    private int mBindToPlayer;
    private CheckedTextView mTichu;
    private CheckedTextView mGreatTichu;
    private CheckedTextView mOut;

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
    private boolean toogleChecked(Checkable view){
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
                Log.d(TAG,"Tichu");
                toogleChecked(mTichu);
            }
        });
        mGreatTichu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG," Great Tichu");
                toogleChecked(mGreatTichu);
            }
        });
        mOut.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "player out");
                if (toogleChecked(mOut)) {
                    mOut.setText("1");
                } else {
                    mOut.setText(getResources().getString(R.string.btn_player_out));
                }
            }
        });
    }

}

