package com.itonlab.rester.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
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
import com.itonlab.rester.model.PreOrderTable;
import com.itonlab.rester.util.AppPreference;
import com.itonlab.rester.util.JsonFunction;

import org.json.JSONException;
import org.json.JSONObject;

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
                JsonFunction.Message jsonMessage = JsonFunction.acceptMessage(message);
                jsonFunction.decideWhatToDo(jsonMessage); // It's update data to database.
                //if message is ORDER_STATUS_MESSAGE let update immediately.
                if (jsonMessage.getMessageType().equals(JsonFunction.Message.Type.ORDER_STATUS_MESSAGE)) {
                    JSONObject body = jsonMessage.getJsonBody();
                    try {
                        int updateItemId = body.getInt("pre_id");
                        for (int i = 0; i < preOrderItemDetails.size(); i++) {
                            OrderItemDetail preOrderItemDetail = preOrderItemDetails.get(i);
                            if (preOrderItemDetail.getPreOderId() == updateItemId) {
                                preOrderItemDetail.setServed(body.getInt("served") == 1);
                                int statusValue = body.getInt("status");
                                preOrderItemDetail.setStatus(
                                        (statusValue == 1) ?
                                                PreOrderItem.Status.DONE : PreOrderItem.Status.UNDONE);
                                // refresh LisView to display new data
                                preOrderItemDetails.remove(i);
                                preOrderItemDetails.add(i, preOrderItemDetail);
                                orderItemListAdapter.notifyDataSetChanged();
                                break;
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (jsonMessage.getMessageType().equals(JsonFunction.Message.Type.EDIT_ORDER_MESSAGE)) {
                    JSONObject body = jsonMessage.getJsonBody();
                    try {
                        int editItemId = body.getInt("pre_id");
                        for (int i = 0; i < preOrderItemDetails.size(); i++) {
                            OrderItemDetail preOrderItemDetail = preOrderItemDetails.get(i);
                            if (preOrderItemDetail.getPreOderId() == editItemId) {
                                preOrderItemDetail.setQuantity(body.getInt(PreOrderTable.Columns._QUANTITY));
                                preOrderItemDetail.setOption(body.getString(PreOrderTable.Columns._OPTION));
                                // refresh LisView to display new data
                                preOrderItemDetails.remove(i);
                                preOrderItemDetails.add(i, preOrderItemDetail);
                                orderItemListAdapter.notifyDataSetChanged();
                                // re-calculate total price
                                tvTotalPrice.setText(String.valueOf(findTotalPrice(preOrderItemDetails)));
                                break;
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else if (jsonMessage.getMessageType().equals(JsonFunction.Message.Type.PAY_CONFIRM_MESSAGE)) {

                }
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
            if (!view.isEnabled()) {
                return;
            }

            final int itemId = (int) id;
            final int itemPosition = position;

            AlertDialog.Builder builder = new AlertDialog.Builder(SummaryActivity.this);
            builder.setMessage(R.string.text_delete_or_edit)
                    .setPositiveButton(R.string.text_delete, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            databaseDao.removePreOrderItem(itemId);
                            preOrderItemDetails.remove(itemPosition);
                            dialog.dismiss();
                            orderItemListAdapter.notifyDataSetChanged();
                            // calculate new total price.
                            tvTotalPrice.setText(String.valueOf(findTotalPrice(preOrderItemDetails)));
                        }
                    })
                    .setNegativeButton(R.string.text_edit, new DialogInterface.OnClickListener() {
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
        AppPreference appPreference = new AppPreference(SummaryActivity.this);
        if (appPreference.getAppLanguage().equals("th")) {
            tvName.setText(menuItem.getNameThai());
        } else {
            tvName.setText(menuItem.getNameEng());
        }

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
                    // update data in database
                    ContentValues values = new ContentValues();
                    values.put(PreOrderTable.Columns._QUANTITY, amount);
                    values.put(PreOrderTable.Columns._OPTION, option);
                    databaseDao.updatePreOrderByValues(orderItemDetail.getPreOderId(), values);
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
        builder.setMessage(R.string.text_choose_take)
                .setPositiveButton(R.string.text_take_here, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        appPreference.saveTakeOrder(Order.Take.HERE);
                        String take = "*" + getResources().getString(R.string.text_take_here);
                        tvTake.setText(take);
                    }
                })
                .setNegativeButton(R.string.text_take_home, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        appPreference.saveTakeOrder(Order.Take.HOME);
                        String take = "*" + getResources().getString(R.string.text_take_home);
                        tvTake.setText(take);
                    }
                });
        // Create the AlertDialog object
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

}
