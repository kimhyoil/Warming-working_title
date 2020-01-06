package com.example.warmer.ui.calender;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.warmer.R;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;

public class ExpandableAdapter extends RecyclerView.Adapter<ExpandableAdapter.ItemViewHolder> {

    private ArrayList<Data> listData = new ArrayList<>();                  // adapter에 들어갈 list 입니다.
    private Context context;
    private SparseBooleanArray selectedItems = new SparseBooleanArray();   // Item의 클릭 상태를 저장할 array 객체
    private int prePosition = -1;                                          // 직전에 클릭됐던 Item의 position

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // LayoutInflater를 이용하여 전 단계에서 만들었던 item.xml을 inflate 시킵니다.
        // return 인자는 ViewHolder 입니다.
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        // Item을 하나, 하나 보여주는(bind 되는) 함수입니다.
        holder.onBind(listData.get(position), position);
    }

    @Override
    public int getItemCount() {
        // RecyclerView의 총 개수 입니다.
        return listData.size();
    }

    void addItem(Data data) {
        // 외부에서 item을 추가시킬 함수입니다.
        listData.add(data);
    }

    // RecyclerView의 핵심인 ViewHolder 입니다.
    // 여기서 subView를 setting 해줍니다.
    class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView textView1;
        private ImageView imageView1;
        private ArrayList<Chip> chipList;
        private Data data;
        private int position;

        // item을 처음 생성 할 때만 실행
        ItemViewHolder(View itemView) {
            super(itemView);
            // item.xml 참조
            textView1 = itemView.findViewById(R.id.textView1);
            imageView1 = itemView.findViewById(R.id.imageView1);
            chipList = new ArrayList<>();

            // *************** DB **************** //
            // textView1(감정종류)에 따라서 chip(emotion)변경
            FlexboxLayout flexboxLayout = itemView.findViewById(R.id.itemFlexBox);

            for (int i = 0; i < 3; i++ ) {
                final Chip chip = new Chip(context);
                chip.setText("new" + i);
                chip.setOnClickListener(new Chip.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("chip ", chip.getText()+"");
//                        chip.setChipBackgroundColorResource(R.color.colorPrimary);
                    }
                });
                chipList.add(chip);
                flexboxLayout.addView(chip);
            }
            Log.d("chip len",  chipList.size() + " ");
        }

        // item.xml 값 세팅
        void onBind(Data data, int position) {
            this.data = data;
            this.position = position;

            // *************** DB **************** //
            // textView1(감정), imageView(대표 이미지) 세팅
            textView1.setText(data.getTitle());
            imageView1.setImageResource(data.getResId());
            changeVisibility(selectedItems.get(position));

            itemView.setOnClickListener(this);
//            textView1.setOnClickListener(this);
//            imageView1.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.linearItem:
                    Log.d("item", "~~~~~~~~~~~~");
                    // 펼쳐진 Item을 클릭 시
                    if (selectedItems.get(position)) {
                        selectedItems.delete(position);
                    } else {
                        // 직전의 클릭됐던 Item의 클릭상태를 지움
//                        selectedItems.delete(prePosition);
                        // 클릭한 Item의 position을 저장
                        selectedItems.put(position, true);
                    }
                    if (prePosition != -1) notifyItemChanged(prePosition);  // 해당 포지션의 변화를 알림
                    notifyItemChanged(position);
                    prePosition = position;                                 // 클릭된 position 저장
                    break;

            }
        }

        // animation & visible/gone
        private void changeVisibility(final boolean isExpanded) {
            // height 값을 dp로 지정해서 넣고싶으면 아래 소스를 이용
            int dpValue = 150;
            float d = context.getResources().getDisplayMetrics().density;
            int height = (int) (dpValue * d);

            // ValueAnimator.ofInt(int... values)는 View가 변할 값을 지정, 인자는 int 배열
            ValueAnimator va = isExpanded ? ValueAnimator.ofInt(0, height) : ValueAnimator.ofInt(height, 0);
            // Animation이 실행되는 시간, n/1000초
            va.setDuration(600);
            va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    for (Chip chip : chipList) {
                        chip.requestLayout();
                        chip.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
                    }
                }
            });
            // Animation start
            va.start();
        }
    }
}