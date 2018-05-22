package com.wireless.ambeent.mozillaprototype.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.wireless.ambeent.mozillaprototype.R;
import com.wireless.ambeent.mozillaprototype.helpers.Constants;
import com.wireless.ambeent.mozillaprototype.pojos.MessageObject;

import org.w3c.dom.Text;

import java.util.List;

public class ChatAdapter  extends RecyclerView.Adapter<ChatAdapter.MyViewHolder>{

    private static final String TAG = "ChatAdapter";

    private Context mContext;
    private List<MessageObject> mMessageObjectList;


    public ChatAdapter(Context mContext, List<MessageObject> mMessageObjectList) {
        this.mContext = mContext;
        this.mMessageObjectList = mMessageObjectList;


    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Creates the views from layout xml.
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.row_chat, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        MessageObject messageObject = mMessageObjectList.get(position);
        String senderName = messageObject.getSender();
        String message = messageObject.getMessage();

        String receiver = messageObject.getReceiver();

        if(Constants.PHONE_NUMBER.equalsIgnoreCase(receiver)){
            holder.containerCardView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.colorLightGray));

        }else{
            holder.containerCardView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.colorWhite));

        }

        //Show the messages and their senders.
        holder.senderNameTextView.setText(senderName);
        holder.senderMessageTextView.setText(message);

    }

    // Return the size of the dataset
    @Override
    public int getItemCount() {
        return mMessageObjectList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {


        public TextView senderNameTextView, senderMessageTextView;

        public CardView containerCardView;


        public MyViewHolder(View view) {
            super(view);

            senderNameTextView = (TextView) view.findViewById(R.id.textView_SenderName);
            senderMessageTextView = (TextView) view.findViewById(R.id.textView_SenderMessage);

            containerCardView = (CardView) view.findViewById(R.id.cardView_MsgContainer);
        }



    }

}
