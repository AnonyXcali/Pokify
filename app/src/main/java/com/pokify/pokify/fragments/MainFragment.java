package com.pokify.pokify.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pokify.pokify.MainActivity;
import com.pokify.pokify.R;
import com.pokify.pokify.Utils;
import com.pokify.pokify.pokemondatabase.PokemonDto;

public class MainFragment extends Fragment {

    TextView mMyPokemonName;
    TextView mMyPokemonHp;
    TextView mMyPokemonType;
    TextView mMyPokemonLevel;
    TextView mMyPokemonWeight;
    TextView mMyPokemonHeight;
    TextView mMyPokemonDesc;
    ImageView mMyPokemonImage;
    View mMyPokemonScreen;
    PokemonDto currentMyPokemon;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle("Pokify");
        currentMyPokemon = Utils.getRandomPokemon();
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View v) {
        mMyPokemonName = (TextView) v.findViewById(R.id.my_pokemon_name);
        mMyPokemonHp = (TextView) v.findViewById(R.id.my_pokemon_hp);
        mMyPokemonType = (TextView) v.findViewById(R.id.my_pokemon_type);
        mMyPokemonLevel = (TextView) v.findViewById(R.id.my_pokemon_level);
        mMyPokemonWeight = (TextView) v.findViewById(R.id.my_pokemon_weight);
        mMyPokemonHeight = (TextView) v.findViewById(R.id.my_pokemon_height);
        mMyPokemonDesc = (TextView) v.findViewById(R.id.my_pokemon_desc);
        mMyPokemonImage = (ImageView) v.findViewById(R.id.my_pokemon_image);
        mMyPokemonScreen = v.findViewById(R.id.my_pokemon_screen);
        mMyPokemonName.setText(currentMyPokemon.getName());
        mMyPokemonHp.setText(currentMyPokemon.getHp() + " Hp");
        mMyPokemonType.setText(currentMyPokemon.getType());
        mMyPokemonLevel.setText("Exp " + currentMyPokemon.getLevel());
        mMyPokemonWeight.setText(currentMyPokemon.getWeight() + " lbs");
        mMyPokemonHeight.setText(currentMyPokemon.getHeight() + " Inch");
        mMyPokemonDesc.setText(currentMyPokemon.getDesc());
        mMyPokemonImage.getLayoutParams().height = (int) (Utils.getDisplayHeight(getActivity()) * 0.40);
        Bitmap bitmap = null;
        if (currentMyPokemon.getImagePath().equals("-1")) {
            Log.d("file path stored is", currentMyPokemon.getBitmapPath());
            bitmap = BitmapFactory.decodeFile(currentMyPokemon.getBitmapPath());
            if (bitmap != null) {
                bitmap = Utils.getRoundedCornerBitmap(bitmap);
            } else {
                Toast.makeText(getActivity(), "Something went wrong.Please set your pokemon again." +
                        "We're really sorry.", Toast.LENGTH_LONG).show();
                bitmap = Utils.getRoundedCornerBitmap(BitmapFactory.decodeResource(getResources(), getResources()
                        .getIdentifier("exeggutor", "drawable", getActivity().getPackageName())));
            }
        } else {
            bitmap = BitmapFactory.decodeResource(getResources(), getResources()
                    .getIdentifier(currentMyPokemon.getImagePath(), "drawable", getActivity().getPackageName()));
        }
        mMyPokemonImage.setImageBitmap(bitmap);
        if (Utils.isTypePresent(currentMyPokemon.getType().toLowerCase())) {
            mMyPokemonScreen.setBackground(new BitmapDrawable(BitmapFactory.decodeResource(getResources(),
                    getResources().getIdentifier(currentMyPokemon.getType().toLowerCase(), "drawable",
                            getActivity().getPackageName()))));
        } else {
            mMyPokemonScreen.setBackground(getResources().getDrawable(R.drawable.standardbackground));
        }
    }

    public void shareMyPokemon() {
        mMyPokemonScreen.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(mMyPokemonScreen.getDrawingCache());
        mMyPokemonScreen.destroyDrawingCache();
        ((MainActivity) getActivity()).shareImage(bitmap);
    }

    public void saveMyPokemon() {
        mMyPokemonScreen.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(mMyPokemonScreen.getDrawingCache());
        mMyPokemonScreen.destroyDrawingCache();
        Utils.saveFile(getActivity(), bitmap, currentMyPokemon.getId());
    }
}
