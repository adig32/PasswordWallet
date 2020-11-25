package com.example.passwordwallet.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.passwordwallet.HomeActivity;
import com.example.passwordwallet.LoginActivity;
import com.example.passwordwallet.Models.MyPasswordModel;
import com.example.passwordwallet.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class MyPasswordAdapter extends RecyclerView.Adapter<MyPasswordAdapter.MyPasswordViewHolder> {

    private Context mCtx;
    private List<MyPasswordModel> myPasswordList;
    private OnItemClickListener mListener;
    String pepper = "XBvQfSVBuPInwt4dwQgB";
    String secretKey;

    public MyPasswordAdapter(Context mCtx, List<MyPasswordModel> myPasswordList, String secretKey) {
        this.mCtx = mCtx;
        this.myPasswordList = myPasswordList;
        this.secretKey = secretKey;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    @Override
    public MyPasswordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.my_password_layout, null);
        return new MyPasswordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyPasswordViewHolder holder, int position) {
        MyPasswordModel myOfferModel = myPasswordList.get(position);

        holder.textViewWebsite.setText(myOfferModel.getWebAddress());
        holder.textViewLogin.setText(myOfferModel.getLogin());
        holder.textViewDescription.setText(myOfferModel.getDescription());
        holder.textViewPassword.setText(myOfferModel.getPassword());
        holder.textViewId.setText(myOfferModel.getId());

    }

    @Override
    public int getItemCount() {
        return myPasswordList.size();
    }

    class MyPasswordViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewWebsite, textViewLogin, textViewDescription, textViewPassword, textViewId;

        public MyPasswordViewHolder(View itemView) {
            super(itemView);

            textViewId = itemView.findViewById(R.id.password_id);
            textViewWebsite = itemView.findViewById(R.id.password_website);
            textViewLogin = itemView.findViewById(R.id.password_login);
            textViewDescription = itemView.findViewById(R.id.password_description);
            textViewPassword = itemView.findViewById(R.id.password_password);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String decryptedPassword = decrypt(textViewPassword.getText().toString(), secretKey);
                    textViewPassword.setText(decryptedPassword);
                }
            });
        }
    }

    public String decrypt(String strToDecrypt, String secret) {
        try
        {
            byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
            IvParameterSpec ivspec = new IvParameterSpec(iv);

            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(secret.toCharArray(), pepper.getBytes(), 65536, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivspec);
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        }
        catch (Exception e) {
            System.out.println("Error while decrypting: " + e.toString());
        }
        return null;
    }
}
