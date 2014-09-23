package com.example.uploadlistener.tasks;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import com.example.uploadlistener.DisplayActivity;
import com.example.uploadlistener.R;
import com.example.uploadlistener.services.CustomSocketIOService;

/**
 * FTPDownloader
 * 
 * @author ahmed.magdy40@gmail.com
 * 
 *         Download a *.zip file from given ftp server, unzip it. takes the
 *         server uri, and return the file path.
 * 
 */
public class FTPDownloader extends AsyncTask<String, Integer, String> {

	public final static String FTP_SERVER = "ftp.byethost31.com";
	public final static String FTP_USERNAME = "b6_15318699";
	public final static String FTP_PASSWORD = "ahmed123456789asd";

	public final static String TAG = "FTPDownloader";
	public static final String FILES_PATH = "htdocs";
	public static final String EXTRACTED_FILES_PATH = "extracted_files";

	private FTPClient ftpClient;
	private OutputStream outputStream;

	private Context mContext;
	private String mFileName;
	private byte[] buffer = new byte[1024];
	// private int mLenghtOfFile;
	private NotificationManager mNotificationManager;
	private NotificationCompat.Builder mNotificationBuilder;
	private Intent mIntent;

	public FTPDownloader(Context context, Bundle extras) {
		this.mContext = context;

		mNotificationManager = (NotificationManager) mContext
				.getSystemService(Context.NOTIFICATION_SERVICE);
		mIntent = new Intent(this.mContext, DisplayActivity.class);
		mIntent.putExtras(extras);

	}

	/**
	 * start downloading the file. One file per time.
	 * 
	 * @param Strnig
	 *            the parameters of the ftp server.
	 * @return String the path of the resulted folder form unzipping the
	 *         downloaded file
	 */
	@Override
	protected String doInBackground(String... params) {
		mFileName = ((String[]) params)[0];
		// TODO download the file, unzip it and return the path
		try {
			initFTPClient();
			downloadFTPFile();
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		// TODO change service-notification view and make it
		// "Starting downloading"
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		unzipFile();
		notifyUser();

		// TODO update displaying ListView
		// TODO reset service-notification view
	}

	private void notifyUser() {
		PendingIntent pi = PendingIntent.getActivity(mContext, 0, mIntent,
				PendingIntent.FLAG_CANCEL_CURRENT);

		String title = "[Application Title...]";
		String body = "You have " + CustomSocketIOService.numMessages
				+ " new case"
				+ (CustomSocketIOService.numMessages > 1 ? "s." : ".");

		mNotificationBuilder = new NotificationCompat.Builder(mContext)
				.setContentTitle(title).setContentText(body)
				.setSmallIcon(R.drawable.bell).setWhen((new Date()).getTime())
				.setAutoCancel(true).setContentIntent(pi).setTicker(body)
				.setNumber(CustomSocketIOService.numMessages++)
				.setDefaults(Notification.DEFAULT_ALL);

		mNotificationManager.notify(CustomSocketIOService.UNIQUEID,
				mNotificationBuilder.build());
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
		int value = ((Integer[]) values)[0];
		Log.i(TAG, "progress: " + value + "%"); // TODO update
												// service-notification view
												// with progress bar.
	}

	/**
	 * initiate the FTPClient to start downloading the file. (connect to the
	 * server and navigate to the files directory).
	 * 
	 * @throws SocketException
	 * @throws IOException
	 */

	private void initFTPClient() throws SocketException, IOException {
		ftpClient = new FTPClient();
		ftpClient.connect(FTP_SERVER);
		Log.i(TAG, "connected");

		ftpClient.enterLocalPassiveMode();
		if (!ftpClient.login(FTP_USERNAME, FTP_PASSWORD)) {
			ftpClient.logout();
			Log.e(TAG, "Error on connecting the FTP server.");
		}

		int reply = ftpClient.getReplyCode();
		if (!FTPReply.isPositiveCompletion(reply)) {
			ftpClient.disconnect();
			Log.e(TAG, "server replay error!!");
		}
		ftpClient.changeWorkingDirectory(FILES_PATH);

		String s = ftpClient.printWorkingDirectory();
		Log.i(TAG, "the current working dirictory is : " + s);

	}

	/**
	 * Start downloading the file from the remote FTP server
	 * 
	 */
	private void downloadFTPFile() {
		try {
			outputStream = new FileOutputStream(new File(
					mContext.getCacheDir(), mFileName));
			Log.i(TAG, "downloading....");
			// ftpClient.get
			ftpClient.retrieveFile(mFileName, outputStream);
			Log.i(TAG, "downloaded");
			outputStream.close();
		} catch (FileNotFoundException e) {
			Log.i(TAG, "cannot open file \"" + mFileName + "\"...");
			e.printStackTrace();
		} catch (IOException e) {
			Log.i(TAG, "file \"" + mFileName
					+ "\" is not found in the remote location...");
			e.printStackTrace();
		}

	}

	/**
	 * unzip the file after downloadign it.
	 * 
	 */
	private void unzipFile() {
		File folder = new File(mContext.getCacheDir(), EXTRACTED_FILES_PATH);
		if (!folder.exists()) {
			folder.mkdir();
		}

		try {
			// create new folder with the same name of the file without .zip
			File newExtractedFolder = new File(folder, mFileName.substring(0,
					mFileName.lastIndexOf(".")));
			if (!newExtractedFolder.exists()) {
				newExtractedFolder.mkdir();
			}

			ZipInputStream zis = new ZipInputStream(new FileInputStream(
					new File(mContext.getCacheDir(), mFileName)));

			ZipEntry ze = zis.getNextEntry();

			while (ze != null) {

				String fileName = ze.getName();

				File newFile = new File(newExtractedFolder, fileName);
				// create parent directories

				Log.i(TAG, "mkdirs : " + fileName);

				// incase the .zip file contains file (not folder) in its main
				// location
				if (fileName.contains(File.separator)) {
					createDirs(
							newExtractedFolder,
							fileName.substring(0,
									fileName.lastIndexOf(File.separator)));
				}

				// if the entry is a file extract it
				if (ze.isDirectory()) {
					Log.i(TAG, "this entry is a folder: \"" + ze.getName()
							+ "\"");
					ze = zis.getNextEntry();
					continue;
				} else {
					Log.i(TAG, "file unzip : " + newFile.getAbsolutePath());

					FileOutputStream fos = new FileOutputStream(newFile);

					int len;
					// int total = 0;
					while ((len = zis.read(buffer)) > 0) {
						// total += len;
						// publishProgress((int) ((total * 100) /
						// mLenghtOfFile));
						fos.write(buffer, 0, len);
					}

					fos.close();
					ze = zis.getNextEntry();
				}
			}

			zis.closeEntry();
			zis.close();
		} catch (FileNotFoundException e) {
			Log.e(TAG, "*.zip file is not found on the internal stroage");
			e.printStackTrace();
		} catch (IOException e) {
			Log.e(TAG, "*.zip file is not valid.");
			e.printStackTrace();
		}
	}

	private boolean createDirs(File parent, String path) {
		String[] dirs = path.split(File.separator);
		boolean returnVal = true;
		for (String dir : dirs) {
			File newDir = new File(parent, dir);
			if (!newDir.exists()) {
				returnVal |= newDir.mkdir();
			}
			parent = newDir;
		}
		return returnVal;

	}

}
