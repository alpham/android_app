package com.example.uploadlistener.tasks;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

import com.example.uploadlistener.DisplayActivity;
import com.example.uploadlistener.MainActivity;
import com.example.uploadlistener.services.CustomSocketIOService;

public class LoginTask extends AsyncTask<String, Integer, Bundle> {

	private Connection dbConnection;
	private ResultSet mResult;

	private String username;
	private String password;
	private MainActivity mainActivity;
	
	private ProgressDialog loadingDialog;

	public final static String DB_HOST = "www.db4free.net";
	public final static String DB_USERNAME = "doctor2014";
	public final static String DB_PASSWORD = "rootroot";
	public final static String DB_NAME = "doctor2014";

	private final static String TAG = "LoginTask";

	public LoginTask(String username, String password, MainActivity activty) {
		this.mainActivity = activty;
		this.username = username;
		this.password = password;

	}

	@Override
	protected Bundle doInBackground(String... params) {
		Bundle user = new Bundle();
		try {
			Class.forName("com.mysql.jdbc.Driver");
			this.dbConnection = DriverManager.getConnection("jdbc:mysql://"
					+ DB_HOST + "/" + DB_NAME + "?" + "user=" + DB_USERNAME
					+ "&password=" + DB_PASSWORD);
			Statement stm;
			stm = dbConnection.createStatement();
			mResult = stm.executeQuery("SELECT * FROM `dr` WHERE `dr_name`=\""
					+ this.username + "\" AND `password`=\"" + this.password
					+ "\"");
			
			Log.i(TAG, "connected to the database");
			if (mResult.next()) {
				user.putInt("id",mResult.getInt("dr_id"));
				user.putString("dr_name",mResult.getString("dr_name"));
				user.putString("dr_specialization",mResult.getString("dr_specialization"));
				user.putString("password",mResult.getString("password"));
			}
			return user;

		} catch (ClassNotFoundException e) {
			Log.e(TAG, "class not found ");
			e.printStackTrace();
		} catch (SQLException e) {
			Log.e(TAG, "cannot connect to the database ");
			e.printStackTrace();
		}

		return null;

	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		
		loadingDialog = ProgressDialog.show(this.mainActivity, "Loading...",
			    "Please, wait...", true);
		
	}

	@Override
	protected void onPostExecute(Bundle result) {
		super.onPostExecute(result);
		
		loadingDialog.dismiss();
		
		if(result != null){
			int dr_id = result.getInt("id");
			CustomSocketIOService.id = dr_id;
			
			if (!CustomSocketIOService.isAlive) {
				mainActivity.startService(new Intent(mainActivity,
						CustomSocketIOService.class));
			}
			
			mainActivity.startActivity(new Intent(mainActivity,DisplayActivity.class));
			mainActivity.finish();
			
		} else {
			this.mainActivity.tryAgain();
		}
	}
	
	
	

}
