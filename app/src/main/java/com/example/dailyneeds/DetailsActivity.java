package com.example.dailyneeds;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import java.text.MessageFormat;
import java.util.Objects;

public class DetailsActivity extends AppCompatActivity {

    private TextView itemNameDetails;
    private TextView itemQtyDetails;
    private TextView itemColorDetails;
    private TextView itemSizeDetails;
    private TextView itemDateDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Item Description");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        itemNameDetails = findViewById(R.id.item_name_details);
        itemQtyDetails = findViewById(R.id.item_quantity_details);
        itemColorDetails = findViewById(R.id.item_color_details);
        itemSizeDetails = findViewById(R.id.item_size_details);
        itemDateDetails = findViewById(R.id.item_date_details);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){

            String name = bundle.getString("item_name");
            String qty = MessageFormat.format("Quantity: {0}", bundle.getString("item_quantity"));
            String color = MessageFormat.format("Color: {0}", bundle.getString("item_color"));
            String size = MessageFormat.format("Size: {0}", bundle.getString("item_size"));
            String date = MessageFormat.format("Date: {0}", bundle.getString("item_date"));

            itemNameDetails.setText(name);
            itemQtyDetails.setText(qty);
            itemColorDetails.setText(color);
            itemSizeDetails.setText(size);
            itemDateDetails.setText(date);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}