package com.matics.command;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import com.matics.Bluetooth.BluetoothApp;
import com.matics.MainActivity;

import android.util.Log;

public class ObdCommand extends Thread {

	protected InputStream in = null;
	protected OutputStream out = null;
	protected ArrayList<Byte> buff = null;
	protected String cmd = null;
	protected String desc = null;
	protected String resType = null;
	protected Exception error;

	public ObdCommand(String cmd, String desc, String resType) {
		this.cmd = cmd;
		this.desc = desc;
		this.resType = resType;
		this.buff = new ArrayList<Byte>();
	}

	public ObdCommand(ObdCommand other) {
		this(other.cmd, other.desc, other.resType);
	}

	public void setInputStream(InputStream in) {
		this.in = in;
	}

	public void setOutputStream(OutputStream out) {
		this.out = out;
	}

	public void run() {
		sendCmd(cmd);
		/*try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		readResult();
	}
	protected void sendCmd(String cmd) {
		try {
//			//Log.e("", "cmd is:" + cmd);
			cmd += "\r";
			byte[] bytes = cmd.getBytes();
			out.write(bytes);
			out.flush();
		} catch (Exception e) {
			//Log.dMainActivity.TAG,"Something happpened heeeere");
			e.printStackTrace();
			setAppDisconnected();
//             CommsThread.stop=true;
//             CommsThread.isConnected=false;
//             BluetoothApp.stopConnectionManager();
//             ConnectionManagerThread.cleanAllCommunicationThreads();
//			//Log.i("", "Error in is :" + e.getMessage());
//			try {
//				MyService.sock.close();
//			} catch (IOException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
			// TODO: handle exception
		}
	}
	protected void readResult() {
		byte c = 0;
		this.buff.clear();
		if(in!=null){

			try {
				while ((char) (c = (byte) in.read()) != '>') {
					buff.add(c);
				}
			} catch (IOException e) {
				e.printStackTrace();
				setAppDisconnected();
			}
		}else{
			//Log.e("", "Input Stream is null");
			BluetoothApp.isConnectionEstablished=false;
		}
		
	}
	
	public void setAppDisconnected(){
		 //Log.dMainActivity.TAG,"Closing Communication after send cmd ioException happened");
        BluetoothApp.isConnectionEstablished=false;
        if(BluetoothApp.connectToServerThread!=null){
        BluetoothApp.connectToServerThread.cancel();
        BluetoothApp.connectToServerThread=null;
        }
        
        if(BluetoothApp.commsThread!=null){
        BluetoothApp.commsThread.cancel();
        BluetoothApp.commsThread =null;
        }
        
        BluetoothApp.isOBD2LockingPreventionRequired=true;
	}

	public String getResult() {
		return new String(getByteArray());
	}

	public byte[] getByteArray() {
		byte[] data = new byte[this.buff.size()];
		for (int i = 0; i < this.buff.size(); i++) {
			data[i] = this.buff.get(i);
		}
		return data;
	}

	public String formatResult() {
		String res = getResult();
		/*String[] ress = res.split("\r\n");
		res = ress[1].replace(" ","");
		//Log.e("", "res is:" + res);*/
		res = res.replace("\r", "");
		res = res.replace("SEARCHING...", "");
		return res;
	}

	public InputStream getIn() {
		return in;
	}

	public OutputStream getOut() {
		return out;
	}

	public ArrayList<Byte> getBuff() {
		return buff;
	}

	public String getCmd() {
		return cmd;
	}

	public String getDesc() {
		return desc;
	}

	public String getResType() {
		return resType;
	}

	public void setError(Exception e) {
		error = e;
	}

	public Exception getError() {
		return error;
	}
}
