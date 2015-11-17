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
import com.itonlab.rester.model.Order;
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
    ArrayList<OrderItemDetail> preOrderItemDetails;
    OrderItemListAdapter orderItemListAdapter;
    ResterDao databaseDao;

    private Button btnConfirm;
    private TextView tvTake, tvTotalPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);
        server = new SimpleTCPServer(TCP_PORT);
        server.setOnDataReceivedListener(new SimpleTCPServer.OnDataReceivedListener() {
            @Override
            public void onDataReceived(String message, String ip) {
                JsonFunction jsonFunction = new JsonFunction(getApplicationContext());
                jsonFunction.decideWhatToDo(JsonFunction.acceptMessage(message));
            }
        });
        databaseDao = new ResterDao(SummaryActivity.this);
        databaseDao.open();

        preOrderItemDetails = databaseDao.getPreOrderDetail();

        ListView lvSummary = (ListView) findViewById(R.id.lvSummary);
        orderItemListAdapter = new OrderItemListAdapter(SummaryActivity.this, preOrderItemDetails);
        lvSummary.setAdapter(orderItemListAdapter);
        lvSummary.setOnItemClickListener(summaryOnItemClickListener);

        tvTake = (TextView) findViewById(R.id.textViewTake);
        String take = "*" + getResources().getString(R.string.text_take_here);
        if (new AppPreference(SummaryActivity.this).getTakeOrder().equals(Order.Take.HOME)) {
            take = "*" + getResources().getString(R.string.text_take_home);
        }
        tvTake.setText(take);

        tvTotalPrice = (TextView) findViewById(R.id.tvTotalPrice);
        tvTotalPrice.setText(String.valueOf(findTotalPrice(preOrderItemDetails)));

        btnConfirm = (Button) findViewById(R.id.btnConfirm);
        btnConfirm.setEnabled(false);
        if ((preOrderItemDetails.size() > 0)) {
            if (!preOrderItemDetails.get(0).isOrdered()) {
                // if first item is not ordered indicate it have items that are not ordered.
                btnConfirm.setEnabled(true);
                btnConfirm.setOnClickListener(confirmOnItemClickListener);
            }
            if (!preOrderItemDetails.get(preOrderItemDetails.size() - 1).isOrdered()) {
                // first time to order food
                showChooseTakeOrderDialog();

            }
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

    private double findTotalPriceNotOrdered(ArrayList<OrderItemDetail> orderItemDetails) {
        double totalPrice = 0;
        for (OrderItemDetail orderItemDetail : orderItemDetails) {
            if (!orderItemDetail.isOrdered()) {
                totalPrice += (orderItemDetail.getPrice() * orderItemDetail.getQuantity());
            }
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
            view.isActivated();
            final int itemId = (int) id;
            final int itemPosition = position;

            AlertDialog.Builder builder = new AlertDialog.Builder(SummaryActivity.this);
            builder.setMessage("ต้องการลบหรือแก้ไข")
                    .setPositiveButton("ลบ", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            databaseDao.removePreOrderItem(itemId);
                            preOrderItemDetails.remove(itemPosition);
                            dialog.dismiss();
                            orderItemListAdapter.notifyDataSetChanged();
                            // calculate new total price.
                            tvTotalPrice.setText(String.valueOf(findTotalPrice(preOrderItemDetails)));
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
        final OrderItemDetail orderItemDetail = preOrderItemDetails.get(itemPosition);

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
                preOrderItemDetails.remove(itemPosition);
                // Re-add to item list.
                preOrderItemDetails.add(itemPosition, orderItemDetail);
                dialogEditSummary.dismiss();
                orderItemListAdapter.notifyDataSetChanged();
                // calculate new total price.
                tvTotalPrice.setText(String.valueOf(findTotalPrice(preOrderItemDetails)));
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
        final ArrayList<PreOrderItem> preOrderItemNotOrderedList = databaseDao.getPreOrderItemNotOrdered();
        if (preOrderItemNotOrderedList.size() > 0) {
            JsonFunction jsonFunction = new JsonFunction(SummaryActivity.this);
            String json = jsonFunction.getJSONOrderMessage(preOrderItemNotOrderedList,
                    findTotalPriceNotOrdered(preOrderItemDetails));
            Log.d("JSON", json);

            AppPreference appPreference = new AppPreference(SummaryActivity.this);
            String ip = appPreference.getMasterIP();
            SimpleTCPClient.send(json, ip, TCP_PORT, new SimpleTCPClient.SendCallback() {
                public void onSuccess(String tag) {
                    // change ordered of pre-order to true(1)
                    databaseDao.updatePreOderToOrdered(preOrderItemNotOrderedList);

                    finish();
                }

                public void onFailed(String tag) {
                    AlertDialog alertDialog = new AlertDialog.Builder(SummaryActivity.this).create();
                    alertDialog.setTitle("Alert");
                    alertDialog.setMessage(getString(R.string.send_order_fialed));
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

    private void showChooseTakeOrderDialog() {
        final AppPreference appPreference = new AppPreference(SummaryActivity.this);
        AlertDialog.Builder builder = new AlertDialog.Builder(SummaryActivity.this);
        builder.setMessage("คุณต้องสั่งอาหารเพื่อทานที่ไหน?")
                .setPositiveButton("ทานที่ร้าน", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        appPreference.saveTakeOrder(Order.Take.HERE);
                        String take = "*" + getResources().getString(R.string.text_take_here);
                        tvTake.setText(take);
                    }
                })
                .setNegativeButton("สั่งกลับบ้าน", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        appPreference.saveTakeOrder(Order.Take.HOME);
                        String take = "*" + getResources().getString(R.string.text_take_home);
                        tvTake.setText(take);
                    }
                });
        // Create the AlertDialog object
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
