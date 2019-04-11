package com.brainyapps.motolabz.Fragments.MechanicsFragments;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.brainyapps.motolabz.Adapters.RatingRecyclerAdapter;
import com.brainyapps.motolabz.Constants.DBInfo;
import com.brainyapps.motolabz.MechanicsView.MechanicEditProfileActivity;
import com.brainyapps.motolabz.MechanicsView.MechanicMainActivity;
import com.brainyapps.motolabz.MechanicsView.MechanicReplyReviewActivity;
import com.brainyapps.motolabz.Models.Mechanic;
import com.brainyapps.motolabz.Models.RateReview;
import com.brainyapps.motolabz.Models.RepairShop;
import com.brainyapps.motolabz.R;
import com.brainyapps.motolabz.Utils.FirebaseManager;
import com.brainyapps.motolabz.Views.AlertFactory;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.walnutlabs.android.ProgressHUD;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class MechanicProfileFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, RatingRecyclerAdapter.OnClickItemListener{

    private CircleImageView avatar;
    private TextView name;
    private TextView license;
    private TextView shop_name;
    private TextView description;

    private ImageView editName;
    private ImageView editShop;
    private ImageView editBrief;

    private ImageView edit_profile;
    private SwitchCompat available_switch;
    private TextView available_text;
    private Boolean isAvailable = false;
    boolean currentAvailability = false;

    private RatingRecyclerAdapter ratingRecyclerAdapter;
    private RecyclerView ratingRecyclerView;
    ArrayList<RateReview> rateList = new ArrayList<>();
    private String myId = FirebaseManager.getInstance().getUserId();
    private Mechanic mechanic;

    public static final String FRAGMENT_TAG = "com_motolabz_mechanic_profile_fragment_tag";
    private static Context mContext;

    private ProgressHUD mProgressDialog;

    private void showProgressHUD(String text) {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }

        mProgressDialog = ProgressHUD.show(getActivity(), text, true);
        mProgressDialog.show();
    }

    private void hideProgressHUD() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }
    public static android.app.Fragment newInstance(Context context) {
        mContext = context;

        android.app.Fragment f = new MechanicProfileFragment();
        return f;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_mechanic_profile, container, false);

        avatar = (CircleImageView)rootView.findViewById(R.id.mechanic_profile_avatar);
        avatar.setOnClickListener(this);
        name = (TextView)rootView.findViewById(R.id.mechanic_profile_name);
        license = (TextView)rootView.findViewById(R.id.mechanic_profile_license);
        shop_name = (TextView)rootView.findViewById(R.id.mechanic_profile_shop_name);
        description = (TextView)rootView.findViewById(R.id.mechanic_profile_description);

        editName = (ImageView) rootView.findViewById(R.id.mechanic_profile_name_edit);
        editName.setOnClickListener(this);
        editShop = (ImageView) rootView.findViewById(R.id.mechanic_profile_shop_edit);
        editShop.setOnClickListener(this);
        editBrief = (ImageView)rootView.findViewById(R.id.mechanic_profile_brief_edit);
        editBrief.setOnClickListener(this);

        edit_profile = (ImageView)rootView.findViewById(R.id.mechanic_profile_edit);
        edit_profile.setOnClickListener(this);
        available_switch = (SwitchCompat)rootView.findViewById(R.id.mechanic_available_switch);
        available_switch.setOnCheckedChangeListener(this);
        available_text = (TextView)rootView.findViewById(R.id.mechanic_available_text);

        ratingRecyclerAdapter = new RatingRecyclerAdapter(rateList);
        ratingRecyclerView = (RecyclerView) rootView.findViewById(R.id.mechanic_rating_recycler_view);
        ratingRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        ratingRecyclerView.setAdapter(ratingRecyclerAdapter);

        getRateList();
        return rootView;
    }

    public void getInfo(){
        mechanic = FirebaseManager.getInstance().getCurrentMechanic();
        if(!mechanic.photoUrl.isEmpty()){
            Glide.with(getActivity()).load(mechanic.photoUrl).into(avatar);
        }
        name.setText(mechanic.fullName);
        ((MechanicMainActivity)getActivity()).setTitle(mechanic.fullName+" Profile");
        license.setText(mechanic.licenseNumber);
        shop_name.setText(mechanic.shopName);
        description.setText(mechanic.description);
        if(!mechanic.shopID.isEmpty()){
            checkStatus(mechanic.shopID);
        }else {
            if(mechanic.available){
                available_switch.setChecked(true);
                isAvailable = true;
                available_text.setText("(Available)");
            }else {
                available_switch.setChecked(false);
                isAvailable = false;
                available_text.setText("(Unavailable)");
            }
        }
//        showProgressHUD("");
//        FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_USER).child(myId).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if(dataSnapshot.exists()){
//                    mechanic = dataSnapshot.getValue(Mechanic.class);
//                    Glide.with(getActivity()).load(mechanic.photoUrl).into(avatar);
//                    name.setText(mechanic.fullName);
//                    ((MechanicMainActivity)getActivity()).setTitle(mechanic.fullName+" Profile");
//                    license.setText(mechanic.licenseNumber);
//                    shop_name.setText(mechanic.shopName);
//                    description.setText(mechanic.description);
//
//                    if(mechanic.available){
//                        available_switch.setChecked(true);
//                        isAvailable = true;
//                        available_text.setText("(Available)");
//                    }else {
//                        available_switch.setChecked(false);
//                        isAvailable = false;
//                        available_text.setText("(Unavailable)");
//                    }
//                }
//                hideProgressHUD();
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                hideProgressHUD();
//            }
//        });
    }

    public void checkStatus(String shopID){
        FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_USER).child(shopID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    RepairShop shop = dataSnapshot.getValue(RepairShop.class);
                    if(shop.available){
                        Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat mdformat = new SimpleDateFormat("HH:mm");
                        String strDate = mdformat.format(calendar.getTime());
                        int day = calendar.get(Calendar.DAY_OF_WEEK);
                        switch (day) {
                            case Calendar.SUNDAY:
                                if(dataSnapshot.child("businessTime").child("Sunday").child("status").getValue().toString().equals("false")){
                                    available_text.setText("(Unavailable)");
                                    available_switch.setChecked(false);
                                    currentAvailability = false;

                                }else {
                                    String start_time = dataSnapshot.child("businessTime").child("Sunday").child("start").getValue().toString();
                                    String end_time = dataSnapshot.child("businessTime").child("Sunday").child("end").getValue().toString();
                                    if(Integer.parseInt(strDate.replaceAll(":","")) > Integer.parseInt(start_time.replaceAll(":","")) && Integer.parseInt(end_time.replaceAll(":","")) > Integer.parseInt(strDate.replaceAll(":",""))){
                                        if(mechanic.available){
                                            available_text.setText("(Available)");
                                            available_switch.setChecked(true);
                                        }else {
                                            available_text.setText("(Unavailable)");
                                            available_switch.setChecked(false);
                                        }
                                        currentAvailability = true;
                                    }else {
                                        available_text.setText("(Unavailable)");
                                        available_switch.setChecked(false);
                                        currentAvailability = false;
                                    }
                                }
                                break;
                            case Calendar.MONDAY:
                                if(dataSnapshot.child("businessTime").child("Monday").child("status").getValue().toString().equals("false")){
                                    available_text.setText("(Unavailable)");
                                    available_switch.setChecked(false);
                                    currentAvailability = false;
                                }else {
                                    String start_time = dataSnapshot.child("businessTime").child("Sunday").child("start").getValue().toString();
                                    String end_time = dataSnapshot.child("businessTime").child("Sunday").child("end").getValue().toString();
                                    if(Integer.parseInt(strDate.replaceAll(":","")) > Integer.parseInt(start_time.replaceAll(":","")) && Integer.parseInt(end_time.replaceAll(":","")) > Integer.parseInt(strDate.replaceAll(":",""))){
                                        if(mechanic.available){
                                            available_text.setText("(Available)");
                                            available_switch.setChecked(true);
                                        }else {
                                            available_text.setText("(Unavailable)");
                                            available_switch.setChecked(false);
                                        }
                                        currentAvailability = true;
                                    }else {
                                        available_text.setText("(Unavailable)");
                                        available_switch.setChecked(false);
                                        currentAvailability = false;
                                    }
                                }
                                break;
                            case Calendar.TUESDAY:
                                if(dataSnapshot.child("businessTime").child("Tuesday").child("status").getValue().toString().equals("false")){
                                    available_text.setText("(Unavailable)");
                                    available_switch.setChecked(false);;
                                    currentAvailability = false;
                                }else {
                                    String start_time = dataSnapshot.child("businessTime").child("Sunday").child("start").getValue().toString();
                                    String end_time = dataSnapshot.child("businessTime").child("Sunday").child("end").getValue().toString();
                                    if(Integer.parseInt(strDate.replaceAll(":","")) > Integer.parseInt(start_time.replaceAll(":","")) && Integer.parseInt(end_time.replaceAll(":","")) > Integer.parseInt(strDate.replaceAll(":",""))){
                                        if(mechanic.available){
                                            available_text.setText("(Available)");
                                            available_switch.setChecked(true);
                                        }else {
                                            available_text.setText("(Unavailable)");
                                            available_switch.setChecked(false);
                                        }
                                        currentAvailability = true;
                                    }else {
                                        available_text.setText("(Unavailable)");
                                        available_switch.setChecked(false);
                                        currentAvailability = false;
                                    }
                                }
                                break;
                            case Calendar.WEDNESDAY:
                                if(dataSnapshot.child("businessTime").child("Wednesday").child("status").getValue().toString().equals("false")){
                                    available_text.setText("(Unavailable)");
                                    available_switch.setChecked(false);
                                    currentAvailability = false;

                                }else {
                                    String start_time = dataSnapshot.child("businessTime").child("Sunday").child("start").getValue().toString();
                                    String end_time = dataSnapshot.child("businessTime").child("Sunday").child("end").getValue().toString();
                                    if(Integer.parseInt(strDate.replaceAll(":","")) > Integer.parseInt(start_time.replaceAll(":","")) && Integer.parseInt(end_time.replaceAll(":","")) > Integer.parseInt(strDate.replaceAll(":",""))){
                                        if(mechanic.available){
                                            available_text.setText("(Available)");
                                            available_switch.setChecked(true);
                                        }else {
                                            available_text.setText("(Unavailable)");
                                            available_switch.setChecked(false);
                                        }
                                        currentAvailability = true;
                                    }else {
                                        available_text.setText("(Unavailable)");
                                        available_switch.setChecked(false);
                                        currentAvailability = false;
                                    }
                                }
                                break;
                            case Calendar.THURSDAY:
                                if(dataSnapshot.child("businessTime").child("Thursday").child("status").getValue().toString().equals("false")){
                                    available_text.setText("(Unavailable)");
                                    available_switch.setChecked(false);
                                    currentAvailability = false;
                                }else {
                                    String start_time = dataSnapshot.child("businessTime").child("Sunday").child("start").getValue().toString();
                                    String end_time = dataSnapshot.child("businessTime").child("Sunday").child("end").getValue().toString();
                                    if(Integer.parseInt(strDate.replaceAll(":","")) > Integer.parseInt(start_time.replaceAll(":","")) && Integer.parseInt(end_time.replaceAll(":","")) > Integer.parseInt(strDate.replaceAll(":",""))){
                                        if(mechanic.available){
                                            available_text.setText("(Available)");
                                            available_switch.setChecked(true);
                                        }else {
                                            available_text.setText("(Unavailable)");
                                            available_switch.setChecked(false);
                                        }
                                        currentAvailability = true;
                                    }else {
                                        available_text.setText("(Unavailable)");
                                        available_switch.setChecked(false);
                                        currentAvailability = false;
                                    }
                                }
                                break;
                            case Calendar.FRIDAY:
                                if(dataSnapshot.child("businessTime").child("Friday").child("status").getValue().toString().equals("false")){
                                    available_text.setText("(Unavailable)");
                                    available_switch.setChecked(false);
                                    currentAvailability = false;
                                }else {
                                    String start_time = dataSnapshot.child("businessTime").child("Sunday").child("start").getValue().toString();
                                    String end_time = dataSnapshot.child("businessTime").child("Sunday").child("end").getValue().toString();
                                    if(Integer.parseInt(strDate.replaceAll(":","")) > Integer.parseInt(start_time.replaceAll(":","")) && Integer.parseInt(end_time.replaceAll(":","")) > Integer.parseInt(strDate.replaceAll(":",""))){
                                        if(mechanic.available){
                                            available_text.setText("(Available)");
                                            available_switch.setChecked(true);
                                        }else {
                                            available_text.setText("(Unavailable)");
                                            available_switch.setChecked(false);
                                        }
                                        currentAvailability = true;
                                    }else {
                                        available_text.setText("(Unavailable)");
                                        available_switch.setChecked(false);
                                        currentAvailability = false;
                                    }
                                }
                                break;
                            case Calendar.SATURDAY:
                                if(dataSnapshot.child("businessTime").child("Saturday").child("status").getValue().toString().equals("false")){
                                    available_text.setText("(Unavailable)");
                                    available_switch.setChecked(false);
                                    currentAvailability = false;
                                }else {
                                    String start_time = dataSnapshot.child("businessTime").child("Sunday").child("start").getValue().toString();
                                    String end_time = dataSnapshot.child("businessTime").child("Sunday").child("end").getValue().toString();
                                    if(Integer.parseInt(strDate.replaceAll(":","")) > Integer.parseInt(start_time.replaceAll(":","")) && Integer.parseInt(end_time.replaceAll(":","")) > Integer.parseInt(strDate.replaceAll(":",""))){
                                        if(mechanic.available){
                                            available_text.setText("(Available)");
                                            available_switch.setChecked(true);
                                        }else {
                                            available_text.setText("(Unavailable)");
                                            available_switch.setChecked(false);
                                        }
                                        currentAvailability = true;
                                    }else {
                                        available_text.setText("(Unavailable)");
                                        available_switch.setChecked(false);
                                        currentAvailability = false;
                                    }
                                }
                                break;
                        }
                    }else {
                        available_text.setText("(Unavailable)");
                        available_switch.setChecked(false);
                        currentAvailability = false;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getRateList(){
        FirebaseDatabase.getInstance().getReference().child(RateReview.TABLE_NAME).child(myId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    rateList.clear();
                    for (DataSnapshot rate : dataSnapshot.getChildren()){
                        RateReview rt = rate.getValue(RateReview.class);
                        rateList.add(rt);
                    }
                    ratingRecyclerAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.mechanic_profile_edit:
                Intent mechanic_edit_profile = new Intent(getActivity(), MechanicEditProfileActivity.class);
                startActivity(mechanic_edit_profile);
                break;
            case R.id.mechanic_profile_name_edit:
                showDetailDlg("name");
                break;
            case R.id.mechanic_profile_shop_edit:
                showDetailDlg("shop_name");
                break;
            case R.id.mechanic_profile_brief_edit:
                showDetailDlg("description");
                break;
            default:
                break;
        }
    }

    @Override
    public void clickRateItem(int index, String userId) {

    }

    @Override
    public void clickReplyRating(int index, String userId) {
        Intent reply_intent = new Intent(getActivity(), MechanicReplyReviewActivity.class);
        startActivity(reply_intent);
    }

    public void showDetailDlg(String target) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dlg_edit_field);
        dialog.show();

        TextView title = (TextView)dialog.findViewById(R.id.dlg_one_field_edit_title);
        final EditText content = (EditText)dialog.findViewById(R.id.dlg_one_field_edit);
        RelativeLayout btnSubmit = (RelativeLayout)dialog.findViewById(R.id.dlg_one_field_submit);

        if(target.equals("name")){
            title.setText("Name");
            content.setText(mechanic.fullName);
            btnSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!content.getText().toString().isEmpty()){
                        FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_USER).child(myId).child("fullName").setValue(content.getText().toString());
                        dialog.dismiss();
                    }
                }
            });
        }else if(target.equals("shop_name")){
            title.setText("Shop Name:");
            content.setText(mechanic.shopName);
            btnSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!content.getText().toString().isEmpty()){
                        FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_USER).child(myId).child("shopName").setValue(content.getText().toString());
                        dialog.dismiss();
                    }
                }
            });

        }else if(target.equals("description")){
            title.setText("Brief:");
            content.setText(mechanic.description);
            btnSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!content.getText().toString().isEmpty()){
                        FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_USER).child(myId).child("description").setValue(content.getText().toString());
                        dialog.dismiss();
                    }
                }
            });
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(mechanic.shopID.isEmpty() || currentAvailability){
            if(isChecked){
                available_text.setText("(Available)");
                setAvailableValue(isChecked);
            }else {
                available_text.setText("(Unavailable)");
                setAvailableValue(isChecked);
            }
        }else {
            AlertFactory.showAlert(getActivity(),"","Your shop closed this time.");
            available_switch.setChecked(false);
        }
    }

    public void setAvailableValue(Boolean isChecked){
        mechanic.available = isChecked;
        FirebaseDatabase.getInstance().getReference().child(DBInfo.TBL_USER).child(myId).child("available").setValue(isChecked);
        FirebaseManager.getInstance().setMechanic(mechanic);

    }

    @Override
    public void onResume() {
        super.onResume();
        getInfo();
    }
}
