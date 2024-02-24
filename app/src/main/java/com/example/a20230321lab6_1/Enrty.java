package com.example.a20230321lab6_1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class Enrty extends AppCompatActivity {
    private  int[] images={R.drawable.jay1,R.drawable.jay2,R.drawable.jay3,R.drawable.jay4,R.drawable.jay5};
    private String[] names={"稻香","花海","兰亭序","青花瓷","晴天"};
//    private int[] infos={R.string.song1,R.string.song2,R.string.song3,R.string.song4,R.string.song5};
    private RecyclerView mrv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enrty);
        mrv=findViewById(R.id.rv);
        mrv.setLayoutManager(new LinearLayoutManager(Enrty.this,RecyclerView.VERTICAL,false));
        mrv.addItemDecoration(new decoration());
        mrv.setAdapter(new MyAdapter());
    }
    private class MyAdapter extends RecyclerView.Adapter<MyHolder> {
        @NonNull
        @Override
        public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view=View.inflate(getApplicationContext(),R.layout.piece,null);
            MyHolder myHolder= new MyHolder(view);

            return myHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull MyHolder holder, int position) {
            holder.miv_image.setBackgroundResource(images[position]);
            holder.mtv_name.setText(names[position]);
//            holder.mtv_info.setText(infos[position]);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent mintent = null;
                    mintent = new Intent(Enrty.this, MainActivity.class);
                    switch (position) {
                        case 0: {
                            MainActivity.changeCurrentMusic(1);
                            break;
                        }
                        case 1: {
                            MainActivity.changeCurrentMusic(2);
                            break;
                        }
                        case 2: {
                            MainActivity.changeCurrentMusic(3);
                            break;
                        }
                        case 3: {
                            MainActivity.changeCurrentMusic(4);
                            break;
                        }
                        case 4: {
                            MainActivity.changeCurrentMusic(5);
                            break;
                        }
                    }
                    startActivity(mintent);
                }
            });

        }


        @Override
        public int getItemCount() {
            return images.length;
        }
    }

    private class MyHolder extends RecyclerView.ViewHolder {
        ImageView miv_image;
        TextView mtv_name,mtv_info;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            miv_image=itemView.findViewById(R.id.iv_image);
            mtv_name = itemView.findViewById(R.id.tv_name);
//            mtv_info = itemView.findViewById(R.id.tv_information);
        }
    }

    private class decoration extends RecyclerView.ItemDecoration {
        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.set(0,0,0,20);
        }
    }




}