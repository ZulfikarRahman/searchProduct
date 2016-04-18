package com.tokopedia.zulfikar.searchproduct.view;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.net.Uri;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.tokopedia.zulfikar.searchproduct.R;
import com.tokopedia.zulfikar.searchproduct.controller.adapter.SearchAdapter;
import com.tokopedia.zulfikar.searchproduct.controller.AppController;
import com.tokopedia.zulfikar.searchproduct.controller.util.EndlessRecyclerViewScrollListener;
import com.tokopedia.zulfikar.searchproduct.model.Product;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, SearchView.OnCloseListener {

    String url = "https://ace.tokopedia.com/search/v2/product?device=android&os_type=1&rows=10";
    List<Product> productList = new ArrayList<>();
    RecyclerView recyclerView;
    SearchAdapter mAdapter;

    private Menu menu;
    private boolean isListView;
    private StaggeredGridLayoutManager mStaggeredLayoutManager;

    private String Key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.list_item_search);
        mAdapter = new SearchAdapter(productList);

        //RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        //recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.addItemDecoration(new MarginDecoration(this));
        recyclerView.setAdapter(mAdapter);
        mStaggeredLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        //recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setLayoutManager(mStaggeredLayoutManager);
        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(mStaggeredLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                loadData(Key, false, totalItemsCount);
            }
        });
    }

    public void loadData(String key, final boolean isClear, final int CurrentPosition){
        String params = "&q=" + Uri.encode(key) + "&start=" + Uri.encode(String.valueOf(CurrentPosition));
        JsonObjectRequest listReq = new JsonObjectRequest(url + params , null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    if(isClear) {
                        productList.clear();
                    }

                    String status = jsonObject.getString("status");
                    if (status.equals("OK")){
                        JSONObject data = jsonObject.getJSONObject("data");
                        JSONArray list_product = data.getJSONArray("products");

                        for(int i = 0; i<list_product.length(); i++){
                            Product m_product = new Product();
                            JSONObject product = list_product.getJSONObject(i);
                            m_product.setProduct_url(product.getString("product_url"));
                            m_product.setProduct_name(product.getString("product_name"));
                            m_product.setProduct_id(product.getString("product_id"));
                            m_product.setProduct_id(product.getString("product_image"));
                            m_product.setProduct_image_full(product.getString("product_image_full"));
                            m_product.setProduct_image(product.getString("product_image"));
                            m_product.setProduct_price(product.getString("product_price"));
                            m_product.setShop_name(product.getString("shop_name"));

                            productList.add(m_product);
                        }
                    }
                } catch (JSONException e) {
                    Log.v("error", "something wrong");
                    e.printStackTrace();
                }
                mAdapter.notifyItemInserted(productList.size());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
            }
        });

        int timeout = 30000; //30 detik
        RetryPolicy policy = new DefaultRetryPolicy(timeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        listReq.setRetryPolicy(policy);
        AppController.getInstance().addToRequestQueue(listReq);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem search_item = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(search_item);
        searchView.setIconifiedByDefault(true);
        searchView.setOnQueryTextListener(this);
        searchView.setOnCloseListener(this);

        this.menu = menu;
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        loadData(query, true, 0);
        this.Key = query;
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public boolean onClose() {
        productList.clear();
        mAdapter.notifyDataSetChanged();
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_toggle) {
            toggle();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void toggle() {
        MenuItem item = menu.findItem(R.id.action_toggle);
        if (isListView) {
            item.setIcon(R.mipmap.ic_action_list);
            mStaggeredLayoutManager.setSpanCount(2);
            item.setTitle("Show as list");
            isListView = false;
        } else {
            item.setIcon(R.mipmap.ic_action_grid);
            mStaggeredLayoutManager.setSpanCount(1);
            item.setTitle("Show as grid");
            isListView = true;
        }
    }
}
