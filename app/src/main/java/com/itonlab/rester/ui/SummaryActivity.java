package com.itonlab.rester.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.itonlab.rester.R;
import com.itonlab.rester.adapter.OrderItemListAdapter;
import com.itonlab.rester.database.ResterDao;
import com.itonlab.rester.model.MenuItem;
import com.itonlab.rester.model.OrderItemDetail;
import com.itonlab.rester.model.Picture;
import com.itonlab.rester.model.PreOrderItem;
import com.itonlab.rester.util.AppPreference;
import com.itonlab.rester.util.JsonFunction;

import java.util.ArrayList;

import app.akexorcist.simpletcplibrary.SimpleTCPClient;
import app.akexorcist.simpletcplibrary.SimpleTCPServer;

public class SummaryActivity extends Activity {
    public final int TCP_PORT = 21111;
    private SimpleTCPServer server;
    ArrayList<OrderItemDetail> orderItemDetails;
    OrderItemListAdapter orderItemListAdapter;
    ResterDao databaseDao;

    private Button btnConfirm;
    private TextView tvTotalPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);
        server = new SimpleTCPServer(TCP_PORT);
        databaseDao = new ResterDao(SummaryActivity.this);
        databaseDao.open();

        orderItemDetails = databaseDao.getSummaryPreOrder();

        ListView lvSummary = (ListView) findViewById(R.id.lvSummary);
        orderItemListAdapter = new OrderItemListAdapter(
                SummaryActivity.this, orderItemDetails, R.layout.summary_list_item);
        lvSummary.setAdapter(orderItemListAdapter);
        lvSummary.setOnItemClickListener(summaryOnItemClickListener);

        tvTotalPrice = (TextView) findViewById(R.id.tvTotalPrice);
        tvTotalPrice.setText(String.valueOf(findTotalPrice(orderItemDetails)));

        btnConfirm = (Button) findViewById(R.id.btnConfirm);
        if (orderItemDetails.size() > 0) {
            btnConfirm.setOnClickListener(confirmOnItemClickListener);
        } else {
            btnConfirm.setEnabled(false);
        }

    }

    @Override
    protected void onResume() {
        databaseDao.open();
        super.onResume();
        server.start();
    }

    @Override
    protected void onPause() {
        databaseDao.close();
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        server.stop();

    }

    private double findTotalPrice(ArrayList<OrderItemDetail> orderItemDetails) {
        double totalPrice = 0;
        for (OrderItemDetail orderItemDetail : orderItemDetails) {
            totalPrice += (orderItemDetail.getPrice() * orderItemDetail.getQuantity());
        }

        return totalPrice;
    }

    View.OnClickListener confirmOnItemClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            sendOrderToMaster();
        }
    };

    AdapterView.OnItemClickListener summaryOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final int itemId = (int) id;
            final int itemPosition = position;
            AlertDialog.Builder builder = new AlertDialog.Builder(SummaryActivity.this);
            builder.setMessage("ต้องการลบหรือแก้ไข")
                    .setPositiveButton("ลบ", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            databaseDao.removePreOrderItem(itemId);
                            orderItemDetails.remove(itemPosition);
                            dialog.dismiss();
                            orderItemListAdapter.notifyDataSetChanged();
                            // calculate new total price.
                            tvTotalPrice.setText(String.valueOf(findTotalPrice(orderItemDetails)));
                        }
                    })
                    .setNegativeButton("แก้ไข", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            showDialogEditSummary(itemPosition);
                        }
                    });
            // Create the AlertDialog object
            AlertDialog dialog = builder.create();
            dialog.show();


        }
    };

    private void showDialogEditSummary(final int itemPosition) {
        final OrderItemDetail orderItemDetail = orderItemDetails.get(itemPosition);

        final Dialog dialogEditSummary = new Dialog(SummaryActivity.this);
        dialogEditSummary.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogEditSummary.setCancelable(true);
        dialogEditSummary.setContentView(R.layout.dialog_edit_summary);
        // show detail of food by menu code
        MenuItem menuItem = databaseDao.getMenuByCode(orderItemDetail.getMenuCode());

        TextView tvName = (TextView) dialogEditSummary.findViewById(R.id.tvName);
        tvName.setText(menuItem.getNameThai());

        TextView tvPrice = (TextView) dialogEditSummary.findViewById(R.id.tvPrice);
        tvPrice.setText(Double.toString(menuItem.getPrice()));

        final EditText etOption = (EditText) dialogEditSummary.findViewById(R.id.editTextOption);
        etOption.setText(orderItemDetail.getOption());

        ImageView ivImgFood = (ImageView) dialogEditSummary.findViewById(R.id.ivImgFood);
        Picture picture = databaseDao.getMenuPicture(menuItem.getPictureId());
        ivImgFood.setImageBitmap(picture.getBitmapPicture());

        dialogEditSummary.show();

        final EditText etAmount = (EditText) dialogEditSummary.findViewById(R.id.etAmount);
        etAmount.setText(String.valueOf(orderItemDetail.getQuantity()));
        Button btnOK = (Button) dialogEditSummary.findViewById(R.id.btnOK);
        btnOK.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int amount = Integer.parseInt(etAmount.getText().toString());
                String option = etOption.getText().toString();
                // In case the user entering zero or negative, skip updating.
                if (amount > 0) {
                    orderItemDetail.setQuantity(amount);
                    orderItemDetail.setOption(option);
                    PreOrderItem preOrderItem = new PreOrderItem();
                    preOrderItem.setId(orderItemDetail.getPreOderId());
                    preOrderItem.setQuantity(amount);
                    preOrderItem.setOption(option);
                    preOrderItem.setMenuCode(orderItemDetail.getMenuCode());
                    databaseDao.updatePreOrder(preOrderItem);
                }
                // Remove from item list. However, it will add back later.
                orderItemDetails.remove(itemPosition);
                // Re-add to item list.
                orderItemDetails.add(itemPosition, orderItemDetail);
                dialogEditSummary.dismiss();
                orderItemListAdapter.notifyDataSetChanged();
                // calculate new total price.
                tvTotalPrice.setText(String.valueOf(findTotalPrice(orderItemDetails)));
            }
        });

        dialogEditSummary.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                // do not anything.
            }
        });
    }

    private void sendOrderToMaster() {
        ArrayList<PreOrderItem> preOrderItems = databaseDao.getAllPreOrderItem();
        JsonFunction jsonFunction = new JsonFunction(SummaryActivity.this);
        String json = jsonFunction.getJSONOrderMessage(preOrderItems, findTotalPrice(orderItemDetails));
        Log.d("JSON", json);

        AppPreference appPreference = new AppPreference(SummaryActivity.this);
        String ip = appPreference.getMasterIP();
        SimpleTCPClient.send(json, ip, TCP_PORT, new SimpleTCPClient.SendCallback() {
            public void onSuccess(String tag) {
                databaseDao.clearPreOrder();
                finish();
            }

            public void onFailed(String tag) {
                AlertDialog alertDialog = new AlertDialog.Builder(SummaryActivity.this).create();
                alertDialog.setTitle("Alert");
                alertDialog.setMessage("Can't connect to master");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        }, "TAG");

    }

}
