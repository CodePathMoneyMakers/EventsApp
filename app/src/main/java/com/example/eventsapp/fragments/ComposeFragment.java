package com.example.eventsapp.fragments;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.eventsapp.R;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.example.eventsapp.MainActivity;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;
import java.util.TimeZone;

public class ComposeFragment extends Fragment  {

    public int counter;
    public static final String TAG = "ComposeFragment";
    private TextView tvDate;
    private ImageButton calendar_btn;
    private ImageButton time_btn;
    private TextView tvTime;
    private TextView tvTime1;
    private TextView till;
    private TextView from;
    private ImageButton music_btn;
    private ImageButton fee_btn;
    private TextView tvMusic;
    private TextView tvFee;
    private ImageView invisible;
    private Switch aSwitch;
    private ImageButton back_btn;

    int t1Hour, t1Minute, t2Hour, t2Minute;

    public ComposeFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragments_compose, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvDate = view.findViewById(R.id.tvDate);
        calendar_btn = view.findViewById(R.id.calender_btn);
        time_btn  = view.findViewById(R.id.time_btn);
        tvTime = view.findViewById(R.id.tvTime);
        tvTime1 = view.findViewById(R.id.tvTime1);
        till = view.findViewById(R.id.till);
        from = view.findViewById(R.id.from);
        music_btn = view.findViewById(R.id.music_btn);
        fee_btn = view.findViewById(R.id.fee_btn);
        tvMusic = view.findViewById(R.id.tvMusic);
        tvFee = view.findViewById(R.id.tvFee);
        invisible = view.findViewById(R.id.invisible);
        aSwitch = view.findViewById(R.id.switch1);

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.clear();

        CalendarConstraints.Builder constraintBuilder = new CalendarConstraints.Builder();
        constraintBuilder.setValidator(DateValidatorPointForward.now());

        MaterialDatePicker.Builder builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText("Select a Date");
        builder.setCalendarConstraints(constraintBuilder.build());
        MaterialDatePicker materialDatePicker = builder.build();

        long today = materialDatePicker.todayInUtcMilliseconds();
        builder.setSelection(today);

        calendar_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity main = new MainActivity();
                materialDatePicker.show(getFragmentManager(), "DatePicker");
            }
        });


        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Object selection) {

                Date date = new Date(materialDatePicker.getHeaderText());
                String dayOfTheWeek = (String) android.text.format.DateFormat.format("EEEE", date);

                String month = "";
                String day = "";
                StringTokenizer stringTokenizer = new StringTokenizer(materialDatePicker.getHeaderText());

                while(stringTokenizer.countTokens() != 1){
                     month = stringTokenizer.nextToken();
                     day = stringTokenizer.nextToken();
                }
                tvDate.setText(dayOfTheWeek + " " + day + " " + month);

            }
        });

        from.setText("");
        till.setText("");
        time_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                counter++;

                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//                        Calendar calendar1 = Calendar.getInstance();
//                        calendar1.set(Calendar.HOUR_OF_DAY, hourOfDay);
//                        calendar1.set(Calendar.MINUTE, minute);
//                        calendar1.setTimeZone(TimeZone.getDefault());
//                        SimpleDateFormat format = new SimpleDateFormat("k:mm a");
//                        String time = format.format(calendar1.getTime());

                        t1Hour = hourOfDay;
                        t1Minute = minute;

                        Calendar c = Calendar.getInstance();
                        c.set(0, 0, 0, t1Hour, t1Minute);

                            if(counter % 2 == 0){
                                tvTime1.setText(DateFormat.format("hh:mm aa", c));
                                till.setText("till");
                            }else{
                                tvTime.setText(DateFormat.format("hh:mm aa", c));
                                from.setText("from");
                            }

                    }
                }, 12, 0, false);
                timePickerDialog.updateTime(t1Hour, t1Minute);
                timePickerDialog.show();
            }
        });

        music_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Dialog dialog = new Dialog(getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.music_dialog);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                EditText etMusicType = dialog.findViewById(R.id.etMusicType);
                Button save_btn = dialog.findViewById(R.id.save_btn);

                save_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String str = etMusicType.getText().toString();
                        tvMusic.setText(str);
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        fee_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Dialog dialog = new Dialog(getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.fee_dialog);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                EditText etFee = dialog.findViewById(R.id.etFee);
                Button save_btn = dialog.findViewById(R.id.save_btn);

                save_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String str = etFee.getText().toString();
                        tvFee.setText(str);
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    invisible.setImageDrawable(getContext().getDrawable(R.drawable.ic_invisible));
                }else{

                    invisible.setImageDrawable(getContext().getDrawable(R.drawable.ic_visibility));
                }
            }
        });



    }

    public String getCallerFragment(){
        FragmentManager fm = getFragmentManager();
        int count = getFragmentManager().getBackStackEntryCount();
        return fm.getBackStackEntryAt(count - 2).getName();
    }

}
