package com.example.dailyneeds.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dailyneeds.R;
import com.example.dailyneeds.RecyclerViewClick;
import com.example.dailyneeds.data.DatabaseHandler;
import com.example.dailyneeds.model.Item;
import com.google.android.material.snackbar.Snackbar;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> implements Filterable {

    private Context context;
    private List<Item> itemList;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private LayoutInflater inflater;

    private List<Item> itemListAll;

    private RecyclerViewClick recyclerViewClick;

    public RecyclerViewAdapter(Context context, List<Item> itemList, RecyclerViewClick recyclerViewClick) {
        this.context = context;
        this.itemList = itemList;

        this.recyclerViewClick = recyclerViewClick;

        this.itemListAll = new ArrayList<>(itemList);
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_row,viewGroup,false);



        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder viewHolder, int position) {

        Item item = itemList.get(position);         // object item
        viewHolder.itemName.setText(MessageFormat.format("Item: {0}", item.getItemName()));
        viewHolder.itemColor.setText(MessageFormat.format("Color: {0}", item.getItemColor()));
        viewHolder.itemSize.setText(MessageFormat.format("Size: {0}", String.valueOf(item.getItemSize())));
        viewHolder.quantity.setText(MessageFormat.format("Quantity: {0}", String.valueOf(item.getItemQuantity())));
        viewHolder.dateAdded.setText(MessageFormat.format("Added on: {0}", String.valueOf(item.getDateItemAdded())));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {

        // this method run on background thread
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            List<Item> filteredList = new ArrayList<>();
            if(charSequence.toString().isEmpty()){
                filteredList.addAll(itemListAll);
            }
            else{
                for(Item item : itemListAll){
                    if(item.getItemName().toLowerCase().contains(charSequence.toString().toLowerCase())){
                        filteredList.add(item);
                    }
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;
            return filterResults;
        }

        // this method run on ui thread
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            itemList.clear();
            itemList.addAll((Collection<? extends Item>) filterResults.values);
            notifyDataSetChanged();
        }
    };

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public CardView itemCardView;
        public TextView itemName;
        public TextView itemColor;
        public TextView quantity;
        public TextView itemSize;
        public TextView dateAdded;
        public Button editButton;
        public Button deleteButton;
        public Button shareButton;
        public int id;

        public ViewHolder(@NonNull final View itemView, Context ctx) {
            super(itemView);
            context = ctx;

            itemCardView = itemView.findViewById(R.id.cardview);
            itemName = itemView.findViewById(R.id.item_name);
            itemColor = itemView.findViewById(R.id.item_color);
            quantity = itemView.findViewById(R.id.item_quantity);
            itemSize = itemView.findViewById(R.id.item_size);
            dateAdded = itemView.findViewById(R.id.item_date);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            shareButton = itemView.findViewById(R.id.shareButton);

            editButton.setOnClickListener(this);
            deleteButton.setOnClickListener(this);
            shareButton.setOnClickListener(this);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    recyclerViewClick.onItemClick(getAdapterPosition());
                }
            });
        }


        @Override
        public void onClick(View view) {

            int position;
            position = getAdapterPosition();
            Item item = itemList.get(position);

            switch (view.getId()) {

                case R.id.editButton:
                    //edit item
                    editItem(item);
                    break;

                case R.id.deleteButton:
                    //delete item
                    deleteItem(item.getId());
                    break;

                case R.id.shareButton:
                    // share item
                    shareItem(item);
                    break;
            }
        }

        private void shareItem(Item item) {

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, "Shopping List");
            intent.putExtra(Intent.EXTRA_TEXT,
                    item.getItemName() + "\nQuantity:"
                    + item.getItemQuantity() + "\nColor:"
                    + item.getItemColor() + "\nSize:"
                    + item.getItemSize() );
            context.startActivity(intent);

        }

        private void deleteItem(final int id) {

            builder = new AlertDialog.Builder(context);
            inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.confirmation_pop, null);

            Button noButton = view.findViewById(R.id.conf_no_button);
            Button yesButton = view.findViewById(R.id.conf_yes_button);

            builder.setView(view);
            dialog = builder.create();
            dialog.show();

            noButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            yesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DatabaseHandler db = new DatabaseHandler(context);
                    db.deleteItem(id);
                    itemList.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                    dialog.dismiss();
                }
            });


        }


        private void editItem(final Item newItem) {

            //Get current object data
            //final Item item = itemList.get(getAdapterPosition());
            builder = new AlertDialog.Builder(context);
            inflater = LayoutInflater.from(context);
            final View view = inflater.inflate(R.layout.popup, null);

            Button saveButton;
            final EditText babyItem;
            final EditText itemQuantity;
            final EditText itemColor;
            final EditText itemSize;
            TextView title;

            babyItem = view.findViewById(R.id.babyItem);
            itemQuantity = view.findViewById(R.id.itemQuantity);
            itemColor = view.findViewById(R.id.itemColor);
            itemSize = view.findViewById(R.id.itemSize);
            saveButton = view.findViewById(R.id.saveButton);
            title = view.findViewById(R.id.title);

            title.setText(R.string.edit_item);
            babyItem.setText(newItem.getItemName());
            itemQuantity.setText(String.valueOf(newItem.getItemQuantity()));
            itemColor.setText(newItem.getItemColor());
            itemSize.setText(String.valueOf(newItem.getItemSize()));
            saveButton.setText(R.string.update_text);

            builder.setView(view);
            dialog = builder.create();
            dialog.show();

            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //update our item
                    DatabaseHandler databaseHandler = new DatabaseHandler(context);


                    if (!babyItem.getText().toString().isEmpty()
                            && !itemQuantity.getText().toString().isEmpty()
                            && !itemColor.getText().toString().isEmpty()
                            && !itemSize.getText().toString().isEmpty()) {

                        newItem.setItemName(babyItem.getText().toString());
                        newItem.setItemQuantity(Integer.parseInt(itemQuantity.getText().toString()));
                        newItem.setItemColor(itemColor.getText().toString());
                        newItem.setItemSize(Integer.parseInt(itemSize.getText().toString()));

                        databaseHandler.updateItem(newItem);
                        notifyItemChanged(getAdapterPosition(),newItem);

                        Snackbar.make(view, "Item updated", Snackbar.LENGTH_SHORT).show();

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //code to be run
                                dialog.dismiss();
                            }
                        },1200);

                    } else {

                        Snackbar.make(view, "Fields Empty", Snackbar.LENGTH_SHORT).show();

                    }

                }
            });
        }
    }
}
