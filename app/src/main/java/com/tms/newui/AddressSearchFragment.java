package com.tms.newui;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.tms.R;
import com.tms.newui.interf.AddressData;
import com.tms.newui.interf.GooglePlacesAutocompleteAdapter;
import com.tms.newui.interf.ListAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by viren on 15/12/17.
 */

public class AddressSearchFragment extends Fragment {

    private GooglePlacesAutocompleteAdapter dataAdapter;
    private GooglePlacesAutocompleteAdapter dataAdapter2;
    private EditText source, dest;
    ListView listView, listView2, listView3;
    private LinearLayout bottomView;
    private ArrayList<String> recentSearch;
    ArrayAdapter<String> searchAdapter;
    private SharedPreferences pref;
    private Set<String> recentSet;
    private MainActivity4 activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        activity = ((MainActivity4) getActivity());
        View view = inflater.inflate(R.layout.address_search, container, false);
        dataAdapter = new GooglePlacesAutocompleteAdapter(getActivity(), R.layout.list_item);
        dataAdapter2 = new GooglePlacesAutocompleteAdapter(getActivity(), R.layout.list_item);

        listView = (ListView) view.findViewById(R.id.listView1);
        listView.setAdapter(dataAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                source.removeTextChangedListener(textWatcher);
                source.setText(dataAdapter.getItem(i).secondText);
                listView.setVisibility(View.GONE);
                dataAdapter.resultList = new ArrayList<AddressData>();
                //dataAdapter.getFilter().filter("");
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        addTextListnerForSource();
                    }
                }, 100);
            }
        });


        listView2 = (ListView) view.findViewById(R.id.listView2);
        listView2.setAdapter(dataAdapter2);
        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                dest.removeTextChangedListener(destTextWatcher);
                dest.setText(dataAdapter2.getItem(i).secondText);

                try {
                    recentSet.add(dataAdapter2.getItem(i).secondText);
                    activity.sharedPreference.setRecents(recentSet);
                } catch (Exception e) {

                }
                listView2.setVisibility(View.GONE);
                dataAdapter2.resultList = new ArrayList<AddressData>();
                book();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        addTextListnerForDest();
                    }
                }, 100);
            }
        });

        try {
            recentSearch = new ArrayList<String>();
            recentSet = activity.sharedPreference.getRecents();
            if (recentSet != null) {
                for (String set : recentSet) {
                    recentSearch.add(set);
                }
            }else {
                if (recentSet == null) {
                    recentSet = new HashSet<String>();
                }
                recentSearch.add("No recents");
            }

            searchAdapter = new ListAdapter(getActivity(),
                    R.layout.search_item_view, recentSearch);

            listView3 = (ListView) view.findViewById(R.id.listView3);
            listView3.setAdapter(searchAdapter);
            listView3.setVisibility(View.VISIBLE);
            listView3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    dest.removeTextChangedListener(destTextWatcher);
                    dest.setText(searchAdapter.getItem(i));
                    listView3.setVisibility(View.GONE);
                    book();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            addTextListnerForDest();
                        }
                    }, 100);
                }
            });
        } catch (Exception e) {
            recentSet = new HashSet<String>();
            e.printStackTrace();
        }

        source = (EditText) view.findViewById(R.id.source);
        source.setText("Current location");
        dest = (EditText) view.findViewById(R.id.destLocation);
        addTextListnerForSource();
        addTextListnerForDest();

        ImageView switchAddress = (ImageView) view.findViewById(R.id.imageView11);
        switchAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                source.removeTextChangedListener(textWatcher);
                dest.removeTextChangedListener(destTextWatcher);
                String sourceAdd = source.getText().toString();
                String destAdd = dest.getText().toString();
                source.setText(destAdd);
                dest.setText(sourceAdd);
                addTextListnerForDest();
                addTextListnerForSource();
            }
        });

        ImageView backPress = (ImageView) view.findViewById(R.id.backBtn);
        backPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity4) getActivity()).backstackFragment();
            }
        });

        Button book = (Button) view.findViewById(R.id.btnBook);
      /*  book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeybord();
                ((MainActivity4) getActivity()).showRoute(dest.getText().toString());
            }
        });*/

        ((MainActivity4) getActivity()).setOnBundleSelected(new MainActivity4.SelectedBundle() {
            @Override
            public void onBundleSelect(Bundle bundle) {
                //updateSourceEditText(bundle);
            }
        });

        bottomView = (LinearLayout) view.findViewById(R.id.bottomView);
        return view;
    }

    private void updateSourceEditText(Bundle bundle) {
        source.removeTextChangedListener(textWatcher);
        source.setText(bundle.getString("source"));
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                addTextListnerForSource();
            }
        }, 100);

    }

    Handler handler = new Handler();

    public void book() {
        if (dest.getText() != null && source.getText() != null) {
            hideKeybord();
            ((MainActivity4) getActivity()).showRoute(dest.getText().toString(), source.getText().toString());
        }
    }

    public void addTextListnerForSource() {
        listView.setVisibility(View.VISIBLE);
        // listView2.setVisibility(View.VISIBLE);
        source.addTextChangedListener(textWatcher);
        // dest.addTextChangedListener(destTextWatcher);
    }

    public void addTextListnerForDest() {
        // listView.setVisibility(View.VISIBLE);
        listView2.setVisibility(View.VISIBLE);
        //  source.addTextChangedListener(textWatcher);
        dest.addTextChangedListener(destTextWatcher);
    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(final Editable editable) {
            /*timer.cancel();
            timer = new Timer();
            timer.schedule(
                    new TimerTask() {
                        @Override
                        public void run() {
                            dataAdapter.getFilter().filter(editable.toString());
                        }
                    },
                    DELAY
            );*/
            if (editable.length() > 2) {
                dataAdapter.getFilter().filter(editable.toString());
            }
        }
    };

    private Timer timer = new Timer();
    private final long DELAY = 500; // milliseconds
    TextWatcher destTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(final Editable editable) {
            if (editable.length() > 2) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dataAdapter2.getFilter().filter(editable.toString());
                        listView3.setVisibility(View.GONE);
                        listView2.setVisibility(View.VISIBLE);
                    }
                });
            } else {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listView2.setVisibility(View.GONE);
                        listView3.setVisibility(View.VISIBLE);
                        searchAdapter.notifyDataSetChanged();
                    }
                });

            }
        }
    };

    public void setSourceText(String address) {
        source.removeTextChangedListener(textWatcher);
        source.setText(address);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                addTextListnerForSource();
            }
        }, 100);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String address = ((MainActivity4) getActivity()).sourceAddress;
        if (address != null) {
            source.removeTextChangedListener(textWatcher);
            source.setText(address);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    addTextListnerForSource();
                }
            }, 100);
        }
    }

    private void hideKeybord() {
        InputMethodManager imm = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(dest.getWindowToken(), 0);

    }

    public void hideBottomView() {
        bottomView.setVisibility(View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }


}