package megvii.testfacepass.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import megvii.testfacepass.R;

/**
 * Created by xingchaolei on 2017/12/5.
 */

public class GroupNameAdapter extends BaseAdapter {

    private List<String> mGroupNames;
    private LayoutInflater mLayoutInflater;
    private ItemDeleteButtonClickListener mItemDeleteButtonClickListener;

    public GroupNameAdapter() {
        super();
    }

    public List<String> getData() {
        return mGroupNames;
    }

    public void setData(List<String> data) {
        mGroupNames = data;
    }

    public void setOnItemDeleteButtonClickListener(ItemDeleteButtonClickListener listener) {
        mItemDeleteButtonClickListener = listener;
    }

    @Override
    public int getCount() {
        return mGroupNames == null ? 0 : mGroupNames.size();
    }

    @Override
    public Object getItem(int position) {
        return mGroupNames == null ? null : mGroupNames.get(position);
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
        ViewHolder holder;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.layout_item_group_name, parent, false);
            holder = new ViewHolder();
            holder.groupNameTv = (TextView) convertView.findViewById(R.id.tv_group_name);
            holder.deleteGroupIv = (ImageView) convertView.findViewById(R.id.iv_delete_group);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.deleteGroupIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemDeleteButtonClickListener != null) {
                    mItemDeleteButtonClickListener.OnItemDeleteButtonClickListener(position);
                }
            }
        });
        holder.groupNameTv.setText(mGroupNames.get(position));
        return convertView;
    }


    public static class ViewHolder {
        TextView groupNameTv;
        ImageView deleteGroupIv;
    }


    public interface ItemDeleteButtonClickListener {

        void OnItemDeleteButtonClickListener(int position);

    }
}
