package com.pokemonify.pokemonify.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pokemonify.pokemonify.MainActivity;
import com.pokemonify.pokemonify.R;
import com.pokemonify.pokemonify.Utils;
import com.pokemonify.pokemonify.pokemondatabase.PokemonDatabase;
import com.pokemonify.pokemonify.pokemondatabase.PokemonDto;
import com.pokemonify.pokemonify.recyclerviewcomponents.ItemClickSupport;
import com.pokemonify.pokemonify.recyclerviewcomponents.PokemonListAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class PokemonListFragment extends Fragment {
    PokemonListAdapter mPokemonListAdapter;
    RecyclerView mRecyclerView;
    List<PokemonDto> nameList;
    String[] adjectiveList;
    TextView mSeachedText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        nameList = Arrays.asList(PokemonDatabase.getPokemonDtos());
        adjectiveList = getResources().getStringArray(R.array.adjectives);
        View view = inflater.inflate(R.layout.fragment_pokemon_list, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View v) {
        mSeachedText = (TextView) v.findViewById(R.id.searchedItem);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.pokemonRecyclerView);
        mPokemonListAdapter = new PokemonListAdapter(getActivity(), nameList);
        mRecyclerView.setAdapter(mPokemonListAdapter);
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        ItemClickSupport.addTo(mRecyclerView).setOnItemLongClickListener(new ItemClickSupport.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClicked(RecyclerView recyclerView, final int position, View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("My Pokemon");
                builder.setMessage("Set " + mPokemonListAdapter.getPokeList().get(position).getName() +
                        " as My pokemon?");
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ProgressDialog progressDialog=new ProgressDialog(getActivity());
                        progressDialog.setTitle("Making this your current pokemon");
                        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                        if(Utils.setMyPokemon(mPokemonListAdapter.getPokeList().get(position), getActivity())) {
                            progressDialog.dismiss();
                        }
                    }
                });
                builder.show();
                return true;
            }
        });
        ItemClickSupport.addTo(mRecyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                PokemonDto clickedPokemon = mPokemonListAdapter.getPokeList().get(position);
                MainActivity mainActivity = (MainActivity) getActivity();
                Bundle bundle = new Bundle();
                bundle.putSerializable("PokemonDto", clickedPokemon);
                PokemonDetailFragment detailFragment = new PokemonDetailFragment();
                detailFragment.setArguments(bundle);
                Utils.hideKeyboard(mainActivity);
                mainActivity.hideSearch();
                mainActivity.changeFrag(detailFragment);
            }
        });
    }

    public void search(String query) {
        mSeachedText.setText(query);
        Set<PokemonDto> newSet = new HashSet<>();
        String[] strings = query.toString().split(",");
        for (String s : strings) {
            for (PokemonDto p : nameList) {
                if (p.getName().toLowerCase().contains(s.toLowerCase())) {
                    Log.d("asjhas", p.getName());
                    newSet.add(p);
                }
            }
            for (String tmp : adjectiveList) {
                if (tmp.equals(s)) {
                    for (PokemonDto p : PokemonDatabase.getPokemonViaAdjective(s)) {
                        newSet.add(p);
                    }
                }
            }
        }
        mPokemonListAdapter.setPokeList(new ArrayList<PokemonDto>(newSet));
        mPokemonListAdapter.notifyDataSetChanged();
    }

    public PokemonListAdapter getPokemonListAdapter() {
        return mPokemonListAdapter;
    }
}
