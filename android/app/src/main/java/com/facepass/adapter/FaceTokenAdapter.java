package megvii.testfacepass.adapter;

import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import megvii.testfacepass.R;

/**
 * Created by xingchaolei on 2017/12/5.
 */

public class FaceTokenAdapter extends BaseAdapter {

    private List<String> mFaceTokens;
    private LayoutInflater mLayoutInflater;
    private ItemButtonClickListener mItemButtonClickListener;

    public FaceTokenAdapter() {
        super();
    }

    public List<String> getData() {
        return mFaceTokens;
    }

    public void setData(List<String> data) {
        mFaceTokens = data;
    }

    public void setOnItemButtonClickListener(ItemButtonClickListener listener) {
        mItemButtonClickListener = listener;
    }

    @Override
    public int getCount() {
        return mFaceTokens == null ? 0 : mFaceTokens.size();
    }

    @Override
    public Object getItem(int position) {
        return mFaceTokens == null ? null : mFaceTokens.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (mLayoutInflater == null) {
            mLayoutInflater = LayoutInflater.from(parent.getContext());
        }
        final ViewHolder holder;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.layout_item_face_token, parent, false);
            holder = new ViewHolder();
            holder.faceTokenNameTv = (TextView) convertView.findViewById(R.id.tv_face_token);
            holder.deleteFaceTokenIv = (ImageView) convertView.findViewById(R.id.iv_delete_face);
            holder.unbindGroupTv = (TextView) convertView.findViewById(R.id.tv_face_unbind);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.deleteFaceTokenIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mItemButtonClickListener != null) {
                    mItemButtonClickListener.onItemDeleteButtonClickListener(position);
                }

            }
        });

        holder.unbindGroupTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mItemButtonClickListener != null) {
                    mItemButtonClickListener.onItemUnbindButtonClickListener(position);
                }
            }
        });

        holder.faceTokenNameTv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipboardManager cmb = (ClipboardManager) v.getContext().getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
                cmb.setText(holder.faceTokenNameTv.getText());
                Toast.makeText(v.getContext(), "FaceToke已复制", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        holder.faceTokenNameTv.setText(mFaceTokens.get(position));
        return convertView;
    }


    public static class ViewHolder {
        TextView faceTokenNameTv;
        ImageView deleteFaceTokenIv;
        TextView unbindGroupTv;
    }


    public interface ItemButtonClickListener {

        void onItemDeleteButtonClickListener(int position);

        void onItemUnbindButtonClickListener(int position);

    }
}
