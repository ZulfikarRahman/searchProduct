package com.tokopedia.zulfikar.searchproduct.controller.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.tokopedia.zulfikar.searchproduct.controller.AppController;
import com.tokopedia.zulfikar.searchproduct.R;
import com.tokopedia.zulfikar.searchproduct.model.Product;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Toped10 on 4/15/2016.
 */
public class SearchAdapter extends RecyclerView.Adapter {

    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    private List<Product> productList = new ArrayList<>();

    private final int VIEW_ITEM = 1;
    private final int VIEW_PROGRESS = 2;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, harga, toko;
        public NetworkImageView imageProduct;

        public MyViewHolder(View view) {
            super(view);
            imageProduct = (NetworkImageView) view.findViewById(R.id.image_product);

            title = (TextView) view.findViewById(R.id.title_product);
            toko = (TextView) view.findViewById(R.id.nama_toko);
            harga = (TextView) view.findViewById(R.id.harga_product);
        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar1);
        }
    }

    public SearchAdapter(List<Product> productList){
        this.productList = productList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder myViewHolder;

        if(viewType == VIEW_ITEM){
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.product_list_row, parent, false);
            myViewHolder = new MyViewHolder(itemView);
        }else{
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.progress_bar_item, parent, false);
            myViewHolder = new ProgressViewHolder(itemView);
        }


        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyViewHolder) {
            Product m_product = productList.get(position);
            ((MyViewHolder) holder).title.setText(m_product.getProduct_name());
            ((MyViewHolder) holder).toko.setText(m_product.getShop_name());
            ((MyViewHolder) holder).harga.setText(m_product.getProduct_price());
            ((MyViewHolder) holder).imageProduct.setDefaultImageResId(R.mipmap.ic_launcher);
            ((MyViewHolder) holder).imageProduct.setImageUrl(m_product.getProduct_image(), imageLoader);

        } else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }


    @Override
    public int getItemViewType(int position) {
        return productList.get(position) != null ? VIEW_ITEM : VIEW_PROGRESS;
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }
}
