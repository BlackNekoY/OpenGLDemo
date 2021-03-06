package com.example.slimxu.opengldemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.slimxu.opengldemo.camera.CameraActivity;
import com.example.slimxu.opengldemo.coordinate.CoordinateActivity;
import com.example.slimxu.opengldemo.egl_offscreen.EGLOffScreenActivity;
import com.example.slimxu.opengldemo.egl_window.EGLWindowActivity;
import com.example.slimxu.opengldemo.light.LightActivity;
import com.example.slimxu.opengldemo.light_direction.LightDirectionActivity;
import com.example.slimxu.opengldemo.light_material.LightMaterialActivity;
import com.example.slimxu.opengldemo.light_multiply.LightMultiplyActivity;
import com.example.slimxu.opengldemo.light_spot.LightSpotActivity;
import com.example.slimxu.opengldemo.light_texture.LightTextureActivity;
import com.example.slimxu.opengldemo.sensor_test.SensorActivity;
import com.example.slimxu.opengldemo.texture.TextureActivity;
import com.example.slimxu.opengldemo.vao.VAOActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView mDemoListView;
    private DemoAdapter mAdapter;
    private List<Case> mCaseList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCaseList = new ArrayList<Case>(){{
            add(new Case().setTitle("VAO & VBO画三角形")
            .setTargetClass(VAOActivity.class));
            add(new Case().setTitle("Texture")
            .setTargetClass(TextureActivity.class));
            add(new Case().setTitle("坐标系")
            .setTargetClass(CoordinateActivity.class));
            add(new Case().setTitle("摄像机")
            .setTargetClass(CameraActivity.class));
            add(new Case().setTitle("光照")
            .setTargetClass(LightActivity.class));
            add(new Case().setTitle("光照材质")
            .setTargetClass(LightMaterialActivity.class));
            add(new Case().setTitle("光照贴图 & 点光源（衰减）")
            .setTargetClass(LightTextureActivity.class));
            add(new Case().setTitle("定向光")
            .setTargetClass(LightDirectionActivity.class));
            add(new Case().setTitle("聚光（手电筒）")
            .setTargetClass(LightSpotActivity.class));
            add(new Case().setTitle("多光源")
            .setTargetClass(LightMultiplyActivity.class));
            add(new Case().setTitle("传感器")
                    .setTargetClass(SensorActivity.class));
            add(new Case().setTitle("SurfaceView + EGL 上屏渲染")
                    .setTargetClass(EGLWindowActivity.class));
            add(new Case().setTitle("SurfaceView + EGL 离屏渲染")
                    .setTargetClass(EGLOffScreenActivity.class));
        }};

        mAdapter = new DemoAdapter(mCaseList);
        mDemoListView = (ListView) findViewById(R.id.demo_list);
        mDemoListView.setAdapter(mAdapter);
        mDemoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Case item = mCaseList.get(position);
                Intent intent = new Intent(MainActivity.this, item.targetClass);
                startActivity(intent);
            }
        });
    }

    private class DemoAdapter extends BaseAdapter {

        private final List<Case> mDataList;

        public DemoAdapter(List<Case> list) {
            mDataList = list;
        }

        @Override
        public int getCount() {
            return mDataList.size();
        }

        @Override
        public Case getItem(int position) {
            return mDataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if(convertView == null || convertView.getTag() == null){
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_case_item,parent,false);
                holder = new ViewHolder();
                holder.item = convertView;
                holder.title = convertView.findViewById(R.id.item_title);
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }

            Case item = getItem(position);
            holder.title.setText(item.title);

            return convertView;
        }
    }

    private class ViewHolder{
        View item;
        TextView title;
    }


    private class Case{
        private String title;
        private Class targetClass;


        public Case setTitle(String title) {
            this.title = title;
            return this;
        }

        public Case setTargetClass(Class target) {
            this.targetClass = target;
            return this;
        }

    }
}
