package com.danertu.dianping;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore.Images.Media;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;

import com.danertu.tools.AppManager;
import com.danertu.tools.MyDialog;
import com.danertu.widget.CommonTools;
import com.danertu.widget.MyBaseAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;

public class PhotoActivity extends BaseActivity implements OnClickListener {
    private GridView gv_photos;
    private Button b_title;
    private int img_path = 0;
    private ImageLoader imageLoader;
    private ArrayList<HashMap<String, Object>> selecteds;
    public static final String KEY_SELECTED = "isSelected";
    public static final String KEY_PATH = "path";
    private int selectCount = 0;
    public static final String KEY_SUMCOUNT = "sumCount";
    private int sumCount = 9;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.photo);
        initIntentMsg();
        initContainer();
        findViewById();
        initView();
    }

    private void initIntentMsg() {
        sumCount = getIntent().getIntExtra(KEY_SUMCOUNT, 0);
    }

    @Override
    protected void findViewById() {
        gv_photos = (GridView) findViewById(R.id.gv_photos);
    }

    @Override
    protected void initView() {
        initTitle();
        imageLoader = ImageLoader.getInstance();
        MyBase ada = new MyBase(this, selecteds.size());
        gv_photos.setAdapter(ada);
        gv_photos.setOnItemClickListener(clickListener);
    }

    final String TITLE = "选择图片";
    final String TAG_SELECTED = "（已选";

    private void initTitle() {
        b_title = (Button) findViewById(R.id.b_title_back2);
        Button b_opera = (Button) findViewById(R.id.b_title_operation2);
        b_title.setText(TITLE + TAG_SELECTED + selectCount + "/" + sumCount + "）");
        b_opera.setText("完成");
        b_title.setOnClickListener(this);
        b_opera.setOnClickListener(this);
    }

    private void initContainer() {
        Cursor cursor = AppManager.getInstance().getPhotoMsg(this);
        img_path = cursor.getColumnIndex(Media.DATA);
        selectCount = 0;
        selecteds = new ArrayList<>();

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            HashMap<String, Object> item = new HashMap<>();
            item.put(KEY_SELECTED, false);
            String uri = /*"file://"+*/cursor.getString(img_path);
            item.put(KEY_PATH, uri);
            selecteds.add(item);
        }
    }

    public OnItemClickListener clickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            HashMap<String, Object> item = selecteds.get(position);
            boolean isCheck = (Boolean) item.get(KEY_SELECTED);
            ImageView iv = (ImageView) view.findViewById(R.id.iv_photo_item_tag);
            if (isCheck) {
                selectCount--;
                iv.setSelected(false);
            } else {
                selectCount++;
                if (selectCount > sumCount) {
                    selectCount = sumCount;
                    CommonTools.showShortToast(getContext(), "选中的图片已经达到上限！");
                    return;
                }
                iv.setSelected(true);
            }
            b_title.setText(TITLE + TAG_SELECTED + selectCount + "/" + sumCount + "£©");
            item.put(KEY_SELECTED, iv.isSelected());
        }
    };

    private class MyBase extends MyBaseAdapter {

        public MyBase(Context context, int count) {
            super(context, count);
        }

        class ViewHolder {
            ImageView iv;
            ImageView iv_tag;

            public ViewHolder(View v) {
                iv = (ImageView) v.findViewById(R.id.iv_photo_item);
                iv_tag = (ImageView) v.findViewById(R.id.iv_photo_item_tag);
            }
        }

        public View getView(int p, View v, ViewGroup vg) {
            ViewHolder vh;
            String path = (String) selecteds.get(p).get(KEY_PATH);
            path = "file://" + path;
            boolean isCheck = (Boolean) selecteds.get(p).get(KEY_SELECTED);
            if (v == null) {
                v = LayoutInflater.from(context).inflate(R.layout.photo_item, null);
                vh = new ViewHolder(v);
                v.setTag(vh);
            } else {
                vh = (ViewHolder) v.getTag();
            }
            vh.iv_tag.setSelected(isCheck);
            imageLoader.displayImage(path, vh.iv);
            return v;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.b_title_back2:
                if (selectCount > 0) {
                    askDialogClick();
                } else {
                    finish();
                }
                break;

            case R.id.b_title_operation2:
                int count = 0;
                ArrayList<String> img_paths = new ArrayList<>();
                for (HashMap<String, Object> item : selecteds) {
                    if (count == selectCount)
                        break;
                    boolean isSelected = (Boolean) item.get(KEY_SELECTED);
                    if (isSelected) {
                        count++;
                        img_paths.add(item.get(KEY_PATH).toString());
                    }
                }
                Intent intent = new Intent();
                intent.putExtra(KEY_PATH, img_paths);
                setResult(PShopAdd.REQ_SELECT_PHOTOS, intent);
                finish();
                break;
        }
    }

    public void askDialogClick() {
        final Dialog dialog = MyDialog.getDefineDialog(this, "提示", "不保存当前选择？");
        Button b_sure = (Button) dialog.findViewById(R.id.b_dialog_right);
        b_sure.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
                finish();
            }
        });
        Button b_cancel = (Button) dialog.findViewById(R.id.b_dialog_left);
        b_cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
